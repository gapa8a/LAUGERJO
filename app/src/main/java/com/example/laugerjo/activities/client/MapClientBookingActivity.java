package com.example.laugerjo.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.laugerjo.R;
import com.example.laugerjo.providers.GeofireProvider;
import com.example.laugerjo.providers.TokenProvider;
import com.example.laugerjo.providers.authProviders;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.ArrayList;
import java.util.List;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Mapa;
    private SupportMapFragment Mapafragmento;



    private GeofireProvider geofireProvider;
    private com.example.laugerjo.providers.TokenProvider TokenProvider;

    private final static int  LOCATION_REQUEST_CODE = 1;

    private final static int  SETTINGS_REQUEST_CODE = 2;
    private authProviders Aunteti;

    private Marker marker;
    private LatLng currentLatlng;



    private List<Marker> driversMarkers = new ArrayList<>();

    private boolean isFirstTime = true;
    private PlacesClient placesClient;


    private String origin;
    private  LatLng originLatLng;

    private String destination;
    private  LatLng destinationLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);

        Aunteti = new authProviders();

        Mapafragmento= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Mapafragmento.getMapAsync(this);
        geofireProvider = new GeofireProvider("_drivers");
        TokenProvider = new TokenProvider();


        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mapa = googleMap;
        Mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Mapa.getUiSettings().setZoomControlsEnabled(true);
        Mapa.setMyLocationEnabled(true);


    }

}
