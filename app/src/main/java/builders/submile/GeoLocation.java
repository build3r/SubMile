package builders.submile;

/**
 * Created by sharatk on 1/14/2017.
 */

public class GeoLocation {
    public static final double RADIUS = 3963.1676;  // Earth radius in miles

    private double lat;
    private double lng;

    public GeoLocation(double theLatitude, double theLongitude) {
        lat = theLatitude;
        lng = theLongitude;
    }

    // returns the latitude of this geo location
    public double getLatitude() {
        return lat;
    }

    // returns the longitude of this geo location
    public double getLongitude() {
        return lng;
    }

    public double distanceFrom(GeoLocation other) {
        double lat1 = Math.toRadians(lat);
        double long1 = Math.toRadians(lng);
        double lat2 = Math.toRadians(other.lat);
        double long2 = Math.toRadians(other.lng);
        // apply the spherical law of cosines with a triangle composed of the
        // two locations and the north pole
        double theCos = Math.sin(lat1) * Math.sin(lat2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.cos(long1 - long2);
        double arcLength = Math.acos(theCos);
        return arcLength * RADIUS;

    }
}
