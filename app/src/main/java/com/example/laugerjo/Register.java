package com.example.laugerjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText edtCorreo,edtContra,edtNombre,edtApellido,edtNumero;
    private TextView txtlogin;
    private Button btnReg;
    private String name= "";
    private String id= "";
    private String lastname= "";
    private String email= "";
    private String password= "";
    private String number="";
    FirebaseAuth Aunte;
    DatabaseReference DB;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        edtCorreo = findViewById(R.id.edtCorreo);
        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtContra = findViewById(R.id.edtContra);
        edtNumero = findViewById(R.id.edtNumero);
        btnReg = findViewById(R.id.btnReg);
        txtlogin = findViewById(R.id.txtlogin);
        txtlogin = findViewById(R.id.txtlogin);
        Aunte = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance().getReference();
        toolbar.show(this,"Registro de Usuario",true);
        txtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Register.this, MainActivity.class);
                Register.this.startActivity(intent);
                finish();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name=edtNombre.getText().toString();
                        lastname=edtApellido.getText().toString();
                        email=edtCorreo.getText().toString();
                        password=edtContra.getText().toString();
                        number=edtNumero.getText().toString();

                if(!name.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !password.isEmpty()&& !number.isEmpty()){
                    if(password.length() >=6){
                        registerUser();
                    }else{
                        Toast.makeText(Register.this, "El password debe tener al menos 6 caracteres.",Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Toast.makeText(Register.this, "Complete todos los campos.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void registerUser() {
        Aunte.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                  id = Aunte.getCurrentUser().getUid();
                    pref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
                    String sUser = pref.getString("user", "");
                    Users users = new Users();
                    users.setId(id);
                    users.setEmail(email);
                    users.setLastname(lastname);
                    users.setName(name);
                    users.setPassword(password);
                    users.setNumber(number);
                    if (sUser.equals("driver")) {

                        DB.child("Users").child("Drivers").child(id).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Registro de conductor exitoso.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Register.this, "Fallo registro de conductor.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else if (sUser.equals("client")) {
                        DB.child("Users").child("Clients").child(id).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Registro de cliente  exitoso.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Register.this, "Fallo registro de cliente.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            }
        });
    }
}

