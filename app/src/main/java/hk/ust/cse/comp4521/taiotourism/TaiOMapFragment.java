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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    // used to define the filter setting of the map from the menu
    private static final String ARG_MAP_FILTER_SETTING = "param1";

    private String mapFilterSetting;

    private OnFragmentInteractionListener mListener;

    private String TAG = "Maps Fragment";

    private GoogleMap mMap;
    private boolean mapReady = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private static final int URL_LOADER = 0;

    final HashMap<String,String> textToCategory = new HashMap<String, String>();

    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private HashMap<Marker,String> markerCategories = new HashMap();

    private ImageButton category_selector;
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
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    protected String mLocationOutput;

    private Marker meMarker;

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    //private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

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
        args.putString(ARG_MAP_FILTER_SETTING, mapFilterSetting);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mapFilterSetting = getArguments().getString(ARG_MAP_FILTER_SETTING);
        }

        buildGoogleApiClient();
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        selectCategoryDialog = createCategoryDialog();

        //TODO: Bind cursor adapter to popup
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

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);

        //add the button to select different categories
        setHasOptionsMenu(true);

        // Get POI peak details container
        poi_peak = (RelativeLayout) view.findViewById(R.id.poi_peak_main);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.gmFragment);
        mapFragment.getMapAsync(this);
        //mapFragment.set

        Log.i("oncreateview",mLastLocation.toString());
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
        Log.i("frag","called onstart");
        client.connect();
    }

    @Override
    public void onResume() {

        super.onResume();
        if (client.isConnected()) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        if (client.isConnected()) {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (client.isConnected()) {
            client.disconnect();
        }
        super.onStop();
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
        Log.i("menu item clicked", String.valueOf(item.getItemId()));
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
    public void onClick(View v) {
    }

    /**
     * Handles the POI popup animation
     * @param v
     */
    private void OnPOITapHandler(View v, Marker marker) {
        if (!anim_stopped) return;

        anim_stopped = false;
        View m = v.findViewById(R.id.gmFragment);

        poi_peak.setVisibility(View.VISIBLE);

//        poi_peak.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), POIActivity.class);
//                startActivity(intent);
//            }
//        });

        if (poi_closed) {
            //Only change the layout information if a popup is not already open
            TextView title = (TextView) poi_peak.findViewById(R.id.Title);
            TextView description = (TextView) poi_peak.findViewById(R.id.Description);

            //Change this to the marker image url
            ImageView image = (ImageView) poi_peak.findViewById(R.id.POI_image);
            image.setBackgroundResource(R.drawable.nav_drawer_head);

            title.setText(marker.getTitle());
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
        final String[] categories = {Constants.CATEGORY_TOUR_STOP_TEXT,Constants.CATEGORY_RESTAURANT_TEXT,Constants.CATEGORY_FACILITY_TEXT};
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Select What You Want to Filter");
        final boolean[] checkedItems = {true,true,true};
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
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Code when user clicked on OK
                        //  Tell the controller to edit the map
                        filterMarkers(hideItems,false);
                        filterMarkers(showItems,true);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                });

        return builder.create();
    }

    private void filterMarkers(Set<String> categories, Boolean setVisible) {
        Log.i("filter markers",categories.toString() + "\n" + setVisible.toString());
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

        int column_index;
        double latitude, longitude;
        String name;
        long id;
        String category;
        int tour_order;
        String description;
        double rating;
        String opening_hours;
        int count;

        while (!mapReady) {
            Log.d("Map-","map not ready");
        }
        //Move to the first row in the cursor
        markerCursor.moveToFirst();

        if (!markerCursor.isAfterLast()) {

            do { // for all the rows in the cursor


                // Get the poiName's name, latitude and longitude
                id = markerCursor.getLong(markerCursor.getColumnIndexOrThrow(_ID));
                name = markerCursor.getString(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_NAME));
                latitude = markerCursor.getDouble(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_LATITUDE));
                longitude = markerCursor.getDouble(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_LONGITUDE));
                category = markerCursor.getString(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_CATEGORY));
                tour_order = markerCursor.getInt(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_TOUR_ORDER));
                description = markerCursor.getString(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_DESCRIPTION));
                rating = markerCursor.getDouble(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_RATING));
                opening_hours = markerCursor.getString(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_OPENING_HOURS));
                count = markerCursor.getInt(markerCursor.getColumnIndexOrThrow(TaiODataContract.POIEntry.COLUMN_VISIT_COUNTER));

                Log.i(TAG, "Marker at: " + id + " " + name + " " + latitude + " " + longitude);

                //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name).snippet(Long.toString(id)));
                Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name).snippet(description));
                markers.add(m);
                markerCategories.put(m,category);

            }
            while (markerCursor.moveToNext());  // until you exhaust all the rows. returns false when we reach the end of the cursor
        }
        markerCursor.close();

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
        String[] projection = {TaiODataContract.POIEntry._ID, TaiODataContract.POIEntry.COLUMN_NAME, TaiODataContract.POIEntry.COLUMN_LATITUDE, TaiODataContract.POIEntry.COLUMN_LONGITUDE,
                TaiODataContract.POIEntry.COLUMN_CATEGORY, TaiODataContract.POIEntry.COLUMN_TOUR_ORDER, TaiODataContract.POIEntry.COLUMN_DESCRIPTION,
                TaiODataContract.POIEntry.COLUMN_RATING, TaiODataContract.POIEntry.COLUMN_OPENING_HOURS, TaiODataContract.POIEntry.COLUMN_VISIT_COUNTER};
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.253155, 113.858185),Constants.ZOOM_LEVEL));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //adapter.swapCursor(null);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.i("On map click"," called");
        if (!poi_closed) {
            View m = view.findViewById(R.id.gmFragment);
            animatePopup(ObjectAnimator.ofFloat(poi_peak, View.TRANSLATION_Y, m.getHeight() - poi_peak.getHeight(), m.getHeight()));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("onconnect", "called");
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


            Log.i("on connect","need permission");
            Log.i("permission", String.valueOf(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)));
            Log.i("permission", String.valueOf(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)));
            Log.i("permission granted", String.valueOf(PackageManager.PERMISSION_GRANTED));
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);

        if (mLastLocation != null) {
            String message = "Last Location is: " +
                    "  Latitude = " + String.valueOf(mLastLocation.getLatitude()) +
                    "  Longitude = " + String.valueOf(mLastLocation.getLongitude());

            mLocationOutput = message;
            Log.i(TAG, message);
            Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.getContext(), R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }

        // Determine whether a Geocoder is available.
        if (!Geocoder.isPresent()) {
            Toast.makeText(this.getContext(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }
        // It is possible that the user presses the button to get the address before the
        // GoogleApiClient object successfully connects. In such a case, mAddressRequested
        // is set to true, but no attempt is made to fetch the address (see
        // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
        // user has requested an address, since we now have a connection to GoogleApiClient.
//        if (mAddressRequested) {
//            startIntentService(mLastLocation);
//        }

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
            Log.i("startlocationupdates","still don't have permissions");
            return;
        }
        Log.i("startlocationupdates","called");
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
        Log.i("onlocationchanged","called");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        String message = "Current Location is: " +
                "  Latitude = " + String.valueOf(mCurrentLocation.getLatitude()) +
                "  Longitude = " + String.valueOf(mCurrentLocation.getLongitude() +
                "\nLast Updated = " + mLastUpdateTime);

        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;

        if (meMarker != null)
            meMarker.remove();

        MarkerOptions markCur = new MarkerOptions()
                .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                .title("Me");

        meMarker = mMap.addMarker(markCur);
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
    public static class ErrorDialogFragment extends DialogFragment {
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
            ((MapActivity) getActivity()).onDialogDismissed();
        }
    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    // TODO: setup interface for main activity to change content pane according to events here.
    public interface OnFragmentInteractionListener {
        void OnMapFragmentInteraction();
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

                    Log.i("onrequestpermission","Cannot show user location, won't try to connect");
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
}
