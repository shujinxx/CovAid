package com.ganshujin.androidrealtimelocation.Remote;

import android.app.DownloadManager;

import com.ganshujin.androidrealtimelocation.Model.MyResponse;
import com.ganshujin.androidrealtimelocation.Model.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
        "Content-Type:application/json",
            "Authorization: key=AAAA8XX8bPM:APA91bG-LjgBvFxuPTMyAwrP0fTf0zzYMveOmdaas2-MPGruVGCsak0dZV7USgZs8O01o5IxCBUaM62qLt5stQw4kMLkI-5zkXIXLSbtcE8FeaZ6QwblSlpORP9Yhol39af0VbSSfH0U"
    })
    @POST("fcm/send")
    Observable<MyResponse> sendFriendRequestToUser(@Body Request body);

}
