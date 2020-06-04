package com.example.laugerjo.activities.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.laugerjo.R;
import com.example.laugerjo.adapters.HistoryBookingClient;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.model.HistoryBooking;
import com.example.laugerjo.providers.HistoryBookingProvider;
import com.example.laugerjo.providers.authProviders;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HistoryBookingClientActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private HistoryBookingClient adapter ;
    private authProviders Aunteti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_booking_client);
        toolbar.show(this,"",true);

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
                .orderByChild("idClient")
                .equalTo(Aunteti.getId());
        FirebaseRecyclerOptions<HistoryBooking> options = new FirebaseRecyclerOptions.Builder<HistoryBooking>()
                .setQuery(query, HistoryBooking.class)
                .build();
        adapter =  new HistoryBookingClient(options, HistoryBookingClientActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
