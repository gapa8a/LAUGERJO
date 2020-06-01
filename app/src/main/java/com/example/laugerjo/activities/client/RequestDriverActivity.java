package com.example.laugerjo.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.laugerjo.R;
import com.example.laugerjo.model.ClientBooking;
import com.example.laugerjo.model.FCMBody;
import com.example.laugerjo.model.FCMResponse;
import com.example.laugerjo.model.Token;
import com.example.laugerjo.providers.ClientBookingProvider;
import com.example.laugerjo.providers.GeofireProvider;
import com.example.laugerjo.providers.GoogleApiProvider;
import com.example.laugerjo.providers.NotificationProvider;
import com.example.laugerjo.providers.TokenProvider;
import com.example.laugerjo.providers.authProviders;
import com.example.laugerjo.utils.DecodePoints;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.AuthProvider;
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

    private String extraOrigin;
    private String extraDestination;

    private  double extraOriginLat;
    private  double extraOriginLng;

    private  double extraDestinationLat;
    private  double extraDestinationLng;
    private LatLng originLatLng;

    private LatLng destinationLatLng;

    private double Radius = 0.1;
    private boolean driverFound = false;
    private String  idDriverFound ="";
    private LatLng driverFoundLatLng;

    private NotificationProvider notificationProvider;
    private TokenProvider tokenProvider;
    private authProviders authProviders;
    private GoogleApiProvider googleApiProvider;

    private ClientBookingProvider clientBookingProvider;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);
        animation = findViewById(R.id.animation);
        txtLookFor = findViewById(R.id.txtLookFor);
        btnCancel = findViewById(R.id.btnCancel);
        geofireProvider = new GeofireProvider("active_drivers");

        animation.playAnimation();

        extraOrigin = getIntent().getStringExtra("origin");
        extraDestination = getIntent().getStringExtra("destination");
        extraOriginLat = getIntent().getDoubleExtra("origin_lat",0);
        extraOriginLng = getIntent().getDoubleExtra("origin_lng",0);

        extraDestinationLat = getIntent().getDoubleExtra("destination_lat",0);
        extraDestinationLng = getIntent().getDoubleExtra("destination_lng",0);

        originLatLng = new LatLng(extraOriginLat,extraOriginLng);
        destinationLatLng = new LatLng(extraDestinationLat,extraDestinationLng);

        notificationProvider = new NotificationProvider();
        tokenProvider = new TokenProvider();
        clientBookingProvider = new ClientBookingProvider();
        authProviders = new authProviders();

        googleApiProvider = new GoogleApiProvider(RequestDriverActivity.this);


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
                 createClientBooking();
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

    private void createClientBooking(){ // acá debemos mirar lo del precio



        googleApiProvider.getDirections(originLatLng,driverFoundLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg =  legs.getJSONObject(0);
                    JSONObject distance =  leg.getJSONObject("distance");
                    JSONObject duration =  leg.getJSONObject("duration");
                    String distanceText =distance.getString("text");
                    String durationText =duration.getString("text");
                    sendNotification(durationText ,distanceText);


                }catch (Exception e){
                    Log.d("Error","Error encontrado " +e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });



    }


    private void sendNotification(final String time , final String km/*, final String price */) {
        tokenProvider.getToken(idDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title","SOLICITUD DE SERVICIO A " +time+" DE TU POSICIÓN");
                    map.put("body","Un cliente esta solicitando un servicio a una distancia de " +km+ "\n"+
                            "Recoger en: " +extraOrigin+ "\n" +
                            "Destino: "+extraDestination+
                            "Precio: "/*+price*/ );
                    map.put("idClient",authProviders.getId());
                    map.put("origin",extraOrigin);
                    map.put("destination",extraDestination);
                    //map.put("price",extraPrice);
                    map.put("min",time);
                    map.put("distance",km);
                    FCMBody fcmBody = new FCMBody(token,"high","4500s",map);
                    notificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body() !=null){
                                if(response.body().getSuccess() == 1 ){
                                    ClientBooking clientBooking = new ClientBooking(authProviders.getId(),
                                            idDriverFound,
                                            extraDestination,
                                            extraOrigin,
                                            time,
                                            km,
                                            /*price,*/
                                            "create",
                                            extraOriginLat,
                                            extraOriginLng,
                                            extraDestinationLat,
                                            extraDestinationLng
                                    );
                                    clientBookingProvider.create(clientBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            checkStatusClientBooking();
                                        }
                                    });
                                }else{
                                    Toast.makeText(RequestDriverActivity.this, "No se pudo enviar la Notificación", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(RequestDriverActivity.this, "No se pudo enviar la Notificación", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error" ,"Error: " +t.getMessage());
                        }
                    });
                }else{
                    Toast.makeText(RequestDriverActivity.this, "No se pudo enviar la notificación porque el conductor no tiene un token de sesión.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkStatusClientBooking() {
        listener= clientBookingProvider.getStatus(authProviders.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String status = dataSnapshot.getValue().toString();
                    if(status.equals("accept")){
                        Toast.makeText(RequestDriverActivity.this, "El conductor  acepto el viaje", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RequestDriverActivity.this,MapClientBookingActivity.class);
                        startActivity(intent);
                        finish();
                    }else if (status.equals("cancel")){
                        Toast.makeText(RequestDriverActivity.this, "El conductor no acepto el viaje", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RequestDriverActivity.this,MapClientActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(listener != null){
        clientBookingProvider.getStatus(authProviders.getId()).removeEventListener(listener);}
    }
}
