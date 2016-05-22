package hk.ust.cse.comp4521.taiotourism.syncAdapter;

/**
 * Created by ethanraymond on 5/13/16.
 */
public class GeoPoint {

    private double lat;
    private double lng;

    // Constructor
    public GeoPoint(double lat, double lng) {
        this.setLat(lat);
        this.setLng(lng);
    }

    // Setters and getters
    public double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        // TODO : add check on range
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        // TODO : add check on range
        this.lng = lng;
    }
}
