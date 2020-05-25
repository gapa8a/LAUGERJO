package com.example.laugerjo.activities.driver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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
import java.util.Calendar;
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
    private RadioButton rbSi,rbNo,rbpNo,rbpSi,rbcSi,rbcNo;
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
    //private int anio;
    private String anio;
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
    private CharSequence title;

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
            edtAnio =findViewById(R.id.edtAnio);
            edtConduNaci =findViewById(R.id.edtConduNaci);
            edtVigenD =findViewById(R.id.edtVigenD);
            edtVigenTp =findViewById(R.id.edtVigenTp);
            edtVigenSoat =findViewById(R.id.edtVigenSoat);
            rbcNo =findViewById(R.id.rbcNo);
            rbcSi =findViewById(R.id.rbcSi);
            rbpNo =findViewById(R.id.rbpNo);
            rbpSi =findViewById(R.id.rbpSi);
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
        edtConduNaci.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDatePickerDialog(1,"Fecha de Nacimiento");

            }
        });
        edtVigenD.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDatePickerDialog(2,"Fecha de vencimiento Licencia de Conducción");
            }
        });

        edtVigenTp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDatePickerDialog(3,"Fecha de vencimiento Tarjeta de Propiedad");
            }
        });

        edtVigenSoat.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDatePickerDialog(4,"Fecha de vencimiento del Soat");
            }
        });

        edtVigenTecno.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDatePickerDialog(5,"Fecha de vencimiento revisión Tecnomecanica");
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
                   // anio=Integer.parseInt(edtAnio.getText().toString());
                    anio=edtAnio.getText().toString();

                    try {
                        conduNaci =deStringADate(edtConduNaci.getText().toString());
                        vigenD = deStringADate(edtVigenD.getText().toString());
                        vigenTp = deStringADate(edtVigenTp.getText().toString());
                        vigenSoat = deStringADate(edtVigenSoat.getText().toString());
                        vigenTecno = deStringADate(edtVigenTecno.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (rbcSi.isChecked()==true) {
                        categoriaD="B1";
                    } else
                    if (rbcNo.isChecked()==true) {
                        categoriaD="";
                    }
                    if (rbpSi.isChecked()==true) {
                        numerop=4;
                    } else
                    if (rbpNo.isChecked()==true) {
                        numerop=2;
                    }

                    if (rbSi.isChecked()==true) {
                        antece="Si";
                    } else
                    if (rbNo.isChecked()==true) {
                        antece="No";
                    }
                    Calendar cal =  Calendar.getInstance();
                    cal.setTime(conduNaci);
                    int anioCondu = cal.get(Calendar.YEAR);

                    int  year= Calendar.getInstance().get(Calendar.YEAR);
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String date = df.format(Calendar.getInstance().getTime());


                    if(!placa.isEmpty() && !marca.isEmpty() && !modelo.isEmpty() && !anio.isEmpty()){
                        if(placa.length() == 6) {
                           if(numerop >=4) {
                               if(categoriaD == "B1") {
                                   int ano = Integer.parseInt(anio);
                                   System.out.println(ano-10);
                                  if((year-10)<=ano) {
                                    if((year-anioCondu)>=18) {
                                        registerUser(email, lastname, name, password, number, identi, placa, marca, modelo, ano, numerop, conduNaci, categoriaD, vigenD, vigenTp, vigenSoat, antece, vigenTecno);
                                    }else{
                                        Toast.makeText(RegisterDriver.this, "Usted debe ser mayor de edad.", Toast.LENGTH_SHORT).show();
                                    }
                                  }else {
                                       Toast.makeText(RegisterDriver.this, "El automovil debe ser menor a 11 años.", Toast.LENGTH_SHORT).show();
                                   }
                                   }else{
                                   Toast.makeText(RegisterDriver.this, "La licencia de conducción debe poseer la categoria B1.",Toast.LENGTH_SHORT).show();

                               }
                               }else{
                                Toast.makeText(RegisterDriver.this, "El vehiculo debe tener minimo 4 puertas.",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(RegisterDriver.this, "La placa debe contener 6 caracteres.",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegisterDriver.this, "Complete todos los campos.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDatePickerDialog(final int campoFecha, final String msg) {
        final Calendar c = Calendar.getInstance();

        final int dia, mes, anio;
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);


        DatePickerDialog newDialog = new DatePickerDialog(RegisterDriver.this, new DatePickerDialog.OnDateSetListener() {

             @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(Calendar.YEAR,year);
                c.set(Calendar.MONTH,month);
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth);


                switch (campoFecha){
                    case 1:edtConduNaci.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        break;
                    case 2:edtVigenD.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        break;
                    case 3:edtVigenTp.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        break;
                    case 4:edtVigenSoat.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        break;
                    case 5:edtVigenTecno.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        break;
                }

            }
        }, anio, mes, dia);
        newDialog.setMessage(msg);
        newDialog.show();
    }

    public static Date deStringADate(String fechaString) throws ParseException {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDate = formatoFecha.parse(fechaString);
        return fechaDate;

    }



    private void create(Driver driver){
            mDriverProviders.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                       String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                       Intent intent =new Intent(RegisterDriver.this,MapDriverActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//Con este add flag se asegura no volver a la pantalla de registro
                        startActivity(intent);
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
                        if (sUser.equals("driver")) {

                            Driver driver = new Driver(id,email,lastname,name,password,number,identi,placa,marca,modelo,anio,numerop,conduNaci,categoriaD,vigenD ,vigenTp,vigenSoat,antece,vigenTecno);
                            create(driver);
                        }

                    }
                }
            });
    }
}
