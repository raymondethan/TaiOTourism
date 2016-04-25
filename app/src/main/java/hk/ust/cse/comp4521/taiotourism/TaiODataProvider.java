package hk.ust.cse.comp4521.taiotourism;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import static hk.ust.cse.comp4521.taiotourism.TaiODataContract.*;

/**
 * Created by ethanraymond on 4/25/16.
 */
public class TaiODataProvider extends ContentProvider {

    private DatabaseHelper db;
    private static final String PROVIDER_NAME = "hk.ust.cse.comp4521.taiotourism";

    //set to the correspondng table name?
    private static final String PATH_POI = "POI";
    private static final String PATH_REVIEW = "Review";
    private static final String PATH_GENERALINFO = "GeneralInfo";

    public static final Uri POIENTRY_URI = Uri.parse("content://" + PROVIDER_NAME + "/" + PATH_POI);
    public static final Uri REVIEWENTRY_URI = Uri.parse("content://" + PROVIDER_NAME + "/" + PATH_REVIEW);
    public static final Uri GENERALINFO_URI = Uri.parse("content://" + PROVIDER_NAME + "/" + PATH_GENERALINFO);

    private static final int POI_ENTRY = 1;
    private static final int REVIEW_ENTRY = 2;
    private static final int GENERALINFO = 3;
    private static final int POI_ENTRY_ID = 4;
    private static final int REVIEW_ENTRY_ID = 5;
    private static final int GENERALINFO_ID = 6;

    private static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, PATH_POI, POI_ENTRY);
        uriMatcher.addURI(PROVIDER_NAME, PATH_REVIEW, REVIEW_ENTRY);
        uriMatcher.addURI(PROVIDER_NAME, PATH_GENERALINFO, GENERALINFO);
        uriMatcher.addURI(PROVIDER_NAME, PATH_POI + "/#", POI_ENTRY_ID);
        uriMatcher.addURI(PROVIDER_NAME, PATH_REVIEW + "/#", REVIEW_ENTRY_ID);
        uriMatcher.addURI(PROVIDER_NAME, PATH_GENERALINFO + "/#", GENERALINFO_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        db = new DatabaseHelper(context);

        //TODO: move this to anohter method?
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
//        db = dbHelper.getWritableDatabase();
//        return (db == null)? false:true;
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = uriMatcher.match(uri);

        switch (uriType) {
            case POI_ENTRY:
                queryBuilder.setTables(POIEntry.TABLE_NAME);
                break;
            case REVIEW_ENTRY:
                queryBuilder.setTables(ReviewEntry.TABLE_NAME);
                break;
            case GENERALINFO:
                queryBuilder.setTables(GeneralInfo.TABLE_NAME);
                break;
            case POI_ENTRY_ID:
                queryBuilder.appendWhere(POIEntry._ID + "="
                        + uri.getLastPathSegment());
                break;
            case REVIEW_ENTRY_ID:
                queryBuilder.appendWhere(ReviewEntry._ID + "="
                        + uri.getLastPathSegment());
                break;
            case GENERALINFO_ID:
                queryBuilder.appendWhere(GeneralInfo._ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = this.db.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db,
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = this.db.getWritableDatabase();
        String return_uri = "";
        long id = 0;
        switch (uriType) {
            case POI_ENTRY:
                id = db.insert(POIEntry.TABLE_NAME, null, values);
                return_uri = PATH_POI;
                break;
            case REVIEW_ENTRY:
                id = db.insert(ReviewEntry.TABLE_NAME, null, values);
                return_uri = PATH_REVIEW;
                break;
            case GENERALINFO:
                id = db.insert(GeneralInfo.TABLE_NAME, null, values);
                return_uri = PATH_GENERALINFO;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(return_uri + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
