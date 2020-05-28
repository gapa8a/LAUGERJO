package com.example.laugerjo.providers;

import com.example.laugerjo.model.Client;
import com.example.laugerjo.model.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

}
