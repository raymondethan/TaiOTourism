package hk.ust.cse.comp4521.taiotourism.syncAdapter;

import com.strongloop.android.loopback.Model;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTourOrder() {
        return tourOrder;
    }

    public void setTourOrder(Integer tourOrder) {
        this.tourOrder = tourOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public Integer getCounter() {
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
}
