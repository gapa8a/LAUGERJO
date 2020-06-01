package com.example.laugerjo.activities.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.laugerjo.R;
import com.example.laugerjo.providers.ClientBookingProvider;
import com.example.laugerjo.providers.GeofireProvider;
import com.example.laugerjo.providers.authProviders;

public class NotificationBookingActivity extends AppCompatActivity {

    private TextView txtDestination, txtOrigin, txtPrice,txtMin,txtDistance;
    private Button btnAccept, btnCancel;

    private ClientBookingProvider clientBookingProvider;
    private GeofireProvider geofireProvider;
    private authProviders Aunte;
    private String extraIdClient,extraOrigin,extraDestination,extraPrice, extraMin,extraDistance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_booking);
        txtDestination = findViewById(R.id.txtDestination);
        txtOrigin = findViewById(R.id.txtOrigin);
        txtPrice = findViewById(R.id.txtPrice);
        txtMin = findViewById(R.id.txtMin);
        txtDistance = findViewById(R.id.txtDistance);

        btnAccept = findViewById(R.id.btnAcceptBooking);
        btnCancel = findViewById(R.id.btnCancelBooking);

        extraIdClient = getIntent().getStringExtra("idClient");

        extraIdClient = getIntent().getStringExtra("idClient");
        extraOrigin = getIntent().getStringExtra("origin");
        extraDestination = getIntent().getStringExtra("destination");
        extraPrice = getIntent().getStringExtra("price");
        extraMin = getIntent().getStringExtra("min");
        extraDistance = getIntent().getStringExtra("distance");

        txtDestination.setText(extraDestination);
        txtOrigin.setText(extraOrigin);
        txtDistance.setText(extraDistance);
        txtMin.setText(extraMin);
        //txtPrice.setText(extraPrice);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBooking();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking();
            }
        });
    }

    private void cancelBooking() {
        clientBookingProvider = new ClientBookingProvider();
        clientBookingProvider.updateStatus(extraIdClient, "cancel");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
        Intent intent =  new Intent(NotificationBookingActivity.this ,MapDriverActivity.class);
        startActivity(intent);
        finish();

    }

    private void acceptBooking() {
        Aunte = new authProviders();
        geofireProvider = new GeofireProvider("active_drivers");
        geofireProvider.removeLocation(Aunte.getId());

        clientBookingProvider = new ClientBookingProvider();
        clientBookingProvider.updateStatus(extraIdClient, "accept");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
        Intent intent1 =new Intent(NotificationBookingActivity.this, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idClient", extraIdClient);
       startActivity(intent1);

    }
}
