package hk.ust.cse.comp4521.taiotourism;

/**
 * Created by amanda on 20/04/16.
 */
public class Item {

    protected String title;
    protected String photo;
    protected String description;
    protected String openingHours;

    public Item(String title, String photo, String description, String openingHours) {
        this.title = title;
        this.photo = photo;
        this.description = description;
        this.openingHours = openingHours;
    }
}
