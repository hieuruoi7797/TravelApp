package com.example.sonng266.travelapp.ultis.notification_firebase;

public class NotificationDataModel {
    final String messageType;
    final String uIdSending;

    public NotificationDataModel(String messageType, String uIdSending) {
        this.messageType = messageType;
        this.uIdSending = uIdSending;
    }
}
