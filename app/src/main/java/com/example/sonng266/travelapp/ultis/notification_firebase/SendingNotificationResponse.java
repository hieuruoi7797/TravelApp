package com.example.sonng266.travelapp.ultis.notification_firebase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendingNotificationResponse {
    @SerializedName("message_id")
    @Expose
    private String messageId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}
