package com.example.laugerjo.activities.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import com.example.laugerjo.R;
import com.example.laugerjo.providers.GeofireProvider;
import com.example.laugerjo.providers.TokenProvider;
import com.example.laugerjo.providers.authProviders;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapDriverBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Mapa;
    private SupportMapFragment Mapafragmento;
    private authProviders Aunteti;
    private GeofireProvider geofireProvider;
    private com.example.laugerjo.providers.TokenProvider TokenProvider;


    private LocationRequest locationRequest;
    private FusedLocationProviderClient FusedLocation;



    private final static int  LOCATION_REQUEST_CODE = 1;

    private final static int  SETTINGS_REQUEST_CODE = 2;

    private Marker marker;
    private LatLng currentLatlng;
    com.google.android.gms.location.LocationCallback LocationCallback =new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult){
            for (Location location:locationResult.getLocations()){
                if(getApplicationContext() != null){
                    // Obtener la localización del usuario en tiempo real
                    currentLatlng = new LatLng(location.getLatitude(),location.getLongitude());
                    if(marker !=null){
                        marker.remove();
                    }
                    currentLatlng = new LatLng(location.getLatitude(),location.getLongitude());

                    marker= Mapa.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                    ).title("Tu posición").icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_condu)));

                    Mapa.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder().target(new LatLng(location.getLatitude(),location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));
                    updateLocation();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver_booking);

        Aunteti = new authProviders();
        geofireProvider = new GeofireProvider("drivers_working");
        TokenProvider = new TokenProvider();


        Mapafragmento= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Mapafragmento.getMapAsync(this);
        FusedLocation = LocationServices.getFusedLocationProviderClient(this);
    }

    private void updateLocation(){
        if(Aunteti.existSession() && currentLatlng !=null){
            geofireProvider.saveLocation(Aunteti.getId(), currentLatlng);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mapa = googleMap;
        Mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Mapa.getUiSettings().setZoomControlsEnabled(true);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);

        startLocation();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived() ){
            FusedLocation.requestLocationUpdates(locationRequest, LocationCallback, Looper.myLooper());
            Mapa.setMyLocationEnabled(true); // ubicación con el punto azul = true
        }else{
            showAlertDialogNOGPS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    if(gpsActived()){
                        FusedLocation.requestLocationUpdates(locationRequest, LocationCallback, Looper.myLooper());
                        Mapa.setMyLocationEnabled(true); // ubicación con el punto azul = true
                    }else{
                        showAlertDialogNOGPS();
                    }
                } else{
                    checkLocationsPermissions();
                }
            }
            else{
                checkLocationsPermissions();

            }
        }
    }

    private void showAlertDialogNOGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor active su ubicacion GPS para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult( new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActived(){
        boolean isActive = false;
        LocationManager locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive = true;
        }
        return isActive;
    }

    private void disconnect(){

        if(FusedLocation !=null){
            FusedLocation.removeLocationUpdates(LocationCallback);
            if(Aunteti.existSession()){
                geofireProvider.removeLocation(Aunteti.getId());
            }
        } else{
            Toast.makeText(this,"No te puedes desconectar",Toast.LENGTH_SHORT).show();
        }
    }
    private void startLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {

                    FusedLocation.requestLocationUpdates(locationRequest, LocationCallback, Looper.myLooper());
                    Mapa.setMyLocationEnabled(true); // ubicación con el punto azul = true
                } else{
                    showAlertDialogNOGPS();
                }
            }
            else{
                checkLocationsPermissions();
            }
        } else {
            if(gpsActived()){
                FusedLocation.requestLocationUpdates(locationRequest, LocationCallback, Looper.myLooper());
                Mapa.setMyLocationEnabled(true); // ubicación con el punto azul = true
            } else{
                showAlertDialogNOGPS();
            }


        }
    }
    private void checkLocationsPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this).setTitle("Proporcionar los permisos para continuar")
                        .setMessage("Esta aplicación requiere de los permisos de ubicación para poder utilizarse")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapDriverBookingActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                            }
                        }).create().show();
            }else {
                ActivityCompat.requestPermissions(MapDriverBookingActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

            }
        }
    }
}
