package com.example.laugerjo.providers;

import android.widget.Toast;

import com.example.laugerjo.activities.client.Register;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class authProviders {
    FirebaseAuth Aunte;
    public authProviders(){
        Aunte = FirebaseAuth.getInstance();
    }
    public Task<AuthResult> register(String email, String password){

        //if(!email.equals(Aunte.getCurrentUser().getEmail())){
        return Aunte.createUserWithEmailAndPassword(email, password);
        /*}else{
            return null;
        }*/
    }

    public Task<AuthResult> login(String email, String password){
        return Aunte.signInWithEmailAndPassword(email, password);
    }
    public  void logout(){
        Aunte.signOut();
    }
    public String getId(){
        return Aunte.getCurrentUser().getUid();
    }
    public boolean existSession(){
        boolean exist = false;
        if(Aunte.getCurrentUser() !=null){
            exist =true;
        }
        return exist;
    }
}
