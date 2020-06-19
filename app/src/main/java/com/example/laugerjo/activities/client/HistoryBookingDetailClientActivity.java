package com.example.laugerjo.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.laugerjo.R;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.model.HistoryBooking;
import com.example.laugerjo.providers.DriverProvider;
import com.example.laugerjo.providers.HistoryBookingProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryBookingDetailClientActivity extends AppCompatActivity {

    private TextView txtFullName,txtOrigin,txtDestination,txtCalification, txtPrice,txtContact,txtCar,txtPlate;
    private RatingBar  rb;
    private CircleImageView circleImg;
    private  String extraId;
    private DriverProvider driverProvider;
    private HistoryBookingProvider historyBookingProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_booking_detail_client);
        txtFullName = findViewById(R.id.txtFullNameHistoryBookingDetail);
        txtOrigin = findViewById(R.id.txtOriginHistoryBookingDetail);
        txtDestination = findViewById(R.id.txtDestinationHistoryBookingDetail);
        txtCalification = findViewById(R.id.txtCalificationHistoryBookingDetail);
        txtPrice = findViewById(R.id.txtPriceHistoryBookingDetail);
        txtContact = findViewById(R.id.txtContactHistoryBookingDetail);
        txtCar = findViewById(R.id.txtBrandHistoryBookingDetail);
        txtPlate = findViewById(R.id.txtPlateHistoryBookingDetail);
        rb = findViewById(R.id.rbHistoryBookingDetail);
        circleImg = findViewById(R.id.circleImageHistoryBookingDetail);
        driverProvider = new DriverProvider();
        extraId = getIntent().getStringExtra("idHistoryBooking");
        historyBookingProvider = new HistoryBookingProvider();
        getHistoryBooking();
    }

    private void getHistoryBooking() {
        historyBookingProvider.getHistoryBooking(extraId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HistoryBooking historyBooking = dataSnapshot.getValue(HistoryBooking.class);
                    txtOrigin.setText(historyBooking.getOrigin());
                    txtDestination.setText(historyBooking.getDestination());
                    txtPrice.setText("$"+historyBooking.getPrice());
                    //txtContact.setText(historyBooking.getContact());
                    //txtCar.setText(historyBooking.getCar());
                    toolbar.show(HistoryBookingDetailClientActivity.this,"Registro de Usuario",true);
                    //txtPlate.setText(historyBooking.getPlate);
                    if(dataSnapshot.hasChild("calificationDriver")){
                        txtCalification.setText("Calificación recibida: "+historyBooking.getCalificationClient());

                    }

                    if(dataSnapshot.hasChild("calificationDriver")){

                        rb.setRating((float)historyBooking.getCalificationDriver());

                    }

                    driverProvider.getDriver(historyBooking.getIdDriver()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String name =dataSnapshot.child("name").getValue().toString();
                                String lastname =dataSnapshot.child("lastname").getValue().toString();
                                String fullname = name+ " "+lastname;
                                txtFullName.setText(fullname.toUpperCase());
                                if(dataSnapshot.hasChild("image")) {
                                    String image = dataSnapshot.child("image").getValue().toString();
                                    Picasso.with(HistoryBookingDetailClientActivity.this).load(image).into(circleImg);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
