package com.example.laugerjo.providers;

import com.example.laugerjo.model.Client;
import com.example.laugerjo.model.ClientBooking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ClientBookingProvider {

    private DatabaseReference DB;


    public ClientBookingProvider() {
        DB = FirebaseDatabase.getInstance().getReference().child("ClientBooking");
    }


    public Task<Void> create (ClientBooking clientBooking){
        return DB.child(clientBooking.getIdClient()).setValue(clientBooking);
    }
    
    public Task<Void>updateStatus(String idClientBooking, String status){
        Map<String ,Object> map = new HashMap<>();
        map.put("status", status);
        return DB.child(idClientBooking).updateChildren(map);
    }

    public Task<Void>updateIdHistoryBooking(String idClientBooking){
        String idPush = DB.push().getKey();
        Map<String ,Object> map = new HashMap<>();
        map.put("idHistoryBooking", idPush);
        return DB.child(idClientBooking).updateChildren(map);
    }

    public DatabaseReference getStatus( String idClientBooking){
        return DB.child(idClientBooking).child("status");
    }

    public DatabaseReference getClientBooking( String idClientBooking){
        return DB.child(idClientBooking);
    }

    public Task<Void> delete (String idClientBooking){
     return DB.child(idClientBooking).removeValue();
    }
}
