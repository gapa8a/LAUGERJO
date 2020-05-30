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
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.client.DetailRequestActivty;
import com.example.laugerjo.activities.client.RequestDriverActivity;
import com.example.laugerjo.model.ClientBooking;
import com.example.laugerjo.model.FCMBody;
import com.example.laugerjo.model.FCMResponse;
import com.example.laugerjo.providers.ClientBookingProvider;
import com.example.laugerjo.providers.ClientProvider;
import com.example.laugerjo.providers.GeofireProvider;
import com.example.laugerjo.providers.GoogleApiProvider;
import com.example.laugerjo.providers.NotificationProvider;
import com.example.laugerjo.providers.TokenProvider;
import com.example.laugerjo.providers.authProviders;
import com.example.laugerjo.utils.DecodePoints;
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
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapDriverBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Mapa;
    private SupportMapFragment Mapafragmento;
    private authProviders Aunteti;
    private GeofireProvider geofireProvider;
    private com.example.laugerjo.providers.TokenProvider TokenProvider;

    private NotificationProvider notificationProvider;

    private ClientProvider clientProvider;
    private ClientBookingProvider clientBookingProvider;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient FusedLocation;



    private final static int  LOCATION_REQUEST_CODE = 1;

    private final static int  SETTINGS_REQUEST_CODE = 2;

    private Marker marker;
    private LatLng currentLatlng;

    private TextView txtClientBooking;
    private TextView txtEmailBooking;

    private TextView txtOriginBooking;
    private TextView txtDestinationBooking;
    private TextView txtPriceBooking;

    private String extraClientId;
    private LatLng originLatLng;
    private LatLng destinationLatLng;

    private GoogleApiProvider googleApiProvider;

    private List<LatLng> PolylineList;
    private PolylineOptions PolylineOptions;
    private Boolean isFirstTime = true;
    private Boolean isCloseToClient = false;
    private Button btnStartBooking, btnFinishBooking;

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

                    if (isFirstTime){
                        isFirstTime = false;
                        getClientBooking();
                    }
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
        clientProvider = new ClientProvider();
        clientBookingProvider = new ClientBookingProvider();
        FusedLocation = LocationServices.getFusedLocationProviderClient(this);

        Mapafragmento= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Mapafragmento.getMapAsync(this);

        txtClientBooking = findViewById(R.id.txtClientBooking);
        txtEmailBooking = findViewById(R.id.txtEmailBooking);
        txtPriceBooking = findViewById(R.id.txtPriceBooking);

        notificationProvider = new NotificationProvider();

        txtOriginBooking = findViewById(R.id.txtOriginClientBooking);
        txtDestinationBooking = findViewById(R.id.txtDestinationClientBooking);

        btnStartBooking=findViewById(R.id.btnStartBooking);
        btnFinishBooking=findViewById(R.id.btnFinishBooking);

        //btnStartBooking.setEnabled(false);

        extraClientId =getIntent().getStringExtra("idClient");
        googleApiProvider = new GoogleApiProvider(MapDriverBookingActivity.this);

        getClient();
        btnStartBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCloseToClient){
                    startBooking();
                }else{
                    Toast.makeText(MapDriverBookingActivity.this, "Debes estar mas cerca a la posición de recogida", Toast.LENGTH_SHORT).show();
                }


            }
        });
        btnFinishBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBooking();
            }
        });
    }

    private void finishBooking() {
        clientBookingProvider.updateStatus(extraClientId,"finish");
        sendNotification("Viaje Finalizado");
        if (FusedLocation != null){
            FusedLocation.removeLocationUpdates(LocationCallback);
        }
        geofireProvider.removeLocation(Aunteti.getId());
        Intent  intent  = new Intent(MapDriverBookingActivity.this,CalificationClientActivity.class);
        startActivity(intent);
        finish();
    }

    private void startBooking() {
        clientBookingProvider.updateStatus(extraClientId, "start");
        btnStartBooking.setVisibility(View.GONE);
        btnFinishBooking.setVisibility(View.VISIBLE);
        Mapa.clear();
        Mapa.addMarker(new MarkerOptions().position(destinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_green)));
        drawRoute(destinationLatLng);
        sendNotification("Viaje iniciado");
    }
    private double getDistanceBetween(LatLng clientLatlng,LatLng driverLatlng){
        double distance = 0;
        Location clientLocation = new Location("");
        Location driverLocation = new Location("");
        clientLocation.setLatitude(clientLatlng.latitude);
        clientLocation.setLongitude(clientLatlng.longitude);
        driverLocation.setLatitude(driverLatlng.latitude);
        driverLocation.setLongitude(driverLatlng.longitude);
        distance = clientLocation.distanceTo(driverLocation);
        return distance;
    }

    private void getClientBooking() {
        clientBookingProvider.getClientBooking(extraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                String destination = dataSnapshot.child("destination").getValue().toString();
                //String price = dataSnapshot.child("price").getValue().toString();
                String origin= dataSnapshot.child("origin").getValue().toString();
                double destinationLat = Double.parseDouble(dataSnapshot.child("destinationLat").getValue().toString());
                double destinationLng = Double.parseDouble(dataSnapshot.child("destinationLng").getValue().toString());

                double originLat = Double.parseDouble(dataSnapshot.child("originLat").getValue().toString());
                double originLng = Double.parseDouble(dataSnapshot.child("originLng").getValue().toString());

                originLatLng = new LatLng(originLat, originLng);
                destinationLatLng = new LatLng(destinationLat,destinationLng);

                txtOriginBooking.setText("Recoger en: "+origin);
                txtDestinationBooking.setText("Destino: "+destination);
                //txtPriceBooking.setText(price);

                Mapa.addMarker(new MarkerOptions().position(originLatLng).title("Recoger Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_green)));
                drawRoute(originLatLng);
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }
        );
    }

    private void drawRoute(LatLng latLng){
        googleApiProvider.getDirections(currentLatlng,latLng ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    PolylineList = DecodePoints.decodePoly(points);
                    PolylineOptions = new PolylineOptions();
                    PolylineOptions.color(Color.DKGRAY);
                    PolylineOptions.width(13f);
                    PolylineOptions.startCap( new SquareCap());
                    PolylineOptions.jointType(JointType.ROUND);
                    PolylineOptions.addAll(PolylineList);
                    Mapa.addPolyline(PolylineOptions);

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg =  legs.getJSONObject(0);
                    JSONObject distance =  leg.getJSONObject("distance");
                    JSONObject duration =  leg.getJSONObject("duration");
                    String distanceText =distance.getString("text");
                    String durationText =duration.getString("text");

                }catch (Exception e){
                    Log.d("Error","Error encontrado " +e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void getClient() {
        clientProvider.getClient(extraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String email=dataSnapshot.child("email").getValue().toString();
                    String name=dataSnapshot.child("name").getValue().toString();
                    String lastname=dataSnapshot.child("lastname").getValue().toString();
                    //String price = dataSnapshot.child("price").getValue().toString();
                    String fullname ="Cliente: "+name+" "+lastname;
                    txtClientBooking.setText(fullname);
                    txtEmailBooking.setText("Email: "+email);
                    //txtPriceBooking.setText("$"price);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateLocation(){
        if(Aunteti.existSession() && currentLatlng !=null){
            geofireProvider.saveLocation(Aunteti.getId(), currentLatlng);
            if (!isCloseToClient){
                if(originLatLng !=null && currentLatlng!=null) {
                    double distance = getDistanceBetween(originLatLng,currentLatlng);// la distancia sera en metros
                    if(distance <= 200 ){
                       // btnStartBooking.setEnabled(true);
                        isCloseToClient = true;
                        Toast.makeText(this, "Estas cerca a la posición de recogida ", Toast.LENGTH_SHORT).show();
                    }
                }

            }

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

    private void sendNotification(final String status) {
        TokenProvider.getToken(extraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title","ESTADO DE TU VIAJE");
                    map.put("body",
                            "Tu estado del viaje es: "+status);
                    FCMBody fcmBody = new FCMBody(token,"high","4500s",map);
                    notificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body() !=null){
                                if(response.body().getSuccess() != 1 ){
                                    Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la Notificación", Toast.LENGTH_SHORT).show();
                                }

                        }else{
                                Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la Notificación", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error" ,"Error: " +t.getMessage());
                        }
                    });
                }else{
                    Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la notificación porque el conductor no tiene un token de sesión.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
