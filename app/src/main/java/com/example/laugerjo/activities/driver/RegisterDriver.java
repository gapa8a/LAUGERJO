package com.example.laugerjo.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterDriver extends AppCompatActivity {
    private EditText edtPlaca;
    private EditText edtMarca;
    private EditText edtModelo;
    private EditText edtNumerop;
    private EditText edtAnio;
    private EditText edtConduNaci;
    private EditText edtCategoriaD;
    private EditText edtVigenD;
    private EditText edtVigenTp;
    private EditText edtVigenSoat;

    private EditText edtVigenTecno;
    private TextView txtlogin;
    private RadioButton rbSi,rbNo;
    private Button btnReg;
    private String name= "";
    private String id= "";
    private String lastname= "";
    private String email= "";
    private String password= "";
    private String number="";
    private String identi="";
    private String placa="";
    private String marca="";
    private String modelo="";
    private int anio;
    private Date conduNaci;
    private String categoriaD="";
    private Date vigenD;
    private Date vigenTp;
    private Date vigenSoat;
    private String antece="";
    private Date vigenTecno;
    private int numerop;





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
            edtPlaca= findViewById(R.id.edtPlaca);
            edtMarca =findViewById(R.id.edtMarca);
            edtModelo =findViewById(R.id.edtModelo);
            edtNumerop =findViewById(R.id.edtNumerop);
            edtAnio =findViewById(R.id.edtAnio);
            edtConduNaci =findViewById(R.id.edtConduNaci);
            edtCategoriaD =findViewById(R.id.edtCategoriaD);
            edtVigenD =findViewById(R.id.edtVigenD);
            edtVigenTp =findViewById(R.id.edtVigenTp);
            edtVigenSoat =findViewById(R.id.edtVigenSoat);

            rbNo =findViewById(R.id.rbNo);
            rbSi =findViewById(R.id.rbSi);
            edtVigenTecno =findViewById(R.id.edtVigenTecno);
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
                    name=getIntent().getStringExtra("name");
                    lastname=getIntent().getStringExtra("lastname");
                    email=getIntent().getStringExtra("email");
                    password=getIntent().getStringExtra("password");
                    number=getIntent().getStringExtra("number");
                    identi=getIntent().getStringExtra("identi");
                    placa = edtPlaca.getText().toString();
                    marca =edtMarca.getText().toString();
                    modelo =edtModelo.getText().toString();
                    numerop= Integer.parseInt(edtNumerop.getText().toString()) ;
                    anio=Integer.parseInt(edtAnio.getText().toString());
                    try {
                        conduNaci =deStringADate(edtConduNaci.getText().toString());
                        vigenD = deStringADate(edtVigenD.getText().toString());
                        vigenTp = deStringADate(edtVigenTp.getText().toString());
                        vigenSoat = deStringADate(edtVigenSoat.getText().toString());
                        vigenTecno = deStringADate(edtVigenTecno.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    categoriaD =edtCategoriaD.getText().toString();


                    if (rbSi.isChecked()==true) {
                        antece="Si";
                    } else
                    if (rbNo.isChecked()==true) {
                        antece="No";
                    }


                    if(!placa.isEmpty() && !marca.isEmpty() && !modelo.isEmpty()&& numerop>=4 && anio>=2010 &&  !categoriaD.isEmpty()&& !antece.isEmpty()){

                        registerUser(email,lastname,name,password,number,identi,placa,marca,modelo,anio,numerop,conduNaci,categoriaD,vigenD,vigenTp,vigenSoat,antece,vigenTecno);

                    }else{
                        Toast.makeText(RegisterDriver.this, "Complete todos los campos.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    public static Date deStringADate(String fechaString) throws ParseException {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaDate = (Date)formatoFecha.parse(fechaString);
        return fechaDate;

    }



    private void create(Driver driver){
            mDriverProviders.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                       String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Driver driver = new Driver(id,email,lastname,name,password,number,identi ,placa,marca,modelo,anio,numerop,conduNaci,categoriaD,vigenD,vigenTp,vigenSoat,antece,vigenTecno);
                        create(driver);

                        Toast.makeText(RegisterDriver.this,"El conductor se creo correctamente",Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(RegisterDriver.this,"El conductor no pudo ser creado",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        private void registerUser(final String email, final String lastname,final String name ,final String password,final String number,final String identi,final String placa, final String marca,final String modelo,final int anio,final int numerop,final Date conduNaci,final String categoriaD, final Date vigenD,final Date vigenTp,final Date vigenSoat,final String antece,final Date vigenTecno) {
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

                            Driver driver = new Driver(id,email,lastname,name,password,number,identi,placa,marca,modelo,anio,numerop,conduNaci,categoriaD,vigenD ,vigenTp,vigenSoat,antece,vigenTecno);
                            create(driver);
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
