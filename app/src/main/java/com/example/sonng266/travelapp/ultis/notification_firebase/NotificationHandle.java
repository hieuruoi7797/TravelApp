package com.example.sonng266.travelapp.ultis.notification_firebase;

import android.app.Activity;
import android.widget.Toast;

import com.example.sonng266.travelapp.ultis.RetrofitInstance;
import com.example.sonng266.travelapp.ultis.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHandle {
    public static void sendingNotification(SendingNotificationModel sendingNotificationModel, final Activity activity) {
        RetrofitService retrofitService = RetrofitInstance.getInstanceNotificationFirebase().create(RetrofitService.class);
        retrofitService.pushNotification(sendingNotificationModel).enqueue(new Callback<SendingNotificationResponse>() {
            @Override
            public void onResponse(Call<SendingNotificationResponse> call, Response<SendingNotificationResponse> response) {
                Toast.makeText(activity, "Thông báo đã được gửi đi!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SendingNotificationResponse> call, Throwable t) {
                Toast.makeText(activity, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
