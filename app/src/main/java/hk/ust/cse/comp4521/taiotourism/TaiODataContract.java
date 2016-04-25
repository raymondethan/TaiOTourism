package hk.ust.cse.comp4521.taiotourism;

import android.provider.BaseColumns;

/**
 * Created by ethanraymond on 4/25/16.
 */
public class TaiODataContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaiODataContract() {}

    /* Inner class that defines the table contents */
    public static abstract class POIEntry implements BaseColumns {
        public static final String TABLE_NAME = "POI";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COORDINATES = "coordinates";
        public static final String COLUMN_CATEGORY = "coordinates";
        public static final String COLUMN_TOUR_ORDER = "tourOrder";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OPENING_HOURS = "openingHours";
        public static final String COLUMN_VISIT_COUNTER = "counter";
    }

    public static abstract class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "Review";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_COMMENT = "comment";
    }

    public static abstract class GeneralInfo implements BaseColumns {
        public static final String TABLE_NAME = "GeneralInfo";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_INFO = "info";
    }

    //We might not need this abstract class
    public static abstract class Administrator implements BaseColumns {
        public static final String TABLE_NAME = "Administrator";
        public static final String COLUMN_LOGIN = "login";
        public static final String COLUMN_PASSWORD = "password";
    }

}
