package com.example.laugerjo.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.MainActivity;
import com.example.laugerjo.activities.driver.RegisterDriver;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.model.Client;
import com.example.laugerjo.model.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.example.laugerjo.providers.authProviders;
import com.example.laugerjo.providers.ClientProvider;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    private EditText edtCorreo,edtContra,edtNombre,edtApellido,edtNumero,edtIdenti;
    private TextView txtlogin;
    private Button btnReg,btnSig;
    private String name= "";
    private String id= "";
    private String lastname= "";
    private String email= "";
    private String password= "";
    private String number="";
    private String identi="";
    SharedPreferences pref;

    authProviders mauthProviders;
    ClientProvider mclientProviders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        String sUser = pref.getString("user", "");
        btnSig = findViewById(R.id.btnSig);
        btnReg = findViewById(R.id.btnReg);
        if (sUser.equals("driver")) {
            btnSig.setVisibility(View.VISIBLE);
            btnSig.setEnabled(true);
            btnReg.setEnabled(false);
            btnReg.setVisibility(View.INVISIBLE);
        }

        mauthProviders = new authProviders();
        mclientProviders = new ClientProvider();
        edtCorreo = findViewById(R.id.edtCorreo);
        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtContra = findViewById(R.id.edtContra);
        edtNumero = findViewById(R.id.edtNumero);
        edtIdenti = findViewById(R.id.edtIdenti);
        txtlogin = findViewById(R.id.txtlogin);
        txtlogin = findViewById(R.id.txtlogin);

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
                        identi=edtIdenti.getText().toString();

                if(!name.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !password.isEmpty()&& !number.isEmpty()&& !identi.isEmpty()){
                    if(password.length() >=6){
                        if(number.length()==10) {
                            registerUser(email, lastname, name, password, number, identi);
                            // Driver driver = new Driver();
                        }else{
                            Toast.makeText(Register.this, "El número de celular debe tener 10 caracteres.",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(Register.this, "El password debe tener al menos 6 caracteres.",Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Toast.makeText(Register.this, "Complete todos los campos.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name=edtNombre.getText().toString();
                lastname=edtApellido.getText().toString();
                email=edtCorreo.getText().toString();
                password=edtContra.getText().toString();
                number=edtNumero.getText().toString();
                identi=edtIdenti.getText().toString();


                if(!name.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !password.isEmpty() && !number.isEmpty() && !identi.isEmpty()){
                    if(password.length() >=6){
                        if(number.length()==10) {
                            Intent intent = new Intent(Register.this, RegisterDriver.class);
                            intent.putExtra("name", edtNombre.getText().toString());
                            intent.putExtra("lastname", edtApellido.getText().toString());
                            intent.putExtra("email", edtCorreo.getText().toString());
                            intent.putExtra("password", edtContra.getText().toString());
                            intent.putExtra("number", edtNumero.getText().toString());
                            intent.putExtra("identi", edtIdenti.getText().toString());
                            Register.this.startActivity(intent);
                        }else{
                            Toast.makeText(Register.this, "El número de celular debe tener 10 caracteres.",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(Register.this, "La contraseña debe tener al menos 6 caracteres.",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Register.this, "Complete todos los campos.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void create(Client client){
        mclientProviders.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful()){
                  Toast.makeText(Register.this,"El cliente se creo",Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(Register.this,"El cliente no pudo ser creado",Toast.LENGTH_SHORT).show();
              }
            }
        });
    }
    private void registerUser(final String email, final String lastname,final String name ,final String password,final String number,final String identi) {
        mauthProviders.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                   String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    pref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
                    String sUser = pref.getString("user", "");
                    /*Users users = new Users();
                    users.setId(id);
                    users.setEmail(email);
                    users.setLastname(lastname);
                    users.setName(name);
                    users.setPassword(password);
                    users.setNumber(number);
                    users.setIdenti(identi);*/

                    if (sUser.equals("driver")) {

                        /*DB.child("Users").child("Drivers").child(id).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Registro de conductor exitoso.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Register.this, "Fallo registro de conductor.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*/
                    } else if (sUser.equals("client")) {
                        Client client = new Client(id,email,lastname,name,password,number,identi);
                        create(client);
                        /*
                        DB.child("Users").child("Clients").child(id).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Registro de cliente  exitoso.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Register.this, "Fallo registro de cliente.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*/
                    }

                }
            }
        });
    }
}

