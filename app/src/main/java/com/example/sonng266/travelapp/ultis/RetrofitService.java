package com.example.sonng266.travelapp.ultis;

import com.example.sonng266.travelapp.ultis.map_direction.DirectionResponse;
import com.example.sonng266.travelapp.ultis.notification_firebase.SendingNotificationModel;
import com.example.sonng266.travelapp.ultis.notification_firebase.SendingNotificationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by qklahpita on 11/26/17.
 */

public interface RetrofitService {
    @GET("json")
    Call<DirectionResponse> getDirection(@Query("origin") String origin,
                                         @Query("destination") String destination,
                                         @Query("key") String key);
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAMj6YtOY:APA91bHRVSEAa6-ijjUPlWJdlDiC_TUkrfnZF48xXzfyXwNYYdzNPt-iD5196tGZb2naQP6KjgY1wY2NFlJPRq_iyNB52Rzh3r82bALbLyq9awPx1fWMpW49sCbAUMuUXZgtFZeYToBh"
    })
    @POST("send")
    Call<SendingNotificationResponse> pushNotification(@Body SendingNotificationModel body);
}
