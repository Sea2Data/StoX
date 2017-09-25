/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.imrmap.utils;

/**
 * Assume earth is flat. Approximation that takes speed into account. Useful for
 * testing distances between stations.
 *
 * @author aasmunds
 */
public class FlatEarthDistance {

    public static double distanceInNM(double lat1, double lng1,
            double lat2, double lng2) {
        return distanceInM(lat1, lng1, lat2, lng2) / 1852;
    }

    //returns distance in meters
    public static double distanceInM(double lat1, double lng1,
            double lat2, double lng2) {
        double a = (lat1 - lat2) * FlatEarthDistance.distPerLat(lat1);
        double b = (lng1 - lng2) * FlatEarthDistance.distPerLng(lat1);
        return Math.sqrt(a * a + b * b);
    }

    private static double distPerLng(double lat) {
        return 0.0003121092 * Math.pow(lat, 4)
                + 0.0101182384 * Math.pow(lat, 3)
                - 17.2385140059 * lat * lat
                + 5.5485277537 * lat + 111301.967182595;
    }

    private static double distPerLat(double lat) {
        return -0.000000487305676 * Math.pow(lat, 4)
                - 0.0033668574 * Math.pow(lat, 3)
                + 0.4601181791 * lat * lat
                - 1.4558127346 * lat + 110579.25662316;
    }

    /**
     * Calculates geodetic distance between two points specified by
     * latitude/longitude using equirectangular approximation
     *
     * @param lat1 first point latitude in decimal degrees
     * @param lon1 first point longitude in decimal degrees
     * @param lat2 second point latitude in decimal degrees
     * @param lon2 second point longitude in decimal degrees
     * @return 
     *
     * @returns distance in Km between points
     *
     * @see http://www.movable-type.co.uk/scripts/latlong-vincenty.html
     */

    public static double equiRecApprox(double lat1, double lon1, double lat2,
            double lon2) {

        //final double R = 6371.0; // earths mean radius in km

        // convert coordinates to radians
        //lat1 = Math.toRadians(lat1);
        //lon1 = Math.toRadians(lon1);
        //lat2 = Math.toRadians(lat2);
        //lon2 = Math.toRadians(lon2);

        double x = (lon2 - lon1) * 60 * Math.cos(Math.toRadians((lat1 + lat2) / 2));
        double y = (lat2 - lat1) * 60;
        return Math.sqrt(x * x + y * y);// * R;
        //return resInKM/* / 1.852*/;

    }

}
