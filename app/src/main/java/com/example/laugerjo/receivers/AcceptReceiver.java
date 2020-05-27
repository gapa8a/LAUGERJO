package com.example.laugerjo.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.laugerjo.activities.driver.MapDriverBookingActivity;
import com.example.laugerjo.providers.ClientBookingProvider;
import com.example.laugerjo.providers.GeofireProvider;
import com.example.laugerjo.providers.authProviders;

import java.security.AuthProvider;

public class AcceptReceiver extends BroadcastReceiver {

    private ClientBookingProvider clientBookingProvider;
    private GeofireProvider geofireProvider;
    private authProviders Aunte;


    @Override
    public void onReceive(Context context, Intent intent) {
        Aunte = new authProviders();
        geofireProvider = new GeofireProvider("active_drivers");
        geofireProvider.removeLocation(Aunte.getId());
        String idClient = intent.getExtras().getString("idClient");
        clientBookingProvider = new ClientBookingProvider();
        clientBookingProvider.updateStatus(idClient, "accept");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
        Intent intent1 =new Intent(context, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        context.startActivity(intent1);


    }
}
