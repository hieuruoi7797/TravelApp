package com.example.sonng266.travelapp.eventBus;

/**
 * Created by sonng266 on 15/12/2017.
 */

public class ShowDialogSOSEvent {
    public boolean isShow;
    public String nameSender;

    public ShowDialogSOSEvent(boolean isShow, String nameSender) {
        this.isShow = isShow;
        this.nameSender = nameSender;
    }
}
