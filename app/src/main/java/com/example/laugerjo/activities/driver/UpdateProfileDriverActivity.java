package com.example.laugerjo.activities.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.laugerjo.R;
import com.example.laugerjo.activities.client.UpdateProfileActivity;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.model.Client;
import com.example.laugerjo.model.Driver;
import com.example.laugerjo.providers.ClientProvider;
import com.example.laugerjo.providers.DriverProvider;
import com.example.laugerjo.providers.ImagesProvider;
import com.example.laugerjo.providers.authProviders;
import com.example.laugerjo.utils.CompressorBitmapImage;
import com.example.laugerjo.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;

public class UpdateProfileDriverActivity extends AppCompatActivity {
    private ImageView imgViewProfile;
    private Button btnUpdateProfile;

    private EditText edtLastname,edtName,edtPlate,edtBrand,edtModel,edtYear;
    private DriverProvider driverProvider;
    private com.example.laugerjo.providers.authProviders authProviders;
    private ImagesProvider imagesProvider;
    private File imgFile;
    private String img;
     private int ano ;

    private  final int GALLERY_REQUEST = 1;

    private ProgressDialog progressDialog;
    private String lastname, name,plate,brand,model,year;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_driver);
        toolbar.show(this,"Actualizar perfil",true);
        imgViewProfile = findViewById(R.id.imgViewProfile);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        edtName = findViewById(R.id.edtName);
        edtLastname = findViewById(R.id.edtLastname);

        edtPlate = findViewById(R.id.edtPlate);
        edtBrand = findViewById(R.id.edtBrand);
        edtModel = findViewById(R.id.edtModel);
        edtYear = findViewById(R.id.edtYear);

        driverProvider= new DriverProvider();
        authProviders = new authProviders();
        progressDialog  = new ProgressDialog(this);
        imagesProvider = new ImagesProvider("driver_images");
        getDriverInfo();

        imgViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent =new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==GALLERY_REQUEST && resultCode ==RESULT_OK){
            try {
                imgFile = FileUtil.from(this, data.getData());
                imgViewProfile.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR","Mensaje: " +e.getMessage());
            }
        }
    }

    private void getDriverInfo(){
        driverProvider.getDriver(authProviders.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String lastname = dataSnapshot.child("lastname").getValue().toString();
                    String plate = dataSnapshot.child("placa").getValue().toString();
                    String brand = dataSnapshot.child("marca").getValue().toString();
                    String model = dataSnapshot.child("modelo").getValue().toString();
                    String year = dataSnapshot.child("año").getValue().toString();
                    String image="";
                    if(dataSnapshot.hasChild("image")){
                        image=dataSnapshot.child("image").getValue().toString();
                        Picasso.with(UpdateProfileDriverActivity.this).load(image).into(imgViewProfile);
                    }
                    edtName.setText(name);
                    edtLastname.setText(lastname);
                    edtPlate.setText(plate);
                    edtBrand.setText(brand);
                    edtModel.setText(model);
                    edtYear.setText(year);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile() {

        name  = edtName.getText().toString();
        lastname  = edtLastname.getText().toString();
        plate  = edtPlate.getText().toString();
        brand  = edtBrand.getText().toString();
        model  = edtModel.getText().toString();
        year  = edtYear.getText().toString();



        if(!name.equals("") && !lastname.equals("")&&!plate.equals("")&& !brand.equals("")&& !model.equals("") && !year.equals("")&& imgFile != null ) {
            if(plate.length() == 6) {

                int  anio= Calendar.getInstance().get(Calendar.YEAR);

            ano= Integer.parseInt(year);
                if((anio-10)<=ano) {


            progressDialog.setMessage("Espere un momento...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            saveImage();
            }else {
                    Toast.makeText(this, "El automovil debe ser menor a 11 años", Toast.LENGTH_SHORT).show();
                } }else{
                    Toast.makeText(this, "La placa debe contener 6 caracteres", Toast.LENGTH_SHORT).show();

                }

            }else{
            Toast.makeText(this, "Ingrese la imagen y todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        imagesProvider.saveImage(UpdateProfileDriverActivity.this,imgFile,authProviders.getId()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    imagesProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image = uri.toString();
                            Driver driver = new Driver();
                            driver.setImage(image);
                            driver.setName(name);
                            driver.setLastname(lastname);
                            driver.setId(authProviders.getId());
                            driver.setPlaca(plate);
                            driver.setMarca(brand);
                            driver.setModelo(model);
                            driver.setAño(ano);
                            driverProvider.update(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UpdateProfileDriverActivity.this, "La información se actualizo correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(UpdateProfileDriverActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}