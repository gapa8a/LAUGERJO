package com.example.laugerjo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.laugerjo.R;
import com.example.laugerjo.activities.client.MapClientActivity;
import com.example.laugerjo.activities.driver.MapDriverActivity;
import com.google.firebase.auth.FirebaseAuth;



public class pantallaInicial extends AppCompatActivity {
    private Button btnCondu,btnClien;

    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicial);
        btnCondu = findViewById(R.id.btnCondu);
        btnClien = findViewById(R.id.btnClien);


        pref =getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        btnClien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user","client");
                editor.apply();
                goToLogin();
            }
        });
        btnCondu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user","driver");
                editor.apply();
                goToLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            String user = pref.getString("user","");
            if(user.equals("client")){
                Intent intent =new Intent(pantallaInicial.this, MapClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//Con este add flag se asegura no volver a la pantalla de registro
                startActivity(intent);

            }else {
                Intent intent =new Intent(pantallaInicial.this, MapDriverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//Con este add flag se asegura no volver a la pantalla de registro
                startActivity(intent);
            }
        }
    }

    private void goToLogin(){
        Intent intent =new Intent(pantallaInicial.this,MainActivity.class);
        startActivity(intent);
    }
}
