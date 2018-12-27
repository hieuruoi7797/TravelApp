package com.example.sonng266.travelapp.eventBus;

/**
 * Created by sonng266 on 26/11/2017.
 */

public class MessageEvent {
    public  String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
