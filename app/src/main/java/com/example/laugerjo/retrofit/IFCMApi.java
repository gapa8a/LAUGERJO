package com.example.laugerjo.retrofit;

import com.example.laugerjo.model.FCMBody;
import com.example.laugerjo.model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HEAD;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({"Content-Type:application/json",
    "Authorization:key=AAAAbqKUgqQ:APA91bHYaTYF-LBzUFE6SQanRB8DPfYt_v6cih_ckaur3dRheVZ3nHGx6mt4gd8T7MlVfwHBNmzuRd6GfPpppx2ek4a1kc3_I-CDeKyJp5ykIqs8kmgFdt__WGj7avztX_kFaTK0DWOk"})
    @POST("fcm/send")
    Call<FCMResponse>send(@Body FCMBody body);
}
