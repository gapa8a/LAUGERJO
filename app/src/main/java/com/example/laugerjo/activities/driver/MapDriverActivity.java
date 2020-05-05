package com.example.laugerjo.activities.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.pantallaInicial;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.providers.authProviders;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapDriverActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap Mapa;
    private SupportMapFragment Mapafragmento;
     private authProviders Aunteti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver);
        Aunteti = new authProviders();
        toolbar.show(this,"Conductor",false);

        Mapafragmento= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Mapafragmento.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mapa = googleMap;
        Mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.action_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
    void logout(){
        Aunteti.logout();
        Intent intent =new Intent(MapDriverActivity.this, pantallaInicial.class);
        startActivity(intent);
        finish();

    }
}
