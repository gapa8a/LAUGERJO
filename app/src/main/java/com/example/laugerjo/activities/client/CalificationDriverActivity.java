package com.example.laugerjo.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.driver.CalificationClientActivity;
import com.example.laugerjo.activities.driver.MapDriverActivity;
import com.example.laugerjo.model.ClientBooking;
import com.example.laugerjo.model.HistoryBooking;
import com.example.laugerjo.providers.ClientBookingProvider;
import com.example.laugerjo.providers.HistoryBookingProvider;
import com.example.laugerjo.providers.authProviders;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.security.AuthProvider;
import java.util.Date;

public class CalificationDriverActivity extends AppCompatActivity {


    private TextView txtOrigin,txtDestination,txtPriceCalification;
    private RatingBar rb;
    private Button btnCalification;

    private ClientBookingProvider clientBookingProvider;
    private authProviders authProviders;

    private HistoryBooking historyBooking;

    private HistoryBookingProvider historyBookingProvider;
    private float calification = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calification_driver);

        txtOrigin = findViewById(R.id.txtOriginCalification);
        txtDestination = findViewById(R.id.txtDestinationCalification);
        btnCalification = findViewById(R.id.btnCalification);
        txtPriceCalification = findViewById(R.id.txtPriceCalificationDriver);
        rb =findViewById(R.id.rbCalification);
        clientBookingProvider = new ClientBookingProvider();
        historyBookingProvider = new HistoryBookingProvider();
        authProviders = new authProviders();


        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                calification =rating;
            }
        });
        btnCalification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calificate();
            }
        });

        getClientBooking();
    }




    private  void getClientBooking(){
        clientBookingProvider.getClientBooking(authProviders.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ClientBooking clientBooking = dataSnapshot.getValue(ClientBooking.class);
                    txtOrigin.setText(clientBooking.getOrigin());
                    txtDestination.setText(clientBooking.getDestination());
                    txtPriceCalification.setText("$"+clientBooking.getPrice());
                    historyBooking = new HistoryBooking(
                            clientBooking.getIdHistoryBooking(),
                            clientBooking.getIdClient(),
                            clientBooking.getIdDriver(),
                            clientBooking.getDestination(),
                            clientBooking.getOrigin(),
                            clientBooking.getTime(),
                            clientBooking.getKm(),
                            clientBooking.getPrice(),
                            clientBooking.getStatus(),
                            clientBooking.getOriginLat(),
                            clientBooking.getOriginLng(),
                            clientBooking.getDestinationLat(),
                            clientBooking.getDestinationLng()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void calificate() {
        if (calification > 0) {
            historyBooking.setCalificationDriver(calification);
            historyBooking.setTimestamp( new Date().getTime());
            historyBookingProvider.getHistoryBooking(historyBooking.getIdHistoryBooking()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        historyBookingProvider.updateCalificationDriver(historyBooking.getIdHistoryBooking(),calification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificationDriverActivity.this, "La calificación se guardo correctmanete", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CalificationDriverActivity.this, MapClientActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else{
                        historyBookingProvider.create(historyBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificationDriverActivity.this, "La calificación se guardo correctmanete", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CalificationDriverActivity.this, MapClientActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            Toast.makeText(this, "Debe ingresar la calificación", Toast.LENGTH_SHORT).show();
        }
    }}