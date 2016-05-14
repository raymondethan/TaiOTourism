package hk.ust.cse.comp4521.taiotourism.syncAdapter;

import com.strongloop.android.loopback.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ethanraymond on 5/4/16.
 */
public class POIModel extends Model {

    private String name;
    private GeoPoint coordinates;
    private String category;
    private Integer tourOrder;
    private String description;
    private Double rating;
    private String openingHours;
    private Integer counter;
    private String lastModified;
    //private Integer id;

    public String getName() {
        if (null == name) return "No Name";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {

        if (null == category) return "Unknown Category";
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTourOrder() {
        if (null == tourOrder) return -1;
        return tourOrder;
    }

    public void setTourOrder(Integer tourOrder) {
        this.tourOrder = tourOrder;
    }

    public String getDescription() {
        if (null == description) return "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        if (null == rating) return 0.0;
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getOpeningHours() {
        if (null == openingHours) return "";
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public Integer getCounter() {
        if (null == counter) return 0;
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public String getLastModified() {
        if (null == lastModified) return new Date().toString();
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

//    @Override
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
}
