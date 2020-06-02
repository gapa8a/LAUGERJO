package com.example.laugerjo.providers;

import android.content.Context;

import com.example.laugerjo.utils.CompressorBitmapImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class ImagesProvider {

    private StorageReference mStorage;

    public ImagesProvider(String ref){
    mStorage= FirebaseStorage.getInstance().getReference().child(ref);
    }

    public UploadTask saveImage(Context context, File image , String idUser){
        byte [] imgByte = CompressorBitmapImage.getImage(context, image.getPath(), 500, 500);
        final StorageReference storage = mStorage.child(idUser+ ".jpg");
        mStorage=storage;
        UploadTask uploadTask = storage.putBytes(imgByte);
        return uploadTask;
    }

    public StorageReference getStorage() {
        return mStorage;
    }
}
