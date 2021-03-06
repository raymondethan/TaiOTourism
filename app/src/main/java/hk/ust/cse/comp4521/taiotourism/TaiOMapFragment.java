//# COMP 4521    #  AMANDA SAMBATH                            20363161          asambath@connect.ust.hk
//# COMP 4521    #  ETHAN RAYMOND                              20363147          eoraymond@connect.ust.hk
//# COMP 4521    #  NICKOLAS JARZEMBOWSKI               2033 8324          njj@connect.ust.hk
//# COMP 4521    #  LOIC BRUNO STEPAHNE TURIN        20363264          lbsturin@connect.ust.hk

package hk.ust.cse.comp4521.taiotourism;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.provider.BaseColumns._ID;

/**
 * Created by nickjarzembowski on 15/05/2016.
 */
public class TaiOMapFragment extends Fragment implements View.OnClickListener, GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener,
        LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<Status> {

    private View view;
    private RelativeLayout poi_peak;
    boolean poi_closed = true;
    boolean anim_stopped = true;


    private String mapFilterSetting = "";
    private double initialLat;
    private double initialLng;
    private String pictureUrlPrefix = "http://52.221.252.163:3000/api/containers/images/download/";

    private OnFragmentInteractionListener mListener;
    private static ToPOIFragListener toPOIFragListener;

    private String TAG = "Maps Fragment";

    private GoogleMap mMap;
    private boolean mapReady = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private static final int URL_LOADER = 0;

    final HashMap<String, String> textToCategory = new HashMap<String, String>();

    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private HashMap<Marker, String> markerCategories = new HashMap();
    private HashMap<Marker, ArrayList<String>> markerInfo = new HashMap();
    int pictureUrlIndex = 0;
    int openingHoursIndex = 1;
    int ratingIndex = 2;

    //For the category selection dialog
    boolean[] checkedItems;
    private HashSet<String> hideItems = new HashSet();
    private HashSet<String> showItems = new HashSet();

    private AlertDialog selectCategoryDialog;

    protected Location mLastLocation, mCurrentLocation;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    //We will want to change these values or get rid of the variables for location updates
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 120000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates = false;

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    //private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    SupportMapFragment mapFragment;

    //assume its true and only set it to false if we realize we don't have permission
    //private Boolean location_permission_granted = true;

    public TaiOMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapFragment.
     */
    public static TaiOMapFragment newInstance(String mapFilterSetting) {
        TaiOMapFragment fragment = new TaiOMapFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_MAP_FILTER_SETTING, mapFilterSetting);
        args.putDouble(Constants.ARG_POI_LATITUDE, 22.253155);
        args.putDouble(Constants.ARG_POI_LONGITUDE,113.858185);
        fragment.setArguments(args);
        return fragment;
    }

    public static TaiOMapFragment newInstance(double latitude, double longitude) {
        TaiOMapFragment fragment = new TaiOMapFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_MAP_FILTER_SETTING, "");
        args.putDouble(Constants.ARG_POI_LATITUDE, latitude);
        args.putDouble(Constants.ARG_POI_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("On create","called");
        super.onCreate(savedInstanceState);
        Log.i("On create",String.valueOf(getArguments()));

        if (getArguments() != null) {
            mapFilterSetting = getArguments().getString(Constants.ARG_MAP_FILTER_SETTING);
            checkedItems = new boolean[]{true, false, false};
            initialLat = getArguments().getDouble(Constants.ARG_POI_LATITUDE);
            initialLng = getArguments().getDouble(Constants.ARG_POI_LONGITUDE);
        }
        else {
            checkedItems = new boolean[]{true, true, true};
            initialLat = Constants.INITIAL_LAT;
            initialLng = Constants.INITIAL_LNG;
        }

        buildGoogleApiClient();
        mRequestingLocationUpdates = false;
        mLastLocation = new Location(String.valueOf(new LatLng(Constants.INITIAL_LAT, Constants.INITIAL_LNG)));

        selectCategoryDialog = createCategoryDialog();

        getLoaderManager().initLoader(0, null, this);

        //Is there a more scalable way we can do this
        textToCategory.put(Constants.CATEGORY_TOUR_STOP_TEXT, Constants.CATEGORY_TOUR_STOP);
        textToCategory.put(Constants.CATEGORY_RESTAURANT_TEXT, Constants.CATEGORY_RESTAURANT);
        textToCategory.put(Constants.CATEGORY_FACILITY_TEXT, Constants.CATEGORY_FACILITY);

    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }

        if (poi_peak == null) {
            poi_peak = (RelativeLayout) view.findViewById(R.id.poi_peak_main);
        }
        try {
            poi_peak = (RelativeLayout) view.findViewById(R.id.poi_peak_main);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }

        Bundle args = this.getArguments();

        // Set toolbar title
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle(R.string.toolbar_map);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        //add the button to select different categories
        setHasOptionsMenu(true);


        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.gmFragment);
            mapFragment.getMapAsync(this);
        }
        if (null != mLastLocation) {
            Log.i(TAG + " onCreateView", mLastLocation.toString());
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "frag: called onStart");
        client.connect();
    }



    @Override
    public void onStop() {
        super.onStop();
        if (client.isConnected()) {
            client.disconnect();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(TAG + "menu item clicked", String.valueOf(item.getItemId()));
        switch (item.getItemId()) {
            case R.id.action_settings:
                selectCategoryDialog.show();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {}

    // Interface for Main Activity
    public interface ToPOIFragListener {
        void onPopupClickListener(String name, String description, String pictureUrl, String openingHours, double rating, double latitude, double longitude);
    }

    /**
     * Handles the POI popup animation
     * @param v
     */
    private void OnPOITapHandler(View v, final Marker marker) {
        if (!anim_stopped) return;

        anim_stopped = false;
        View m = v.findViewById(R.id.gmFragment);

        poi_peak.setVisibility(View.VISIBLE);

        poi_peak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> info = markerInfo.get(marker);
                Log.i("popup info", String.valueOf(info));
                String url = pictureUrlPrefix + info.get(pictureUrlIndex);
                if (null != info) {
                    mListener.OnMapFragmentPOIPopupInteraction(marker.getTitle(), marker.getSnippet(), url,
                            info.get(openingHoursIndex), Double.parseDouble(info.get(ratingIndex)),
                            marker.getPosition().latitude, marker.getPosition().longitude);
                }
            }
        });

        if (poi_closed) {
            //Only change the layout information if a popup is not already open
            TextView title = (TextView) poi_peak.findViewById(R.id.Title);
            TextView title_two = (TextView) poi_peak.findViewById(R.id.Title_Two);
            TextView description = (TextView) poi_peak.findViewById(R.id.Description);

            //Change this to the marker image url
            ImageView image = (ImageView) poi_peak.findViewById(R.id.POI_image);
            Picasso.with(getContext())
                    .load(pictureUrlPrefix + markerInfo.get(marker).get(pictureUrlIndex))
                    .fit().centerCrop()
                    .into(image);


//            image.setBackgroundResource(R.drawable.nav_drawer_head);
            System.out.println("oh: " + markerInfo.get(marker));
            title.setText(marker.getTitle());
            title_two.setText(markerInfo.get(marker).get(openingHoursIndex));
            description.setText(marker.getSnippet());

            animatePopup(ObjectAnimator.ofFloat(poi_peak, View.TRANSLATION_Y, m.getHeight(), m.getHeight() - poi_peak.getHeight()));
        } else {
            animatePopup(ObjectAnimator.ofFloat(poi_peak, View.TRANSLATION_Y, m.getHeight() - poi_peak.getHeight(), m.getHeight()));
        }
    }

    private void animatePopup(ObjectAnimator anim) {
        anim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (poi_closed) {
                    poi_closed = false;
                } else {
                    poi_closed = true;
                }
                anim_stopped = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        anim.setDuration(400);
        anim.start();
    }


    public AlertDialog createCategoryDialog() {
        final String[] categories = {Constants.CATEGORY_TOUR_STOP_TEXT, Constants.CATEGORY_RESTAURANT_TEXT, Constants.CATEGORY_FACILITY_TEXT};
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(getString(R.string.map_select_filters));

        builder.setMultiChoiceItems(categories, checkedItems,
                new OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            hideItems.remove(textToCategory.get(categories[indexSelected]));
                            showItems.add(textToCategory.get(categories[indexSelected]));
                        } else {
                            showItems.remove(textToCategory.get(categories[indexSelected]));
                            hideItems.add(textToCategory.get(categories[indexSelected]));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Code when user clicked on OK
                        //  Tell the controller to edit the map
                        filterMarkers(hideItems, false);
                        filterMarkers(showItems, true);

                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                });

        return builder.create();
    }

    private void filterMarkers(Set<String> categories, Boolean setVisible) {
        Log.i(TAG, "filter markers: " + categories.toString() + "\n" + setVisible.toString());

        for (Marker marker : markers) {
            if (categories.contains(markerCategories.get(marker))) {
                marker.setVisible(setVisible);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMarkers(Cursor markerCursor) {

        double latitude, longitude;
        String name;
        String category;
        String description;
        double rating;
        String opening_hours;
        String pictureUrl;

        while (!mapReady) {
            Log.d(TAG, "map not ready");
        }
        //Move to the first row in the cursor
        markerCursor.moveToFirst();

        if (!markerCursor.isAfterLast()) {

            do { // for all the rows in the cursor


                // Get the poiName's name, latitude and longitude
                name = markerCursor.getString(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_NAME));
                latitude = markerCursor.getDouble(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_LATITUDE));
                longitude = markerCursor.getDouble(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_LONGITUDE));
                category = markerCursor.getString(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_CATEGORY));
                description = markerCursor.getString(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_DESCRIPTION));
                rating = markerCursor.getDouble(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_RATING));
                opening_hours = markerCursor.getString(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_OPENING_HOURS));
                pictureUrl = markerCursor.getString(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_PICTURE_URL));

                Log.i(TAG, "Marker at: " + name + " " + latitude + " " + longitude);

                Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name).snippet(description));
                if (Constants.CATEGORY_TOUR_STOP.equals(mapFilterSetting) && !category.equals(Constants.CATEGORY_TOUR_STOP)) {
                    Log.i(TAG,mapFilterSetting.toString());
                    Log.i(TAG,category.toString());
                    Log.i(TAG,Constants.CATEGORY_TOUR_STOP);
                    m.setVisible(false);
                }
                markers.add(m);
                markerCategories.put(m, category);
                ArrayList<String> info = new ArrayList();
                info.add(pictureUrl);
                info.add(opening_hours);
                info.add(String.valueOf(rating));
                markerInfo.put(m,info);

            }
            while (markerCursor.moveToNext());  // until you exhaust all the rows. returns false when we reach the end of the cursor
        }
