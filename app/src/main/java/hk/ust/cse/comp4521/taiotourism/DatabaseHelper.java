package hk.ust.cse.comp4521.taiotourism;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ethanraymond on 4/25/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private	static final String	DATABASE_NAME =	"POIdata";
    private	static final int DATABASE_VERSION =	5;

    private static final String TEXT_TYPE = " TEXT";
    private static final String PRIMARY_KEY = " INTEGER PRIMARY KEY";
    private static final String REAL_TYPE = " REAL";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_POIEntries =
            "CREATE TABLE " + TaiODataContract.POIEntry.TABLE_NAME + " (" +
                    TaiODataContract.POIEntry._ID + PRIMARY_KEY + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_NAME_CH + TEXT_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_LATITUDE + REAL_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_LONGITUDE + REAL_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_CATEGORY + TEXT_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_TOUR_ORDER + INT_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_DESCRIPTION_CH + TEXT_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_RATING + REAL_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_OPENING_HOURS + TEXT_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_VISIT_COUNTER + INT_TYPE + COMMA_SEP +
                    TaiODataContract.POIEntry.COLUMN_LAST_MODIFIED + TEXT_TYPE + " )";

    private static final String SQL_CREATE_ReviewEntries =
            "CREATE TABLE " + TaiODataContract.ReviewEntry.TABLE_NAME + " (" +
                    TaiODataContract.ReviewEntry._ID + PRIMARY_KEY + COMMA_SEP +
                    TaiODataContract.ReviewEntry.COLUMN_RATING + REAL_TYPE + COMMA_SEP +
                    TaiODataContract.ReviewEntry.COLUMN_COMMENT + TEXT_TYPE + " )";

    private static final String SQL_CREATE_GeneralInfo =
            "CREATE TABLE " + TaiODataContract.GeneralInfo.TABLE_NAME + " (" +
                    TaiODataContract.GeneralInfo._ID + PRIMARY_KEY + COMMA_SEP +
                    TaiODataContract.GeneralInfo.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    TaiODataContract.GeneralInfo.COLUMN_INFO + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaiODataContract.POIEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_POIEntries);
        db.execSQL(SQL_CREATE_ReviewEntries);
        db.execSQL(SQL_CREATE_GeneralInfo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaiODataContract.POIEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaiODataContract.ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaiODataContract.GeneralInfo.TABLE_NAME);

        onCreate(db);
    }
}
