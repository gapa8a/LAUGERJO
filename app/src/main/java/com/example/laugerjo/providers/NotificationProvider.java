package com.example.laugerjo.providers;

import com.example.laugerjo.model.FCMBody;
import com.example.laugerjo.model.FCMResponse;
import com.example.laugerjo.retrofit.IFCMApi;
import com.example.laugerjo.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url ="https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body){
    return RetrofitClient.getClientObject(url).create(IFCMApi.class).send(body);

    }
}
