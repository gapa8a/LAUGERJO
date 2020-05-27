package com.example.laugerjo.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {
    private DatabaseReference DB;
    private GeoFire geoFire;
    public GeofireProvider(String reference){
        DB = FirebaseDatabase.getInstance().getReference().child(reference);
        geoFire = new GeoFire(DB);
    }

    public void saveLocation(String idDriver, LatLng latLng){
        geoFire.setLocation(idDriver,new GeoLocation(latLng.latitude, latLng.longitude));
    }
    public void removeLocation(String idDriver){
        geoFire.removeLocation(idDriver);
    }
    public GeoQuery getActiveDrivers(LatLng latLng,double radius){
        GeoQuery geoQuery =  geoFire.queryAtLocation(new GeoLocation(latLng.latitude ,latLng.longitude),radius);
        geoQuery.removeAllListeners();
        return geoQuery;
    }

    public DatabaseReference isDriverWorking(String idDriver){
        return FirebaseDatabase.getInstance().getReference().child("drivers_working").child(idDriver);
    }


}
