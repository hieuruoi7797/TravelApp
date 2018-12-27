package com.example.sonng266.travelapp.ultis.notification_firebase;

/**
 * Created by sonng266 on 13/12/2017.
 */

public class SendingNotificationModel {
    final String to;
    final NotificationModel notification;
    final NotificationDataModel data;

    public SendingNotificationModel(String to, NotificationModel notification, NotificationDataModel data) {
        this.to = to;
        this.notification = notification;
        this.data = data;
    }
}
