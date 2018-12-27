package com.example.sonng266.travelapp.eventBus;

public class ShowDialogWrongWayEvent {
    public boolean isShow;
    public String nameSender;

    public ShowDialogWrongWayEvent(boolean isShow, String nameSender) {
        this.isShow = isShow;
        this.nameSender = nameSender;
    }
}
