package hk.ust.cse.comp4521.taiotourism;

import android.provider.BaseColumns;

/**
 * Created by ethanraymond on 4/25/16.
 */
public class TaiODataContract {

    public static final String AUTHORITY = "hk.ust.cse.comp4521.taiotourism.provider";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaiODataContract() {}

    /* Inner class that defines the table contents */
    public static abstract class POIEntry implements BaseColumns {
        public static final String TABLE_NAME = "POI";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NAME_CH = "nameCH";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_TOUR_ORDER = "tourOrder";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DESCRIPTION_CH = "descriptionCH";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OPENING_HOURS = "openingHours";
        public static final String COLUMN_VISIT_COUNTER = "counter";
        public static final String COLUMN_LAST_MODIFIED = "lastModified";
        public static final String COLUMN_PICTURE_URL = "pictureUrl";
    }

    public static abstract class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "Review";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_DATE = "date";
    }

    public static abstract class GeneralInfo implements BaseColumns {
        public static final String TABLE_NAME = "GeneralInfo";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_YWCA_INFO = "ywcaDesciption";
        public static final String COLUMN_YWCA_INFO_CH = "ywcaDesciptionCH";
        public static final String COLUMN_TAIO_INFO = "taiODescription";
        public static final String COLUMN_TAIO_INFO_CH = "taiODescriptionCH";
    }

    //We might not need this abstract class
    public static abstract class Administrator implements BaseColumns {
        public static final String TABLE_NAME = "Administrator";
        public static final String COLUMN_LOGIN = "login";
        public static final String COLUMN_PASSWORD = "password";
    }

}