//        markerCursor.close();

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        OnPOITapHandler(view, marker);
        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
           /*
     * Takes action based on the ID of the Loader that's being created
     */
        String[] projection = {TaiODataContract.POIEntry.COLUMN_NAME, TaiODataContract.POIEntry.COLUMN_LATITUDE, TaiODataContract.POIEntry.COLUMN_LONGITUDE,
                TaiODataContract.POIEntry.COLUMN_CATEGORY, TaiODataContract.POIEntry.COLUMN_DESCRIPTION,
                TaiODataContract.POIEntry.COLUMN_RATING, TaiODataContract.POIEntry.COLUMN_OPENING_HOURS, TaiODataContract.POIEntry.COLUMN_PICTURE_URL};
        switch (id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this.getContext(),   // Parent activity context
                        TaiODataProvider.POIENTRY_URI,      // Table to query
                        projection,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //adapter.swapCursor(data);

        setUpMarkers(data);
        Log.d("******LOADER MANAGER: ", "called initLoader");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //adapter.swapCursor(null);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.i(TAG, "onMapClick: called");
        if (!poi_closed) {
            View m = view.findViewById(R.id.gmFragment);
            animatePopup(ObjectAnimator.ofFloat(poi_peak, View.TRANSLATION_Y, m.getHeight() - poi_peak.getHeight(), m.getHeight()));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected: called");
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

//            ActivityCompat.requestPermissions(this.getActivity(),
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


            Log.i("on connect", "need permission");
            Log.i("permission", String.valueOf(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)));
            Log.i("permission", String.valueOf(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)));
            Log.i("permission granted", String.valueOf(PackageManager.PERMISSION_GRANTED));
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);

        if (null == mLastLocation) {
            Toast.makeText(this.getContext(), R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }

        // Determine whether a Geocoder is available.
        if (!Geocoder.isPresent()) {
            Toast.makeText(this.getContext(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }

        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG, "startLocationUpdates: still don't have permissions");
            return;
        }
        Log.i(TAG, "startLocationUpdates: called");
        LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG,"onLocationChanged: called");
        mCurrentLocation = location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this.getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                client.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getActivity().getFragmentManager(), "errordialog");
    }


    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            onDialogDismissed();
        }
    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    // TODO: setup interface for main activity to change content pane according to events here.
    public interface OnFragmentInteractionListener {
        // Implemented in main activity.
        void OnMapFragmentPOIPopupInteraction(String name, String description, String pictureUrl, String openingHours, double rating, double latitude, double longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapReady = true;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowClickListener(this);
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

//            ActivityCompat.requestPermissions(this.getActivity(),
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);
        final Context context = this.getContext();
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (null != mLastLocation && null != (Double) mLastLocation.getLatitude() && null != (Double) mLastLocation.getLongitude()) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Constants.ZOOM_LEVEL));
                } else {
                    Toast.makeText(context, R.string.no_location_detected, Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initialLat, initialLng), Constants.ZOOM_LEVEL));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    client.connect();

                } else {

                    Log.i(TAG, "onRequestPermission: Cannot show user location, won't try to connect");
                    //location_permission_granted = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapFragment.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (client.isConnected()) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
        ((MainActivity)getActivity()).nvDrawer.getMenu().getItem(((MainActivity)getActivity()).lastSelected).setChecked(false);
        ((MainActivity)getActivity()).nvDrawer.getMenu().getItem(1).setChecked(true);
        mapFragment.onResume();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (client.isConnected()) {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
        ((MainActivity)getActivity()).nvDrawer.getMenu().getItem(1).setChecked(false);
        mapFragment.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mapFragment != null) {
//            mapFragment.onDestroy();
//        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }






}
