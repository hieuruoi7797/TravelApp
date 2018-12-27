package com.example.sonng266.travelapp.models;

public class PlaceModel {
    public LocationModel location;
    public String name;

    public PlaceModel() {
    }

    public PlaceModel(LocationModel location, String name) {
        this.location = location;
        this.name = name;
    }

    public LocationModel getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PlaceModel{" +
                "location=" + location +
                ", name='" + name + '\'' +
                '}';
    }
}
