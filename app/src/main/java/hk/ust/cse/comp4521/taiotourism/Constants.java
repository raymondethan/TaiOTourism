//# COMP 4521    #  AMANDA SAMBATH                            20363161          asambath@connect.ust.hk
//# COMP 4521    #  ETHAN RAYMOND                              20363147          eoraymond@connect.ust.hk
//# COMP 4521    #  NICKOLAS JARZEMBOWSKI               2033 8324          njj@connect.ust.hk
//# COMP 4521    #  LOIC BRUNO STEPAHNE TURIN        20363264          lbsturin@connect.ust.hk

package hk.ust.cse.comp4521.taiotourism;

/**
 * Created by ethanraymond on 4/18/16.
 */
public final class Constants {
    private Constants() {
    }

    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME = "hk.ust.cse.comp4521.taiotourism";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";


    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String SHARED_PREFERENCES_LANG = "language";    // key for language setting
    public static final String SHARED_PREFERENCES_EN = "en";    // value for English
    public static final String SHARED_PREFERENCES_CH = "ch";    // value for Chinese

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 50;

    public static final String LIST_CATEGORY = "listCategory";
    public static final String CATEGORY_TOUR_STOP = "Tour_Stop";
    public static final String CATEGORY_RESTAURANT = "Restaurant";
    public static final String CATEGORY_FACILITY = "Facility";

    public static final String CATEGORY_TOUR_STOP_TEXT = "Tour Stops";
    public static final String CATEGORY_RESTAURANT_TEXT = "Restaurants";
    public static final String CATEGORY_FACILITY_TEXT = "Facilities";

    public static final float ZOOM_LEVEL = 12;

    public static final String SHAREDPREF_LAST_UPDATE = "LAST_UPDATE";
    public static final String LAST_UPDATE_DEFAULT = "1990-00-10T00:00:00.000Z";
    public static final String DATE_FORMAT = "yyyy-MM-ddhh:mm:ss:SSS";

    // Fragment's arguments
    // used to define the filter setting of the map from the menu
    public static final String ARG_MAP_FILTER_SETTING = "param1";
    public static final String ARG_POI_LATITUDE = "poiLat";
    public static final String ARG_POI_LONGITUDE = "poiLng";
    public static final Double INITIAL_LAT = 22.253155;
    public static final Double INITIAL_LNG = 113.858185;

}
