package hk.ust.cse.comp4521.taiotourism;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import static android.provider.BaseColumns._ID;

/**
 * Created by nickjarzembowski on 15/05/2016.
 */
public class TaiOMapFragment extends Fragment implements View.OnClickListener, GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    private View view;
    private RelativeLayout poi_peak;
    boolean poi_closed = true;
    boolean anim_stopped = true;
    private ObjectAnimator anim;

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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this.getContext()).addApi(AppIndex.API).build();

        //Is there a more scalable way we can do this
        textToCategory.put(Constants.CATEGORY_TOUR_STOP_TEXT, Constants.CATEGORY_TOUR_STOP);
        textToCategory.put(Constants.CATEGORY_RESTAURANT_TEXT, Constants.CATEGORY_RESTAURANT);
        textToCategory.put(Constants.CATEGORY_FACILITY_TEXT, Constants.CATEGORY_FACILITY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);

        // Get POI peak details container
        poi_peak = (RelativeLayout) view.findViewById(R.id.poi_peak_main);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.gmFragment);
        mapFragment.getMapAsync(this);

        return view;
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
    // TODO: Add click events to map pointers, and call OnPOITapHandler() to show POI popup
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.mapView:
//                OnPOITapHandler(v);
//                Log.i(TAG,"map view clicked");
//                break;
        }
    }

    /**
     * Handles the POI popup animation
     * @param v
     */
    private void OnPOITapHandler(View v) {
        if (!anim_stopped) return;

        anim_stopped = false;
        View m = v.findViewById(R.id.gmFragment);
        poi_peak.setVisibility(View.VISIBLE);

        if (poi_closed) {
            anim = ObjectAnimator.ofFloat(poi_peak, View.TRANSLATION_Y, m.getHeight(), m.getHeight() - poi_peak.getHeight());
        } else {
            anim = ObjectAnimator.ofFloat(poi_peak, View.TRANSLATION_Y, m.getHeight() - poi_peak.getHeight(), m.getHeight());
        }

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


    public void showCategoryDialog() {
        AlertDialog dialog;
        final String[] categories = {Constants.CATEGORY_TOUR_STOP_TEXT,Constants.CATEGORY_RESTAURANT_TEXT,Constants.CATEGORY_FACILITY_TEXT};
        // arraylist to keep the selected items
        final ArrayList<String> seletedItems = new ArrayList<String>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Select What You Want to Visit");
        builder.setMultiChoiceItems(categories, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            // write your code when user checked the checkbox
                            seletedItems.add(textToCategory.get(categories[indexSelected]));
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            // write your code when user Uchecked the checkbox
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Code when user clicked on OK
                        //  Tell the controller to edit the map
                        filterMarkers(seletedItems);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                });

        dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();
    }

    private void filterMarkers(ArrayList<String> categories) {
        for (Marker marker : markers) {
            marker.setVisible(false);
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

            }
            while (markerCursor.moveToNext());  // until you exhaust all the rows. returns false when we reach the end of the cursor
        }
        markerCursor.close();

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        OnPOITapHandler(view);
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

    // TODO: setup interface for main activity to change content pane according to events here.
    public interface OnFragmentInteractionListener {
        void OnMapFragmentInteraction();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapReady = true;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowClickListener(this);

//        Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(22.253155, 113.858185);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Tai O"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Log.i(TAG, "Marker added");


    }
}
