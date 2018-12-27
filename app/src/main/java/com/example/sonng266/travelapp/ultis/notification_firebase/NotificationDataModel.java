package com.example.sonng266.travelapp.ultis.notification_firebase;

/**
 * Created by sonng266 on 15/12/2017.
 */

public class NotificationDataModel {
    final String messageType;
    final String uIdSending;

    public NotificationDataModel(String messageType, String uIdSending) {
        this.messageType = messageType;
        this.uIdSending = uIdSending;
    }
}
