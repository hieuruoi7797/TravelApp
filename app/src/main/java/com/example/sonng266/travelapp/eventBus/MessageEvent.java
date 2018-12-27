package com.example.sonng266.travelapp.eventBus;

public class MessageEvent {
    public  String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
