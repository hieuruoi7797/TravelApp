package com.example.sonng266.travelapp.models;

/**
 * Created by cuonghx on 11/26/2017.
 */

public class MessageModel {
    public String userName;
    public String content;
    public String time;
    public String uId;
    public String Uri;

    public MessageModel() {
    }

    public MessageModel(String userName,String content, String time, String uId,String Uri) {
        this.userName = userName;
        this.content = content;
        this.time = time;
        this.uId = uId;
        this.Uri = Uri;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", uId='" + uId + '\'' +
                '}';
    }
}
