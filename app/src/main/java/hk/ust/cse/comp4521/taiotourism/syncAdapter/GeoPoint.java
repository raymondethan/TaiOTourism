package hk.ust.cse.comp4521.taiotourism.syncAdapter;

/**
 * Created by ethanraymond on 5/13/16.
 */
public class GeoPoint {

    private Double lat;
    private Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        if (lat < -85 || lat > 85) {
            //set default value because lat is out of bounds
            this.lat = 22.254563;
        }
        this.lat = lat;
    }

    public Double getLng() { return lng; }

    public void setLng(Double lng) {
        if (lng < -180 || lng > 180) {
            //set default value because lng is out of bounds
            this.lng = 113.864075;
        }
        this.lng = lng;
    }
}
