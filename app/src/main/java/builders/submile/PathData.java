package builders.submile;

/**
 * Created by sharatk on 1/14/2017.
 */

public class PathData {
    private String mobileNumber;
    private String address;
    private GeoLocation [] wayPoints;
    private GeoLocation start;
    private GeoLocation end;

    public PathData(
            String aMobileNumber,
            String aAddress,
            GeoLocation [] aWayPoints,
            GeoLocation aStart,
            GeoLocation aEnd) {
        mobileNumber = aMobileNumber;
        address = aAddress;
        wayPoints = aWayPoints;
        start = aStart;
        end = aEnd;
    }
}
