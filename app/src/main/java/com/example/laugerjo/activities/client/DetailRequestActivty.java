package com.example.laugerjo.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.laugerjo.R;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.providers.GoogleApiProvider;
import com.example.laugerjo.utils.DecodePoints;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRequestActivty extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Mapa;
    private SupportMapFragment Mapafragmento;
    private double ExtraOriginLat;
    private double ExtraOriginLng;
    private double ExtraDestinationLat;
    private double ExtraDestinationLng;

    private String ExtraOrigin;
    private String ExtraDestination;

    private LatLng OriginLatLng;
    private LatLng DestinationLatLng;

    private GoogleApiProvider googleApiProvider;

    private List<LatLng> PolylineList;
    private PolylineOptions PolylineOptions;

    private TextView txtOrigin,txtDestination,txtTiempoV,txtDistancia;
    private Button buttonRequest;

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
        ExtraOrigin = getIntent().getStringExtra("origin");
        ExtraDestination = getIntent().getStringExtra("destination");
        OriginLatLng = new LatLng(ExtraOriginLat,ExtraOriginLng);
        DestinationLatLng = new LatLng(ExtraDestinationLat,ExtraDestinationLng);

        googleApiProvider = new GoogleApiProvider(DetailRequestActivty.this);

        txtOrigin =findViewById(R.id.txtOrigin);
        txtDestination =findViewById(R.id.txtDestination);
        txtTiempoV =findViewById(R.id.txtTiempoV);
        txtDistancia =findViewById(R.id.txtDistancia);
        buttonRequest = findViewById(R.id.btnRequestNow);
        txtOrigin.setText(ExtraOrigin);
        txtDestination.setText(ExtraDestination);
     buttonRequest.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             goToRequestDriver();
         }
     });
    }

    private void goToRequestDriver() {
        Intent intent = new Intent(DetailRequestActivty.this , RequestDriverActivity.class);
        intent.putExtra("origin_lat",OriginLatLng.latitude);
        intent.putExtra("origin_lng",OriginLatLng.longitude);
        startActivity(intent);
        finish();
    }

    private void drawRoute(){
        googleApiProvider.getDirections(OriginLatLng,DestinationLatLng).enqueue(new Callback<String>() {
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
                    txtTiempoV.setText(durationText);
                    txtDistancia.setText(distanceText);
                }catch (Exception e){
                    Log.d("Error","Error encontrado " +e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
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

        drawRoute();
    }
}
