package com.example.laugerjo.providers;

import com.example.laugerjo.model.Client;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClientProvider {
   DatabaseReference DB;

    public ClientProvider() {
        DB = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
    }

    public Task<Void> create(Client client){
        return DB.child(client.getId()).setValue(client);

    }
}