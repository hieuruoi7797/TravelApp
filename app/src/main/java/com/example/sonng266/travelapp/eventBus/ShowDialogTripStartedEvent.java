package com.example.sonng266.travelapp.eventBus;

/**
 * Created by sonng266 on 13/12/2017.
 */

public class ShowDialogTripStartedEvent {
    public boolean isShow;

    public ShowDialogTripStartedEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }
}
