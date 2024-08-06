package ln.dev.geohash;

import ln.dev.protos.coordinate.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatLonCoordinate {
    public static double LON_MIN = -180L;
    public static double LON_MAX = 180L;
    public static double LAT_MIN = -90L;
    public static double LAT_MAX = 90L;

    private double latitude;

    private double longitude;

    public LatLonCoordinate(Coordinate coordinate) {
        this.latitude = coordinate.getLatitude();
        this.longitude = coordinate.getLongitude();
    }

    private double degreeToRadian(double degree) {
        return degree * (Math.PI / 180);
    }

    public double distanceBetween(LatLonCoordinate other) {
        double deltaLatitude = degreeToRadian(other.latitude - this.latitude);
        double deltaLongitude = degreeToRadian(other.longitude - this.longitude);
        double a = 1
                - Math.cos(deltaLatitude)
                + Math.cos(this.latitude) * Math.cos(other.latitude) * (1 - Math.cos(deltaLongitude));
        return 2 * Math.asin(Math.sqrt(a / 2));
    }

    @Override
    public String toString() {
        return "[Lat=" + this.latitude + ", Lon=" + this.longitude + "]";
    }
}
