package com.example.laugerjo.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {
    private DatabaseReference DB;
    private GeoFire geoFire;
    public GeofireProvider(){
        DB = FirebaseDatabase.getInstance().getReference().child("active_drivers");
        geoFire = new GeoFire(DB);
    }

    public void saveLocation(String idDriver, LatLng latLng){
        geoFire.setLocation(idDriver,new GeoLocation(latLng.latitude, latLng.longitude));
    }
    public void removeLocation(String idDriver){
        geoFire.removeLocation(idDriver);
    }
}
