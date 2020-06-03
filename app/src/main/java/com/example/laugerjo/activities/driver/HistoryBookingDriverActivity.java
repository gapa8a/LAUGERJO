package com.example.laugerjo.activities.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.client.HistoryBookingClientActivity;
import com.example.laugerjo.adapters.HistoryBookingClient;
import com.example.laugerjo.adapters.HistoryBookingDriver;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.model.HistoryBooking;
import com.example.laugerjo.providers.authProviders;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HistoryBookingDriverActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private HistoryBookingDriver adapter ;
    private authProviders Aunteti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_booking_driver);
        toolbar.show(this,"Historial de viajes",true);

        recyclerView = findViewById(R.id.rcvHistoryBooking);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Aunteti = new authProviders();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("HystoryBooking")
                .orderByChild("idDriver")
                .equalTo(Aunteti.getId());
        FirebaseRecyclerOptions<HistoryBooking> options = new FirebaseRecyclerOptions.Builder<HistoryBooking>()
                .setQuery(query, HistoryBooking.class)
                .build();
        adapter =  new HistoryBookingDriver(options, HistoryBookingDriverActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
