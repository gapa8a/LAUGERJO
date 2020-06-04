package com.example.laugerjo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.client.HistoryBookingDetailClientActivity;
import com.example.laugerjo.activities.driver.HistoryBookingDetailDriverActivity;
import com.example.laugerjo.model.HistoryBooking;
import com.example.laugerjo.providers.ClientProvider;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HistoryBookingDriver extends FirebaseRecyclerAdapter<HistoryBooking, HistoryBookingDriver.ViewHolder> {


private ClientProvider clientProvider;
private Context mContext;
public HistoryBookingDriver(FirebaseRecyclerOptions<HistoryBooking> options, Context context){
        super(options);
        clientProvider = new ClientProvider();
        mContext= context;
        }

@Override
protected void onBindViewHolder(@NonNull final HistoryBookingDriver.ViewHolder holder, int position, @NonNull HistoryBooking historyBooking) {
        final String id = getRef(position).getKey();

        holder.txtOrigin.setText(historyBooking.getOrigin());
        holder.txtDestination.setText(historyBooking.getDestination());
        holder.txtCalification.setText( String.valueOf(historyBooking.getCalificationDriver()) );
        //holder.txtPrice.setText(historyBooking.getDestination());
        clientProvider.getClient(historyBooking.getIdClient()).addListenerForSingleValueEvent(new ValueEventListener() {
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
    holder.mView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, HistoryBookingDetailDriverActivity.class);
            intent.putExtra("idHistoryBooking",id);
            mContext.startActivity(intent);
        }
    });

        }

@NonNull
@Override
public HistoryBookingDriver.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_booking,parent,false);
        return new HistoryBookingDriver.ViewHolder(view);
        }

public class ViewHolder extends RecyclerView.ViewHolder{
    private TextView txtFullName,txtOrigin,txtDestination,txtCalification,txtPrice;
    private ImageView imgHistoryBooking;
    private View mView;

    public ViewHolder(View view){
        super(view);
        mView=view;
        txtFullName = view.findViewById(R.id.txtFullName);
        txtOrigin = view.findViewById(R.id.txtOrigin);
        txtDestination = view.findViewById(R.id.txtDestination);
        txtCalification = view.findViewById(R.id.txtCalification);
        txtPrice = view.findViewById(R.id.txtPrice);
        imgHistoryBooking = view.findViewById(R.id.imgHistoryBooking);
    }
}

}


