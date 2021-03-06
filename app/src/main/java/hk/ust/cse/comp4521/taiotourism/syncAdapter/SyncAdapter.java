//# COMP 4521    #  AMANDA SAMBATH                            20363161          asambath@connect.ust.hk
//# COMP 4521    #  ETHAN RAYMOND                              20363147          eoraymond@connect.ust.hk
//# COMP 4521    #  NICKOLAS JARZEMBOWSKI               2033 8324          njj@connect.ust.hk
//# COMP 4521    #  LOIC BRUNO STEPAHNE TURIN        20363264          lbsturin@connect.ust.hk

package hk.ust.cse.comp4521.taiotourism.syncAdapter;

/**
 * Created by ethanraymond on 5/3/16.
 */

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hk.ust.cse.comp4521.taiotourism.Constants;
import hk.ust.cse.comp4521.taiotourism.TaiODataContract;
import hk.ust.cse.comp4521.taiotourism.TaiODataProvider;

import static android.provider.BaseColumns._ID;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SyncAdapter";

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    Context mContext;

    private String serverUrl = "http://52.221.252.163:3000/api";
    private String getUpdateUrl = "/PointOfInterests/getUpdate?date=";
    private String getGenInfoUpdateUrl = "/GeneralInfos/getUpdate?date=";

    private SharedPreferences mSharedPreferences;
    private Date lastUpdate;
    private SimpleDateFormat formatter;
    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize,boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContext = context;
        //might not need this because we have a content provider
        mContentResolver = mContext.getContentResolver();


        formatter = new SimpleDateFormat(Constants.DATE_FORMAT);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {

        // naive implementation, delete replace everything

        Log.i(TAG, "starting sync");

        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        Log.i("formatter",formatter.getTimeZone().toString());

        mSharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE);

        try {
            lastUpdate = formatter.parse(mSharedPreferences.getString(Constants.SHAREDPREF_LAST_UPDATE,Constants.LAST_UPDATE_DEFAULT));
        } catch (ParseException e) {
            //There was an error when retrieving the date so we just reset it
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            lastUpdate = new Date();
            editor.putString(Constants.SHAREDPREF_LAST_UPDATE,formatter.format(new Date()));
            editor.commit();
            Log.i(TAG,"Updated last update: " + formatter.format(lastUpdate));
            e.printStackTrace();
        }

        Log.i(TAG,"Last update: " + formatter.format(lastUpdate));

        SyncResult result = new SyncResult();

        try {
            Looper.prepare();
            Log.i(TAG, "trying get all places");

            getUpdate(formatter.format(lastUpdate),getUpdateUrl);
            getUpdate(formatter.format(lastUpdate),getGenInfoUpdateUrl);

            setLastUpdatePreference();
        }
        catch (Exception e) {
            syncResult.hasHardError();
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, "done sync");
    }

    private void setLastUpdatePreference() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.SHAREDPREF_LAST_UPDATE, formatter.format(new Date()));
        editor.commit();
        Log.i(TAG,"Updated last update: " + formatter.format(lastUpdate));
    }

    private void deletePoi(Long id) {
        try {
            mContentResolver.delete(TaiODataProvider.POIENTRY_URI,TaiODataContract.POIEntry._ID+"=?",new String[] {id.toString()});
            Log.i("Poi deleted",id.toString());
        } catch (Exception e) {
            Log.e("Poi failed to delete", e.toString());
        }
    }

    private void deleteGeneralInfo() {
        try {
            mContentResolver.delete(TaiODataProvider.GENERALINFO_URI,null,null);
            Log.i("General Info","erased");
        } catch (Exception e) {
            Log.e("General Info","failed to erase");
        }
    }

    private void insertPOI(POIModel poi) {
        Log.i(TAG,"Constructing query arguments for poi id: " + poi.getId());
        String[] mProjection = {TaiODataContract.POIEntry._ID};

        // Defines a string to contain the selection clause
        String mSelectionClause = TaiODataContract.POIEntry._ID + " = ?";
        String mSelectionArgs[] = {poi.getId().toString()};

        Cursor mCursor = mContentResolver.query(TaiODataProvider.POIENTRY_URI,mProjection,mSelectionClause,mSelectionArgs,"");
        Log.i(TAG,"Checked database for entry");
        Log.i(TAG,"cursor: " + ((Integer) mCursor.getCount()).toString());
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(_ID));
            deletePoi(id);
        }
        final ContentValues values = new ContentValues();
        values.put(TaiODataContract.POIEntry.COLUMN_NAME, poi.getName());
        values.put(TaiODataContract.POIEntry.COLUMN_NAME_CH, poi.getNameCH());
        values.put(TaiODataContract.POIEntry._ID, ((Double) poi.getId()).intValue());
        values.put(TaiODataContract.POIEntry.COLUMN_LATITUDE, poi.getCoordinates().getLat());
        values.put(TaiODataContract.POIEntry.COLUMN_LONGITUDE, poi.getCoordinates().getLng());
        values.put(TaiODataContract.POIEntry.COLUMN_CATEGORY, poi.getCategory());
        values.put(TaiODataContract.POIEntry.COLUMN_TOUR_ORDER, poi.getTourOrder());
        values.put(TaiODataContract.POIEntry.COLUMN_DESCRIPTION, poi.getDescription());
        values.put(TaiODataContract.POIEntry.COLUMN_DESCRIPTION_CH, poi.getDescriptionCH());
        values.put(TaiODataContract.POIEntry.COLUMN_RATING, poi.getRating());
        values.put(TaiODataContract.POIEntry.COLUMN_VISIT_COUNTER, poi.getCounter());
        values.put(TaiODataContract.POIEntry.COLUMN_LAST_MODIFIED, poi.getLastModified());
        values.put(TaiODataContract.POIEntry.COLUMN_PICTURE_URL, poi.getPictureUrl());
        Uri contractUri = mContentResolver.insert(TaiODataProvider.POIENTRY_URI, values);
        long rawContactId = ContentUris.parseId(contractUri);
        Log.i("Inserted ID: ", String.valueOf(rawContactId));
        mCursor.close();
    }

    private void insertGeneralInfo(GeneralInfoModel genInfo) {
        Log.i(TAG, "Inserting:  " + genInfo.getTaiODescription() + ", " + genInfo.getYwcaDesciption());
        deleteGeneralInfo();
        final ContentValues values = new ContentValues();
        values.put(TaiODataContract.GeneralInfo.COLUMN_NAME, "General Information");
        values.put(TaiODataContract.GeneralInfo.COLUMN_TAIO_INFO, genInfo.getTaiODescription());
        values.put(TaiODataContract.GeneralInfo.COLUMN_TAIO_INFO_CH, genInfo.getTaiODescriptionCH());
        values.put(TaiODataContract.GeneralInfo.COLUMN_YWCA_INFO, genInfo.getYwcaDesciption());
        values.put(TaiODataContract.GeneralInfo.COLUMN_YWCA_INFO, genInfo.getYwcaDesciptionCH());
        Uri contractUri = mContentResolver.insert(TaiODataProvider.GENERALINFO_URI, values);
        long rawContactId = ContentUris.parseId(contractUri);
        Log.i("Inserted ID: ", String.valueOf(rawContactId));
    }

    public void getData() {
        InputStream inputStream;
        HttpURLConnection urlConnection;
        Integer result = 0;
        try {
                /* forming th java.net.URL object */
            Log.e(TAG, "server ulr is "+serverUrl + "/POIs");
            URL url = new URL(serverUrl + "/POIs");
            urlConnection = (HttpURLConnection) url.openConnection();

                 /* optional request header */
            urlConnection.setRequestProperty("Content-Type", "application/json");

                /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json");

                /* for Get request */
            urlConnection.setRequestMethod("GET");
            // urlConnection.connect();
            int statusCode = urlConnection.getResponseCode();
            Log.i(TAG, "statusCode is "+statusCode);

                /* 200 represents HTTP OK */
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                parseJson(response, getUpdateUrl);
            } else {
                Log.e(TAG, "Failed to fetch data!");
            }
        } catch (Exception e) {
            Log.d(TAG + "getdataerror", e.getLocalizedMessage());
        }
    }

    private void getUpdate(String lastUpdate, String getUpdateUrl) {
        InputStream inputStream;
        HttpURLConnection urlConnection;
        Integer result = 0;
        //We need to do this because loopback formats dates differently than Java can
        //lastUpdate = lastUpdate.substring(0,10) + "T" + lastUpdate.substring(10,18) + "." + lastUpdate.substring(19,lastUpdate.length()) + "Z";
        String serverUrl = this.serverUrl + getUpdateUrl + lastUpdate.replace(" ","%20").replace(":","%3A").replace("+","%2B");
        //String serverUrl = "http://52.221.252.163:3000/api/PointOfInterests/getUpdate?date=Mon%20May%2021%2021%3A12%3A05%20GMT%2B08%3A00%202016";
        try {
                /* forming th java.net.URL object */
            Log.e(TAG, "server update ulr is "+serverUrl);
            //Log.e(TAG, "server update ulr1 is "+serverUrl1);
            URL url = new URL(serverUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

                 /* optional request header */
            urlConnection.setRequestProperty("Content-Type", "application/json");

                /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json");

                /* for Get request */
            urlConnection.setRequestMethod("GET");
            // urlConnection.connect();
            int statusCode = urlConnection.getResponseCode();
            Log.i(TAG, "statusCode is "+statusCode);

                /* 200 represents HTTP OK */
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                parseJson(response, getUpdateUrl);
            } else {
                Log.e(TAG, "Failed to fetch data!");
            }
        } catch (Exception e) {
            Log.d(TAG + "updatepoi", e.toString());
        }
    }

    private void parseJson(String result, String url) {

        Log.i(TAG + "parse json", "Result from server is " + result);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        if (url.equals(getUpdateUrl)) {
            parseResult(Arrays.asList(gson.fromJson(result, POIModel[].class)));
        }
        else if (url.equals(getGenInfoUpdateUrl)) {
            parseGenInfoResult(Arrays.asList(gson.fromJson(result, GeneralInfoModel[].class)));
        }

    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

            /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }
        return result;
    }

    private void parseResult(List<POIModel> models) {

        Log.i(TAG + "parse result", "Result from server is " + models.toString());

        for (int i = 0; i < models.size(); ++i) {
            insertPOI(models.get(i));
        }

    }

    private void parseGenInfoResult(List<GeneralInfoModel> models) {

        Log.i(TAG + "parse result", "Result from server is " + models.toString());

        for (int i = 0; i < models.size(); ++i) {
            insertGeneralInfo(models.get(i));
        }

    }

}

