package com.example.sonng266.travelapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonng266 on 25/11/2017.
 */

public class UserModel {

    public LocationModel location;
    public List<TripModel> tripModelJoinedList = new ArrayList<>();
    public String uid;
    public String name;
    public String uri;

    public UserModel(LocationModel location, List<TripModel> tripModelJoinedList, String uid, String name) {
        this.location = location;
        this.tripModelJoinedList = tripModelJoinedList;
        this.uid = uid;
        this.name = name;
    }

    public UserModel() {
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "location=" + location +
                ", tripModelJoinedList=" + tripModelJoinedList +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public void addTrip(TripModel tripModel){
        if (this.tripModelJoinedList == null) {
            this.tripModelJoinedList = new ArrayList<>();
        }
        this.tripModelJoinedList.add(tripModel);
    }
}
