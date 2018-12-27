package com.example.sonng266.travelapp.ultis.map_direction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class DirectionHandler {
    public static LatLng fake1 = new LatLng(21.022205735362007, 105.81567924519466);
    public static LatLng fake2 = new LatLng(21.02066019886197, 105.81452103142932);

    private static List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    public static List<RouteModel> getListRoute(DirectionResponse directionResponse) {
        List<RouteModel> routeModelList = new ArrayList<>();
        List<DirectionResponse.RouteJSON> routeJSONList = directionResponse.routes;

        for (DirectionResponse.RouteJSON routeJSON : routeJSONList) {
            RouteModel routeModel = new RouteModel();

            routeModel.distance = routeJSON.legs.get(0).distance.text;
            routeModel.duration = routeJSON.legs.get(0).duration.text;

            routeModel.startLocation = new LatLng(routeJSON.legs.get(0).start_location.lat,
                    routeJSON.legs.get(0).start_location.lng);
            routeModel.endLocation = new LatLng(routeJSON.legs.get(0).end_location.lat,
                    routeJSON.legs.get(0).end_location.lng);

            routeModel.points = decodePolyLine(routeJSON.overview_polyline.points);
            routeModelList.add(routeModel);
        }
        return routeModelList;
    }

    public static List<RouteModel> getListRouteDetail(DirectionResponse directionResponse) {
        List<RouteModel> routeModelList = new ArrayList<>();
        List<DirectionResponse.RouteJSON> routeJSONList = directionResponse.routes;

        for (DirectionResponse.RouteJSON routeJSON : routeJSONList) {
            for (DirectionResponse.RouteJSON.LegsJSON legsJSON : routeJSON.legs) {
                for (DirectionResponse.RouteJSON.LegsJSON.StepsJSON stepsJSON : legsJSON.steps) {
                    RouteModel routeModel = new RouteModel();

//                    routeModel.distance = routeJSON.legs.get(0).distance.text;
//                    routeModel.duration = routeJSON.legs.get(0).duration.text;
//
//                    routeModel.startLocation = new LatLng(routeJSON.legs.get(0).start_location.lat,
//                            routeJSON.legs.get(0).start_location.lng);
//                    routeModel.endLocation = new LatLng(routeJSON.legs.get(0).end_location.lat,
//                            routeJSON.legs.get(0).end_location.lng);

                    routeModel.points = decodePolyLine(stepsJSON.polyline.points);
                    routeModelList.add(routeModel);
                }
            }
        }
        return routeModelList;
    }

    public static void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
