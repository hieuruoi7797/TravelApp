package com.example.sonng266.travelapp.models;

import java.util.ArrayList;
import java.util.List;

public class TripModel {
    public String leaderId;
  
    public List<String> memberList;
    public String name;
    public String tId;
    public List<PlaceModel> placeList;
    public boolean isGoing;

    public TripModel() {
    }

    public TripModel(String leaderId, List<String> memberList, String name, String tId, List<PlaceModel> placeList, boolean isGoing) {
        this.leaderId = leaderId;
        this.memberList = memberList;
        this.name = name;
        this.tId = tId;
        this.placeList = placeList;
        this.isGoing = isGoing;
    }

    @Override
    public String toString() {
        return "TripModel{" +
                "leaderId='" + leaderId + '\'' +
                ", memberList=" + memberList +
                ", name='" + name + '\'' +
                ", tId='" + tId + '\'' +
                ", placeList=" + placeList +
                ", isGoing=" + isGoing +
                '}';
    }
}
