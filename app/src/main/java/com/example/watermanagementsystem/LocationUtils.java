package com.example.watermanagementsystem;

import android.location.Location;
import java.util.ArrayList;
import java.util.List;

public class LocationUtils {
    private static final double MAX_DISTANCE_KM = 10.0; // Show sources within 10km

    public static double calculateDistance(String coordinates1, String coordinates2) {
        try {
            String[] coord1 = coordinates1.split(",");
            String[] coord2 = coordinates2.split(",");

            double lat1 = Double.parseDouble(coord1[0].trim());
            double lon1 = Double.parseDouble(coord1[1].trim());
            double lat2 = Double.parseDouble(coord2[0].trim());
            double lon2 = Double.parseDouble(coord2[1].trim());

            Location loc1 = new Location("");
            loc1.setLatitude(lat1);
            loc1.setLongitude(lon1);

            Location loc2 = new Location("");
            loc2.setLatitude(lat2);
            loc2.setLongitude(lon2);

            return loc1.distanceTo(loc2) / 1000;
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }

    public static List<WaterSource> filterByLocation(List<WaterSource> sources, String userLocation) {
        List<WaterSource> filteredSources = new ArrayList<>();

        for (WaterSource source : sources) {
            double distance = calculateDistance(userLocation, source.getDirections());
            if (distance <= MAX_DISTANCE_KM) {
                filteredSources.add(source);
            }
        }

        return filteredSources;
    }
}