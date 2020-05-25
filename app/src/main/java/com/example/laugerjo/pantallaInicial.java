package com.example.laugerjo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;


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
                Intent intent = new Intent(pantallaInicial.this, MainActivity.class);
                pantallaInicial.this.startActivity(intent);
            }
        });
        btnCondu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user","driver");
                editor.apply();
                Intent intent = new Intent(pantallaInicial.this, MainActivity.class);
                pantallaInicial.this.startActivity(intent);

            }
        });
    }
}
