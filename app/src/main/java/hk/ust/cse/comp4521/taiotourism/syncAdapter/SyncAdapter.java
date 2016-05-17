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
import android.widget.ArrayAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.remoting.Repository;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContractItem;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private RestAdapter adapter;

    private String serverUrl = "http://10.0.2.2:3000/api";
    private String getUpdateUrl = "/POIs/getUpdate?date=";

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

        adapter = getLoopBackAdapter();

        formatter = new SimpleDateFormat(Constants.DATE_FORMAT);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {

        // naive implementation, delete replace everything

        Log.i(TAG, "starting sync");

        mSharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE);

        try {
            lastUpdate = formatter.parse(mSharedPreferences.getString(Constants.SHAREDPREF_LAST_UPDATE,Constants.LAST_UPDATE_DEFAULT));
        } catch (ParseException e) {
            //There was an error when retrieving the date so we just reset it
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            lastUpdate = new Date();
            editor.putString(Constants.SHAREDPREF_LAST_UPDATE, formatter.format(new Date()));
            editor.commit();
            Log.i(TAG,"Updated last update: " + lastUpdate);
            e.printStackTrace();
        }

        Log.i(TAG,"Last update: " + lastUpdate);

        SyncResult result = new SyncResult();

        try {
            Looper.prepare();
            Log.i(TAG, "trying get all places");
            getUpdatedPois(formatter.format(lastUpdate));
            setLastUpdatPreference();
            //getAllPlaces();
            //2016-05-10T00:00:00.000Z
        }
        catch (Exception e) {
            syncResult.hasHardError();
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, "done sync");
    }

    private void setLastUpdatPreference() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.SHAREDPREF_LAST_UPDATE, formatter.format(new Date()));
        editor.commit();
        Log.i(TAG,"Updated last update: " + lastUpdate);
    }

    private RestAdapter getLoopBackAdapter() {
        if (adapter == null) {
            // Instantiate the shared RestAdapter. In most circumstances,
            // you'll do this only once; putting that reference in a singleton
            // is recommended for the sake of simplicity.
            // However, some applications will need to talk to more than one
            // server - create as many Adapters as you need.
            adapter = new RestAdapter(mContext.getApplicationContext(), serverUrl);

            // This boilerplate is required for Lesson Three.
//            adapter.getContract().addItem(
//                    new RestContractItem("locations/nearby", "GET"),
//                    "location.nearby");
        }
        Log.i(TAG, "get context: " + getContext().toString());
        Log.i(TAG, "mcontext.getapplicationcontext: " + mContext.getApplicationContext().toString());
        Log.i(TAG, "mcontext: " + mContext.toString());
        Log.i(TAG, "mcontentresolver: " + mContentResolver.toString());
        return adapter;
    }

    private void deletePlaces() throws RemoteException {

        //int retval = mContext.getContentResolver().delete(POIContract.POIProvider.CONTENT_URI, null, null);
        int retval = 0;

        Log.i(TAG, "Rows deleted: "+retval);
    }

    private void deletePoi(Long id) {
        try {
            mContentResolver.delete(TaiODataProvider.POIENTRY_URI,id+"=?",new String[] {id.toString()});
            Log.i("Poi deleted",id.toString());
        } catch (Exception e) {
            Log.e("Poi failed to delete", e.toString());
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
        } else {
            Log.i(TAG, "Inserting:  " + poi.getName() + ", " + poi.getDescription());
            final ContentValues values = new ContentValues();
            values.put(TaiODataContract.POIEntry.COLUMN_NAME, poi.getName());
            values.put(TaiODataContract.POIEntry._ID, ((Double) poi.getId()).intValue());
            values.put(TaiODataContract.POIEntry.COLUMN_LATITUDE, poi.getCoordinates().getLat());
            values.put(TaiODataContract.POIEntry.COLUMN_LONGITUDE, poi.getCoordinates().getLng());
            values.put(TaiODataContract.POIEntry.COLUMN_CATEGORY, poi.getCategory());
            values.put(TaiODataContract.POIEntry.COLUMN_TOUR_ORDER, poi.getTourOrder());
            values.put(TaiODataContract.POIEntry.COLUMN_DESCRIPTION, poi.getDescription());
            values.put(TaiODataContract.POIEntry.COLUMN_RATING, poi.getRating());
            values.put(TaiODataContract.POIEntry.COLUMN_VISIT_COUNTER, poi.getCounter());
            values.put(TaiODataContract.POIEntry.COLUMN_LAST_MODIFIED, poi.getLastModified());
            Uri contractUri = mContentResolver.insert(TaiODataProvider.POIENTRY_URI, values);
            long rawContactId = ContentUris.parseId(contractUri);
            Log.i("Inserted ID: ", String.valueOf(rawContactId));
        }
        mCursor.close();
    }

    //Do I have to make a request for all information and sync it locally or does loopback provide a way for me to only query recently updated items
    //Do we need local and server side models?
    private void getAllPlaces() {

        Log.e(TAG, "adapter is connected: "+adapter.isConnected());
        Log.e(TAG, "adapter: "+adapter.toString());

        //POIRepository POIRepo = adapter.createRepository(POIRepository.class);
        POIRepository POIRepo = adapter.createRepository(POIRepository.class);
        Log.e(TAG, "adapter url: "+POIRepo.getNameForRestUrl().toString());

        try {

            Log.e(TAG, "trying to find all places");

            POIRepo.findAll(new ListCallback<POIModel>() {
                @Override
                public void onSuccess(List<POIModel> objects) {
                    parseResultModels(objects);
                    Log.e(TAG, "Successfully fetched data: " + objects.toString());
                }

                @Override
                public void onError(Throwable t) {
                    Log.e(TAG, "Failed to fetch data!");
                }
            });

            Log.e(TAG, "called find all");
        } catch (Exception e) {
            Log.d(TAG, "Find all exception: " + e.getLocalizedMessage());
        }
    }

    //TODO: BIG ERROR: DATABASE ROUNDS LAT LONG TO INTEGER
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
                parseJson(response);
            } else {
                Log.e(TAG, "Failed to fetch data!");
            }
        } catch (Exception e) {
            Log.d(TAG + "getdataerror", e.getLocalizedMessage());
        }
    }

    private void getUpdatedPois(String lastUpdate) {
        InputStream inputStream;
        HttpURLConnection urlConnection;
        Integer result = 0;
        //We need to do this because loopback formats dates differently than Java can
        lastUpdate = lastUpdate.substring(0,10) + "T" + lastUpdate.substring(10,18) + "." + lastUpdate.substring(19,lastUpdate.length());
        String serverUrl = this.serverUrl + getUpdateUrl + lastUpdate;
        try {
                /* forming th java.net.URL object */
            Log.e(TAG, "server update ulr is "+serverUrl);
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
                parseJson(response);
            } else {
                Log.e(TAG, "Failed to fetch data!");
            }
        } catch (Exception e) {
            Log.d(TAG + "updatepoi", e.toString());
        }
    }

    private void parseJson(String result) {

        Log.i(TAG + "parse json", "Result from server is " + result);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        parseResult(Arrays.asList(gson.fromJson(result, POIModel[].class)));

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

    private void parseResultModels(List<POIModel> models) {

        Log.i(TAG, "Result from server is " + models.toString());

        for (int i = 0; i < models.size(); ++i) {
            Log.i(TAG + " model", models.get(i).toString());
            insertPOI(models.get(i));
        }

    }

    private void getSelectedPlace(String point, String method) {

        String response = null;
        boolean deletePlace = false;
        //change this
        int result = -1;

        POIRepository POIRepo = adapter.createRepository(POIRepository.class);
        try {
            //check if should delete place
            //check if should delete place
            //if not delete, then get place

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        if(result == 1 && response != null && !deletePlace){

            //if you requested a place then parse the response and add it to the database

        }else{
            Log.e(TAG, "Failed to fetch data!");
        }
    }

    public static class POIRepository extends ModelRepository<POIModel> {
        public POIRepository() {
            super("POI", POIModel.class);
        }
    }

//    final Map<String,?> parameters = ImmutableMap.of(
//            "date", "2016-05-10T00:00:00.000Z");
//    Log.e("params", String.valueOf(parameters));
//    POIRepo.invokeStaticMethod("getUpdate", parameters, new Adapter.JsonArrayCallback() {
//        @Override
//        public void onSuccess(final JSONArray response) {
//            Log.e("Main Activity", "Successfullly fetched data!");
//            Log.i("Main activity", String.valueOf(response));
//        }
//
//        @Override
//        public void onError(final Throwable t) {
//            Log.e("Date request test","Cannot list locations.", t);
//        }
//    });
//    Log.e("ism", "called");

}

