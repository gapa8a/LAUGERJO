package com.example.laugerjo.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laugerjo.R;
import com.example.laugerjo.providers.ClientBookingProvider;
import com.example.laugerjo.providers.GeofireProvider;
import com.example.laugerjo.providers.authProviders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class NotificationBookingActivity extends AppCompatActivity {

    private TextView txtDestination, txtOrigin, txtPrice, txtMin, txtDistance, txtCounter;
    private Button btnAccept, btnCancel;

    private ClientBookingProvider clientBookingProvider;
    private GeofireProvider geofireProvider;
    private authProviders Aunte;
    private String extraIdClient, extraOrigin, extraDestination, extraPrice, extraMin, extraDistance;

    private MediaPlayer mediaPlayer;
    private int mCounter = 10;
    private Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mCounter = mCounter - 1;
            txtCounter.setText(String.valueOf(mCounter));
            if (mCounter > 0) {

                initTimer();
            } else {
                cancelBooking();
            }
        }
    };
    private ValueEventListener listener;

    private void initTimer() {
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_booking);
        txtDestination = findViewById(R.id.txtDestination);
        txtOrigin = findViewById(R.id.txtOrigin);
        txtPrice = findViewById(R.id.txtPrice);
        txtMin = findViewById(R.id.txtMin);
        txtDistance = findViewById(R.id.txtDistance);
        txtCounter = findViewById(R.id.txtCounter);
        btnAccept = findViewById(R.id.btnAcceptBooking);
        btnCancel = findViewById(R.id.btnCancelBooking);

        extraIdClient = getIntent().getStringExtra("idClient");

        extraIdClient = getIntent().getStringExtra("idClient");
        extraOrigin = getIntent().getStringExtra("origin");
        extraDestination = getIntent().getStringExtra("destination");
        extraPrice = getIntent().getStringExtra("price");
        extraMin = getIntent().getStringExtra("min");
        extraDistance = getIntent().getStringExtra("distance");
        mediaPlayer = MediaPlayer.create(this, R.raw.tono);
        mediaPlayer.setLooping(true);
        clientBookingProvider = new ClientBookingProvider();
        txtDestination.setText(extraDestination);
        txtOrigin.setText(extraOrigin);
        txtDistance.setText(extraDistance);
        txtMin.setText(extraMin);
        //txtPrice.setText(extraPrice);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        initTimer();
        checkIfClientCancelBooking();
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
        private void checkIfClientCancelBooking(){
         listener = clientBookingProvider.getClientBooking(extraIdClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(NotificationBookingActivity.this, "El cliente cancelo el viaje", Toast.LENGTH_LONG).show();
                    if (handler != null) handler.removeCallbacks(runnable);

                    Intent intent = new Intent(NotificationBookingActivity.this, MapDriverActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }
    private void cancelBooking() {
        if (handler != null) handler.removeCallbacks(runnable);

        clientBookingProvider.updateStatus(extraIdClient, "cancel");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
        Intent intent = new Intent(NotificationBookingActivity.this, MapDriverActivity.class);
        startActivity(intent);
        finish();

    }

    private void acceptBooking() {
        if (handler != null) handler.removeCallbacks(runnable);
        Aunte = new authProviders();
        geofireProvider = new GeofireProvider("active_drivers");
        geofireProvider.removeLocation(Aunte.getId());

        clientBookingProvider = new ClientBookingProvider();
        clientBookingProvider.updateStatus(extraIdClient, "accept");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
        Intent intent1 = new Intent(NotificationBookingActivity.this, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idClient", extraIdClient);
        startActivity(intent1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }
        @Override
        protected void onStop() {
            super.onStop();
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.release();
                }
            }
        }

        @Override
        protected void onResume(){
            super.onResume();
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (handler != null) handler.removeCallbacks(runnable);

            if (mediaPlayer != null) {
               // if (mediaPlayer.isPlaying()) {
                    //mediaPlayer.pause();
                //}
            }
            if (listener!= null){
                clientBookingProvider.getClientBooking(extraIdClient).removeEventListener(listener);
            }
        }
    }

