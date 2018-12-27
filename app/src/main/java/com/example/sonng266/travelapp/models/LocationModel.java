package com.example.sonng266.travelapp.models;


public class LocationModel {
    public double lat;
    public double lng;

    public LocationModel() {
    }

    public LocationModel(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
