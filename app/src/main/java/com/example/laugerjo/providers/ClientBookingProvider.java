package com.example.laugerjo.providers;

import com.example.laugerjo.model.Client;
import com.example.laugerjo.model.ClientBooking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClientBookingProvider {

    private DatabaseReference DB;

    public ClientBookingProvider() {
        DB = FirebaseDatabase.getInstance().getReference().child("ClientBooking");
    }


    public Task<Void> create (ClientBooking clientBooking){
        return DB.child(clientBooking.getIdClient()).setValue(clientBooking);
    }
}
