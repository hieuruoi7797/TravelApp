package com.example.sonng266.travelapp.ultis.notification_firebase;

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
