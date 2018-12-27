package com.example.sonng266.travelapp.eventBus;

/**
 * Created by sonng266 on 19/12/2017.
 */

public class ShowDialogWrongWayEvent {
    public boolean isShow;
    public String nameSender;

    public ShowDialogWrongWayEvent(boolean isShow, String nameSender) {
        this.isShow = isShow;
        this.nameSender = nameSender;
    }
}
