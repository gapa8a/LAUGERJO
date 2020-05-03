package com.example.laugerjo.activities.driver;

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
import com.example.laugerjo.activities.client.Register;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.model.Client;
import com.example.laugerjo.model.Driver;
import com.example.laugerjo.providers.ClientProvider;
import com.example.laugerjo.providers.DriverProvider;
import com.example.laugerjo.providers.authProviders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class RegisterDriver extends AppCompatActivity {
    private EditText edtPlaca,edtContra,edtNombre,edtApellido,edtNumero,edtIdenti;
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
    DriverProvider mDriverProviders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            Driver driver;
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register_driver);
            mauthProviders = new authProviders();
            mDriverProviders = new DriverProvider();
            edtPlaca = findViewById(R.id.edtPlaca);
            edtNombre = findViewById(R.id.edtNombre);
            edtApellido = findViewById(R.id.edtApellido);
            edtContra = findViewById(R.id.edtContra);
            edtNumero = findViewById(R.id.edtNumero);
            edtIdenti = findViewById(R.id.edtIdenti);
            btnReg = findViewById(R.id.btnReg);
            txtlogin = findViewById(R.id.txtlogin);
            txtlogin = findViewById(R.id.txtlogin);
            toolbar.show(this,"Registro de Conductor",true);
            txtlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(RegisterDriver.this, MainActivity.class);
                    RegisterDriver.this.startActivity(intent);
                    finish();
                }
            });
            btnReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name=edtNombre.getText().toString();
                    lastname=edtApellido.getText().toString();
                   // email=edtCorreo.getText().toString();
                    password=edtContra.getText().toString();
                    number=edtNumero.getText().toString();
                    identi=edtIdenti.getText().toString();

                    if(!name.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !password.isEmpty()&& !number.isEmpty()&& !identi.isEmpty()){
                        if(password.length() >=6){
                            registerUser(email,lastname,name,password,number,identi);
                        }else{
                            Toast.makeText(RegisterDriver.this, "El password debe tener al menos 6 caracteres.",Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Toast.makeText(RegisterDriver.this, "Complete todos los campos.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        private void create(Driver driver){
            mDriverProviders.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                       String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        //Driver driver = new Client(id,email,lastname,name,password,number,identi ,placa,marca,modelo,año,puertas,edad,categoriaD,vigenciaDriver,vigenciaTarjetap,vigenciaSoat, antecedentes,vigenciaTecno);
                       // create(driver);

                        Toast.makeText(RegisterDriver.this,"El conductor se creo correctamente",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterDriver.this,"El conductor no pudo ser creado",Toast.LENGTH_SHORT).show();
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
