package com.example.sonng266.travelapp.ultis.map_direction;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteModel {
    public String distance;
    public String duration;
    public LatLng endLocation;
    public LatLng startLocation;

    public List<LatLng> points;
}
