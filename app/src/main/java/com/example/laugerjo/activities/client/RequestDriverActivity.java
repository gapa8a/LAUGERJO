package com.example.laugerjo.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.laugerjo.R;
import com.example.laugerjo.model.FCMBody;
import com.example.laugerjo.model.FCMResponse;
import com.example.laugerjo.model.Token;
import com.example.laugerjo.providers.GeofireProvider;
import com.example.laugerjo.providers.NotificationProvider;
import com.example.laugerjo.providers.TokenProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDriverActivity extends AppCompatActivity {

    private LottieAnimationView animation;
    private TextView txtLookFor;
    private Button  btnCancel;
    private GeofireProvider geofireProvider;

    private  double extraOriginLat;
    private  double extraOriginLng;
    private LatLng originLatLng;

    private double Radius = 0.1;
    private boolean driverFound = false;
    private String  idDriverFound ="";
    private LatLng driverFoundLatLng;

    private NotificationProvider notificationProvider;
    private TokenProvider tokenProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);
        animation = findViewById(R.id.animation);
        txtLookFor = findViewById(R.id.txtLookFor);
        btnCancel = findViewById(R.id.btnCancel);
        geofireProvider = new GeofireProvider();

        animation.playAnimation();
        extraOriginLat = getIntent().getDoubleExtra("origin_lat",0);
        extraOriginLng = getIntent().getDoubleExtra("origin_lng",0);
        originLatLng = new LatLng(extraOriginLat,extraOriginLng);

        notificationProvider = new NotificationProvider();
        tokenProvider = new TokenProvider();
        getClosestDriver();
    }
    private  void getClosestDriver(){
        geofireProvider.getActiveDrivers(originLatLng,Radius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
             if (!driverFound){
                 driverFound = true;
                 idDriverFound = key;
                 driverFoundLatLng = new LatLng(location.latitude,location.longitude);
                 txtLookFor.setText("SE ENCONTRO EL CONDUCTOR\nESPERANDO RESPUESTA");
                 sendNotification();
                 Log.d("DRIVER","ID: " +idDriverFound);
             }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //Ingresa cuando termina la busqueda del conductores en un radio de 0.1 KM
                if(!driverFound){
                    Radius =Radius + 0.1f;
                    //NO ENCONTRO NINGUN CONDUCTOR
                    if(Radius > 5){
                        txtLookFor.setText("NO SE ENCONTRÓ UN CONDUCTOR");
                        //Toast.makeText(RequestDriverActivity.this, "NO SE ENCONTRÓ UN CONDUCTOR",Toast.LENGTH_LONG).show();
                        return;
                    }else{
                        getClosestDriver();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void sendNotification() {
        tokenProvider.getToken(idDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String token = dataSnapshot.child("token").getValue().toString();
                Map<String, String> map = new HashMap<>();
                map.put("title","SOLICITUD DE SERVICIO");
                map.put("body","Un cliente esta solicitando un servicio");
                FCMBody fcmBody = new FCMBody(token,"high",map);
                notificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body() !=null){
                            if(response.body().getSuccess() == 1 ){
                                Toast.makeText(RequestDriverActivity.this, "La notificación se envio ", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(RequestDriverActivity.this, "No se pudo enviar la Notificación", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                        Log.d("Error" ,"Error: " +t.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
