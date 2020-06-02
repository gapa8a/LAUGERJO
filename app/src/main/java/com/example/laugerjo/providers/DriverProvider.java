package com.example.laugerjo.providers;

import com.example.laugerjo.model.Client;
import com.example.laugerjo.model.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DriverProvider {
    DatabaseReference DB;

    public DriverProvider() {
        DB = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver");
    }

    public Task<Void> create(Driver driver){
        return DB.child(driver.getId()).setValue(driver);

    }
    public DatabaseReference getDriver(String idDriver){
        return DB.child(idDriver);
    }


    public Task<Void> update(Driver driver){

        Map<String ,Object> map =new HashMap<>();
        map.put("name",driver.getName());
        map.put("lastname",driver.getLastname());
        map.put("image",driver.getImage());
        map.put("placa",driver.getPlaca());
        map.put("marca",driver.getMarca());
        map.put("modelo",driver.getModelo());
        map.put("año",driver.getAño());
        return DB.child(driver.getId()).updateChildren(map);

    }
}
