package com.example.laugerjo.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.driver.MapDriverBookingActivity;
import com.example.laugerjo.model.ClientBooking;
import com.example.laugerjo.providers.ClientBookingProvider;
import com.example.laugerjo.providers.DriverProvider;
import com.example.laugerjo.providers.GeofireProvider;
import com.example.laugerjo.providers.GoogleApiProvider;
import com.example.laugerjo.providers.TokenProvider;
import com.example.laugerjo.providers.authProviders;
import com.example.laugerjo.utils.DecodePoints;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Mapa;
    private SupportMapFragment Mapafragmento;



    private GeofireProvider geofireProvider;
    private TokenProvider TokenProvider;
    private ClientBookingProvider clientBookingProvider;
    private DriverProvider driverProvider;

    private authProviders Aunteti;

    private Marker markerDriver;






    private boolean isFirstTime = true;
    private PlacesClient placesClient;

    private TextView txtClientBooking;
    private TextView txtEmailBooking;

    private TextView txtOriginBooking;
    private TextView txtDestinationBooking;
    private TextView txtPriceBooking;
    private TextView txtStatusBooking;

    private ImageView imgDriverBooking;
    private String origin;
    private  LatLng originLatLng;

    private String destination;
    private  LatLng destinationLatLng;
    private  LatLng driverLatlng;

    private GoogleApiProvider googleApiProvider;

    private List<LatLng> PolylineList;
    private PolylineOptions PolylineOptions;
    private ValueEventListener listener;
    private String midDriver;
    private ValueEventListener listenerStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);

        Aunteti = new authProviders();

        Mapafragmento= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Mapafragmento.getMapAsync(this);
        geofireProvider = new GeofireProvider("drivers_working");
        TokenProvider = new TokenProvider();
        clientBookingProvider = new ClientBookingProvider();
        googleApiProvider = new GoogleApiProvider(MapClientBookingActivity.this);
        driverProvider = new DriverProvider();



        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        }

        txtClientBooking = findViewById(R.id.txtDriverBooking);
        txtEmailBooking = findViewById(R.id.txtEmailDriverBooking);
        txtPriceBooking = findViewById(R.id.txtPriceBooking);

        txtStatusBooking = findViewById(R.id.txtStatusBooking);
        txtOriginBooking = findViewById(R.id.txtOriginDriverBooking);
        txtDestinationBooking = findViewById(R.id.txtDestinationDriverBooking);
        imgDriverBooking = findViewById(R.id.imgViewClientBooking);
        getStatus();
    getClientBooking();
    }

    private void getStatus() {
        listenerStatus= clientBookingProvider.getStatus(Aunteti.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String status =dataSnapshot.getValue().toString();

                    if(status.equals("accept")){
                        txtStatusBooking.setText("Estado: Aceptado");
                    }
                    if(status.equals("start")){
                        txtStatusBooking.setText("Estado: Viaje Iniciado");
                        startBooking();
                    }else if (status.equals("finish")){
                        txtStatusBooking.setText("Estado: Finalizado");
                        finishBooking();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void finishBooking() {
        Intent intent = new Intent(MapClientBookingActivity.this,CalificationDriverActivity.class );
        startActivity(intent);
        finish();
    }

    private void startBooking() {
        Mapa.clear();
        Mapa.addMarker(new MarkerOptions().position(destinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_green)));
        drawRoute(destinationLatLng);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listener!=null){
            geofireProvider.getDriverLocation(midDriver).removeEventListener(listener);
        }
        if(listenerStatus != null){
            clientBookingProvider.getStatus(Aunteti.getId()).removeEventListener(listenerStatus);
        }
    }

    private void getClientBooking() {
        clientBookingProvider.getClientBooking(Aunteti.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String destination = dataSnapshot.child("destination").getValue().toString();
                    //String price = dataSnapshot.child("price").getValue().toString();
                    String origin= dataSnapshot.child("origin").getValue().toString();
                    String idDriver = dataSnapshot.child("idDriver").getValue().toString();
                    midDriver = idDriver;
                    double destinationLat = Double.parseDouble(dataSnapshot.child("destinationLat").getValue().toString());
                    double destinationLng = Double.parseDouble(dataSnapshot.child("destinationLng").getValue().toString());
                    double originLat = Double.parseDouble(dataSnapshot.child("originLat").getValue().toString());
                    double originLng = Double.parseDouble(dataSnapshot.child("originLng").getValue().toString());
                    originLatLng = new LatLng(originLat, originLng);
                    destinationLatLng = new LatLng(destinationLat,destinationLng);
                    txtOriginBooking.setText("Recoger en: "+origin);
                    txtDestinationBooking.setText("Destino: "+destination);
                    //txtPriceBooking.setText(price);
                    Mapa.addMarker(new MarkerOptions().position(originLatLng).title("Recoger Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red)));
                    getDriver(idDriver);
                    getDriverLocation(idDriver);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void  getDriver(String idDriver){
         driverProvider.getDriver(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String lastname=dataSnapshot.child("lastname").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String image="";
                    if(dataSnapshot.hasChild("image")){
                        image=dataSnapshot.child("image").getValue().toString();
                        Picasso.with(MapClientBookingActivity.this).load(image).into(imgDriverBooking);
                    }
                    //String price = dataSnapshot.child("price").getValue().toString();
                    String fullname ="Conductor: "+name+" "+lastname;
                    txtClientBooking.setText(fullname);
                    txtEmailBooking.setText("Email: " +email);
                    //txtPriceBooking.setText("$"price);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDriverLocation(String idDriver) {
        listener =geofireProvider.getDriverLocation(idDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    double lat = Double.parseDouble(dataSnapshot.child("0").getValue().toString());
                    double lng = Double.parseDouble(dataSnapshot.child("1").getValue().toString());
                    driverLatlng = new LatLng(lat,lng);
                    if(markerDriver != null){
                        markerDriver.remove();
                    }
                    markerDriver= Mapa.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title("Tu conductor").icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_condu)));
                    if(isFirstTime){
                        isFirstTime = false;
                        Mapa.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder().target(driverLatlng).zoom(14f).build()
                        ));
                        drawRoute(originLatLng);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mapa = googleMap;
        Mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Mapa.getUiSettings().setZoomControlsEnabled(true);
        Mapa.setMyLocationEnabled(true);


    }

    private void drawRoute(LatLng latLng){
        googleApiProvider.getDirections(driverLatlng, latLng).enqueue(new Callback<String>() {
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

}
