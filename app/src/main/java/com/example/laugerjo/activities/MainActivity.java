package com.example.laugerjo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.client.Register;
import com.example.laugerjo.includes.toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private EditText edtEmail,edtPassword;
    private Button btnLogin;
    private TextView txtSignUp,txtForgot;
    private String email = "", password = "";
    private ProgressDialog Dialog;
    FirebaseAuth Aunte;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignUp =findViewById(R.id.txtSignUp);
        txtForgot =findViewById(R.id.txtForgot);
        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);
        Aunte = FirebaseAuth.getInstance();
        Dialog = new ProgressDialog(this);
        toolbar.show(this,"Iniciar Sesión",true);
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Register.class);
                MainActivity.this.startActivity(intent);
                finish();
    }
});
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();
                if(!email.isEmpty() && !password.isEmpty()){
                    Dialog.setMessage("Espere un momento");
                    Dialog.setCanceledOnTouchOutside(false);
                    Dialog.show();
                    loginUser();
                }else{
                    Toast.makeText(MainActivity.this, "Complete los campos.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Forgot.class));
            }
        });
    }
    private void loginUser(){
        Aunte.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    //finish();
                }else{
                    Toast.makeText(MainActivity.this, "No se pudo iniciar sesión, verifique los datos ingresados.",Toast.LENGTH_SHORT).show();
                }
            Dialog.dismiss();}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Aunte.getCurrentUser() !=null){

            startActivity(new Intent(MainActivity.this , ProfileActivity.class));
            Aunte.getCurrentUser().getEmail();
            /*Toast.makeText(MainActivity.this, "Bienvenido"+Aunte.getCurrentUser().getEmail(),Toast.LENGTH_SHORT);
            finish();*/

        }
    }
}