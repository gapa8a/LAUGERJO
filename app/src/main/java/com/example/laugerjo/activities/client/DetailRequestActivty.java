package com.example.laugerjo.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.laugerjo.R;
import com.example.laugerjo.includes.toolbar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailRequestActivty extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Mapa;
    private SupportMapFragment Mapafragmento;
    private double ExtraOriginLat;
    private double ExtraOriginLng;
    private double ExtraDestinationLat;
    private double ExtraDestinationLng;

    private LatLng OriginLatLng;
    private LatLng DestinationLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request_activty);
        toolbar.show(this,"Viaje actual",true);
        Mapafragmento= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Mapafragmento.getMapAsync(this);

        ExtraOriginLat=getIntent().getDoubleExtra("origin_lat",0);
        ExtraOriginLng=getIntent().getDoubleExtra("origin_lng",0);
        ExtraDestinationLat=getIntent().getDoubleExtra("destination_lat",0);
        ExtraDestinationLng=getIntent().getDoubleExtra("destination_lng",0);

        OriginLatLng = new LatLng(ExtraOriginLat,ExtraOriginLng);
        DestinationLatLng = new LatLng(ExtraDestinationLat,ExtraDestinationLng);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mapa = googleMap;
        Mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Mapa.getUiSettings().setZoomControlsEnabled(true);

        Mapa.addMarker(new MarkerOptions().position(OriginLatLng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red)));
        Mapa.addMarker(new MarkerOptions().position(DestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_green)));
        Mapa.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(OriginLatLng).zoom(14f).build()
        ));
    }
}
