//# COMP 4521    #  AMANDA SAMBATH                            20363161          asambath@connect.ust.hk
//# COMP 4521    #  ETHAN RAYMOND                              20363147          eoraymond@connect.ust.hk
//# COMP 4521    #  NICKOLAS JARZEMBOWSKI               2033 8324          njj@connect.ust.hk
//# COMP 4521    #  LOIC BRUNO STEPAHNE TURIN        20363264          lbsturin@connect.ust.hk

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
        if (lat < -85 || lat > 85) {
            //set default value because lat is out of bounds
            this.lat = 22.254563;
        }
        this.lat = lat;
    }

    public double getLng() { return lng; }

    public void setLng(double lng) {
        if (lng < -180 || lng > 180) {
            //set default value because lng is out of bounds
            this.lng = 113.864075;
        }
        this.lat = lat;
    }
}
