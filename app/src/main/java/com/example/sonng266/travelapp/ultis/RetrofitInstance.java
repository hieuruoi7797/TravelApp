package com.example.sonng266.travelapp.ultis;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofitMapDirection;
    private static Retrofit retrofitNotificationFirebase;

    public static Retrofit getInstanceMapDirection() {
        if (retrofitMapDirection == null) {
            retrofitMapDirection = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitMapDirection;
    }

    public static Retrofit getInstanceNotificationFirebase() {
        if (retrofitNotificationFirebase == null) {
            retrofitNotificationFirebase = new Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitNotificationFirebase;
    }
}
