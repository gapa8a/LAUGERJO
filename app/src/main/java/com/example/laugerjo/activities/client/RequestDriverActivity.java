package com.example.laugerjo.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.laugerjo.R;
import com.example.laugerjo.providers.GeofireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;

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
}
