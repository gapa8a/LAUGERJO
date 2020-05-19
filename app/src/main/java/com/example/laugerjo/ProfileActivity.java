package com.example.laugerjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
private Button btnCerrar_ses,btnMapa;
private TextView txtName, txtEmail;
private FirebaseAuth aunte;
private DatabaseReference DB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile1);
        aunte = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference();
        btnCerrar_ses = findViewById(R.id.btnSes);
        btnMapa = findViewById(R.id.btnMapa);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        btnCerrar_ses.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                aunte.signOut();
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                finish();

            }
        });

        getUserInfo();
    }
    private void getUserInfo(){
        String id = aunte.getCurrentUser().getUid();
        DB.child("usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String lastname = dataSnapshot.child("lastname").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    txtName.setText(name+" "+lastname);
                    txtEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
