package com.example.sonng266.travelapp.eventBus;

public class ShowDialogSOSEvent {
    public boolean isShow;
    public String nameSender;

    public ShowDialogSOSEvent(boolean isShow, String nameSender) {
        this.isShow = isShow;
        this.nameSender = nameSender;
    }
}
