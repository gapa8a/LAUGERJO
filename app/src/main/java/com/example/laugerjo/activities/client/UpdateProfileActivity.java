package com.example.laugerjo.activities.client;

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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.laugerjo.R;
import com.example.laugerjo.includes.toolbar;
import com.example.laugerjo.model.Client;
import com.example.laugerjo.providers.ClientProvider;
import com.example.laugerjo.providers.ImagesProvider;
import com.example.laugerjo.providers.authProviders;
import com.example.laugerjo.utils.CompressorBitmapImage;
import com.example.laugerjo.utils.FileUtil;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.util.GAuthToken;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.security.AuthProvider;

public class UpdateProfileActivity extends AppCompatActivity {

    private ImageView imgViewProfile;
    private Button btnUpdateProfile;

    private EditText edtLastname,edtName;
    private ClientProvider clientProvider;
    private authProviders authProviders;
    private ImagesProvider imagesProvider;
    private File imgFile;
    private String img;

    private  final int GALLERY_REQUEST = 1;

    private ProgressDialog progressDialog;
    private String lastname, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        toolbar.show(this,"Actualizar perfil",true);
        imgViewProfile = findViewById(R.id.imgViewProfile);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        edtName = findViewById(R.id.edtName);
        edtLastname = findViewById(R.id.edtLastname);
        clientProvider = new ClientProvider();
        authProviders = new authProviders();
        imagesProvider = new ImagesProvider("client_images");
        progressDialog  = new ProgressDialog(this);
        getClientInfo();

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

    private void getClientInfo(){
    clientProvider.getClient(authProviders.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                String name = dataSnapshot.child("name").getValue().toString();
                String lastname = dataSnapshot.child("lastname").getValue().toString();
                String image="";
                if(dataSnapshot.hasChild("image")){
                    image=dataSnapshot.child("image").getValue().toString();
                    Picasso.with(UpdateProfileActivity.this).load(image).into(imgViewProfile);
                }
                edtName.setText(name);
                edtLastname.setText(lastname);
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
        if(!name.equals("") && !lastname.equals("") && imgFile != null ) {
            progressDialog.setMessage("Espere un momento...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            saveImage();

        }else{
            Toast.makeText(this, "Ingresa la imagen y todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        imagesProvider.saveImage(UpdateProfileActivity.this,imgFile,authProviders.getId()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    imagesProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                       String image = uri.toString();
                       Client client = new Client();
                       client.setImage(image);
                       client.setName(name);
                       client.setLastname(lastname);
                       client.setId(authProviders.getId());
                       clientProvider.update(client).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               progressDialog.dismiss();
                               Toast.makeText(UpdateProfileActivity.this, "La información se actualizo correctamente", Toast.LENGTH_SHORT).show();
                           }
                       });
                        }
                    });
                }else{
                    Toast.makeText(UpdateProfileActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
