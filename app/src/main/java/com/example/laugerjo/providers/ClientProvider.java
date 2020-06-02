package com.example.laugerjo.providers;

import com.example.laugerjo.model.Client;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ClientProvider {
   DatabaseReference DB;

    public ClientProvider() {
        DB = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
    }

    public Task<Void> create(Client client){

        Map<String ,Object> map =new HashMap<>();
        map.put("name",client.getName());
        map.put("lastname",client.getLastname());
        map.put("email",client.getEmail());
        return DB.child(client.getId()).setValue(client);

    }
    public Task<Void> update(Client client){

        Map<String ,Object> map =new HashMap<>();
        map.put("name",client.getName());
        map.put("lastname",client.getLastname());
        map.put("image",client.getImage());
        return DB.child(client.getId()).updateChildren(map);

    }

    public DatabaseReference getClient(String idClient){
        return DB.child(idClient);
    }
}