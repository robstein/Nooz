package com.nooz.nooz.util;

public class GlobeTrigonometry {
	
	public static double EQUATOR_LENGTH = 40075000; // in meters
	public static double EARTH_RADIUS = 6378100; // in meters
	
	public static double distBetween(double lat1, double lng1, double lat2, double lng2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = EARTH_RADIUS * c;

		return dist;
	}
	
	public static double mapWidthInMeters(int screenWidthInPixels, float zoomlevel) {
		return ((EQUATOR_LENGTH / 256) / (Math.pow(2, zoomlevel))) * screenWidthInPixels;
	}
}
