package com.example.sonng266.travelapp.eventBus;

public class ShowDialogTripStartedEvent {
    public boolean isShow;

    public ShowDialogTripStartedEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }
}
