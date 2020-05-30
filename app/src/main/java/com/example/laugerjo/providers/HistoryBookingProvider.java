package com.example.laugerjo.providers;

import com.example.laugerjo.model.ClientBooking;
import com.example.laugerjo.model.HistoryBooking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class HistoryBookingProvider {

    private DatabaseReference DB;


    public HistoryBookingProvider() {
        DB = FirebaseDatabase.getInstance().getReference().child("HystoryBooking");
    }


    public Task<Void> create(HistoryBooking historyBooking) {
        return DB.child(historyBooking.getIdHistoryBooking()).setValue(historyBooking);

    }

    public  Task<Void>  updateCalificationClient( String idHistoryBooking, float calificationClient) {
        Map<String, Object> map = new HashMap<>();
        map.put("calificationClient",calificationClient);
        return DB.child(idHistoryBooking).updateChildren(map);
    }

    public  Task<Void>  updateCalificationDriver( String idHistoryBooking, float calificationDriver) {
        Map<String, Object> map = new HashMap<>();
        map.put("calificationDriver",calificationDriver);
        return DB.child(idHistoryBooking).updateChildren(map);
    }
    public DatabaseReference getHistoryBooking(String idHistoryBooking){
        return  DB.child(idHistoryBooking);
    }
}


