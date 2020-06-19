package com.example.laugerjo.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.client.HistoryBookingDetailClientActivity;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.model.HistoryBooking;
import com.example.laugerjo.providers.ClientProvider;
import com.example.laugerjo.providers.DriverProvider;
import com.example.laugerjo.providers.HistoryBookingProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryBookingDetailDriverActivity extends AppCompatActivity {
    private TextView txtFullName,txtOrigin,txtDestination,txtCalification, txtPrice,txtContact;
    private RatingBar rb;
    private CircleImageView circleImg;
    private  String extraId;
    private ClientProvider clientProvider;
    private HistoryBookingProvider historyBookingProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_booking_detail_driver);
        txtFullName = findViewById(R.id.txtFullNameHistoryBookingDetail);
        txtOrigin = findViewById(R.id.txtOriginHistoryBookingDetail);
        txtDestination = findViewById(R.id.txtDestinationHistoryBookingDetail);
        txtCalification = findViewById(R.id.txtCalificationHistoryBookingDetail);
        txtPrice = findViewById(R.id.txtPriceHistoryBookingDetail);
        txtContact = findViewById(R.id.txtContactHistoryBookingDetail);
        rb = findViewById(R.id.rbHistoryBookingDetail);
        circleImg = findViewById(R.id.circleImageHistoryBookingDetail);
        clientProvider = new ClientProvider();
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
                    toolbar.show(HistoryBookingDetailDriverActivity.this,"",true);
                    //txtPlate.setText(historyBooking.getPlate);
                    if(dataSnapshot.hasChild("calificationDriver")){
                        txtCalification.setText("Calificación recibida: "+historyBooking.getCalificationDriver());

                    }

                    if(dataSnapshot.hasChild("calificationClient")){

                        rb.setRating((float)historyBooking.getCalificationClient());

                    }

                    clientProvider.getClient(historyBooking.getIdClient()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String name =dataSnapshot.child("name").getValue().toString();
                                String lastname =dataSnapshot.child("lastname").getValue().toString();
                                String fullname = name+ " "+lastname;
                                txtFullName.setText(fullname.toUpperCase());
                                if(dataSnapshot.hasChild("image")) {
                                    String image = dataSnapshot.child("image").getValue().toString();
                                    Picasso.with(HistoryBookingDetailDriverActivity.this).load(image).into(circleImg);

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
