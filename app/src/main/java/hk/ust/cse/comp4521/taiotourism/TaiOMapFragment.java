package hk.ust.cse.comp4521.taiotourism;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
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


import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.provider.BaseColumns._ID;

/**
 * Created by nickjarzembowski on 15/05/2016.
 */
public class TaiOMapFragment extends Fragment implements View.OnClickListener, GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, LoaderManager.LoaderCallbacks<Cursor> {

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
    private HashMap<Marker,String> markerCategories = new HashMap();

    private ImageButton category_selector;
    private HashSet<String> hideItems = new HashSet();
    private HashSet<String> showItems = new HashSet();

    private AlertDialog selectCategoryDialog;

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

        selectCategoryDialog = createCategoryDialog();

        //TODO: Bind cursor adapter to popup
        getLoaderManager().initLoader(0, null, this);

        //Is there a more scalable way we can do this
        textToCategory.put(Constants.CATEGORY_TOUR_STOP_TEXT, Constants.CATEGORY_TOUR_STOP);
        textToCategory.put(Constants.CATEGORY_RESTAURANT_TEXT, Constants.CATEGORY_RESTAURANT);
        textToCategory.put(Constants.CATEGORY_FACILITY_TEXT, Constants.CATEGORY_FACILITY);

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

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
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


    public AlertDialog createCategoryDialog() {
        AlertDialog dialog;
        final String[] categories = {Constants.CATEGORY_TOUR_STOP_TEXT,Constants.CATEGORY_RESTAURANT_TEXT,Constants.CATEGORY_FACILITY_TEXT};

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Select What You Want to Filter");
        builder.setMultiChoiceItems(categories, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            // write your code when user checked the checkbox
                            Boolean removed = showItems.remove(textToCategory.get(categories[indexSelected]));
                            hideItems.add(textToCategory.get(categories[indexSelected]));
                        } else {
                            // Else, if the item is already in the array, remove it
                            // write your code when user Uchecked the checkbox
                            Boolean removed = hideItems.remove(textToCategory.get(categories[indexSelected]));
                            showItems.add(textToCategory.get(categories[indexSelected]));
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
    public void onMapLongClick(LatLng latLng) {

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
    }
}
