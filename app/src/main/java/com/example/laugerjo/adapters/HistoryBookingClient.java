package com.example.laugerjo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laugerjo.R;
import com.example.laugerjo.model.Driver;
import com.example.laugerjo.model.HistoryBooking;
import com.example.laugerjo.providers.DriverProvider;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HistoryBookingClient extends FirebaseRecyclerAdapter <HistoryBooking, HistoryBookingClient.ViewHolder>{


    private DriverProvider driverProvider;
    private Context mContext;
    public HistoryBookingClient(FirebaseRecyclerOptions<HistoryBooking> options,Context context){
    super(options);
    driverProvider = new DriverProvider();
    mContext= context;
    }

    @Override
    protected void onBindViewHolder(@NonNull  final ViewHolder holder, int position, @NonNull HistoryBooking historyBooking) {
        holder.txtOrigin.setText(historyBooking.getOrigin());
        holder.txtDestination.setText(historyBooking.getDestination());
        holder.txtCalification.setText( String.valueOf(historyBooking.getCalificationClient()) );
        //holder.txtPrice.setText(historyBooking.getDestination());
        driverProvider.getDriver(historyBooking.getIdDriver()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name =dataSnapshot.child("name").getValue().toString();
                    String lastname =dataSnapshot.child("lastname").getValue().toString();
                    String fullname = name+ " "+lastname;
                    holder.txtFullName.setText(fullname);
                    if(dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(mContext).load(image).into(holder.imgHistoryBooking);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_booking,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtFullName,txtOrigin,txtDestination,txtCalification,txtPrice;
        private ImageView imgHistoryBooking;
        public ViewHolder(View view){
            super(view);
            txtFullName = view.findViewById(R.id.txtFullName);
            txtOrigin = view.findViewById(R.id.txtOrigin);
            txtDestination = view.findViewById(R.id.txtDestination);
            txtCalification = view.findViewById(R.id.txtCalification);
            txtPrice = view.findViewById(R.id.txtPrice);
            imgHistoryBooking = view.findViewById(R.id.imgHistoryBooking);
        }
    }

}
