package com.example.laugerjo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.laugerjo.R;
import com.example.laugerjo.includes.toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot extends AppCompatActivity {
    private EditText edtRecuperar;
    private Button btnRecuperar;
    private String email = "";
    private FirebaseAuth Aunte;
    private ProgressDialog Dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        edtRecuperar = findViewById(R.id.edtRecuperar);
        btnRecuperar = findViewById(R.id.btnRecuperar);
        Aunte = FirebaseAuth.getInstance();
        Dialog = new ProgressDialog(this);
        toolbar.show(this,"Recuperación de Contraseña",true);
        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = edtRecuperar.getText().toString();
                if(!email.isEmpty()){
                    Dialog.setMessage("Espere un momento");
                    Dialog.setCanceledOnTouchOutside(false);
                    Dialog.show();
                    resetPassword();
                }else {
                    Toast.makeText(Forgot.this,"Ingrese un email valido.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void resetPassword(){
    Aunte.setLanguageCode("es");
    Aunte.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()){
            Toast.makeText(Forgot.this,"Se ha enviado un correo de Recuperación de Contraseña.", Toast.LENGTH_SHORT).show();
        }
            else{
            Toast.makeText(Forgot.this,"No se pude enviar el correo de recuperación de contraseña.", Toast.LENGTH_SHORT).show();
            }
            Dialog.dismiss();
        }
    });
    }
}
