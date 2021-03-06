package com.example.sonng266.travelapp.ultis.map_direction;

import java.util.List;

public class DirectionResponse {
    public List<RouteJSON> routes;
    public String status;

    public class RouteJSON {
        public OverviewJSON overview_polyline;
        public List<LegsJSON> legs;

        public class OverviewJSON {
            public String points;
        }

        public class LegsJSON {
            public DistanceJSON distance;
            public DurationJSON duration;
            public EndLocationJSON end_location;
            public StartLocationJSON start_location;
            public List<StepsJSON> steps;

            public class DistanceJSON {
                public String text;
            }

            public class DurationJSON {
                public String text;
            }

            public class EndLocationJSON {
                public double lat;
                public double lng;
            }

            public class StartLocationJSON {
                public double lat;
                public double lng;
            }

            public class StepsJSON {
                public PolylineJSON polyline;

                public class PolylineJSON {
                    public String points;
                }
            }
        }
    }
}
