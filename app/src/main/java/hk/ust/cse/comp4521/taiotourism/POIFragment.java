//# COMP 4521    #  AMANDA SAMBATH                            20363161          asambath@connect.ust.hk
//# COMP 4521    #  ETHAN RAYMOND                              20363147          eoraymond@connect.ust.hk
//# COMP 4521    #  NICKOLAS JARZEMBOWSKI               2033 8324          njj@connect.ust.hk
//# COMP 4521    #  LOIC BRUNO STEPAHNE TURIN        20363264          lbsturin@connect.ust.hk

package hk.ust.cse.comp4521.taiotourism;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by amanda on 17/05/16.
 */
public class POIFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "POI Fragment";

    // Views
    private TextView vName;
    private TextView vDescription;
    private ImageView vPicture;
    private TextView vOpeningHours;
    private TextView vRating;

    // Listener to access the poi location on the map
    private static ToMapListener toMapListener;

    // Fragment arguments
    private String poiName;
    private String poiDescription;
    private String poiPictureUrl;
    private String poiOpeningHours;
    private Double poiRating;
    private double poiLatitude;
    private double poiLongitude;
    private static final String POI_NAME = "name";
    private static final String POI_DESCRIPTION = "description";
    private static final String POI_PICTURE_URL = "pictureUrl";
    private static final String POI_OPENING_HOURS  = "openingHours";
    private static final String POI_RATING = "rating";
    private static final String POI_LATITUDE = "latitude";
    private static final String POI_LONGITUDE = "longitude";

    // ************************************************************************
    //                         CONSTRUCTORS AND SETTER
    // ************************************************************************
    public POIFragment() {}

    public static POIFragment newInstance(String poiName, String poiDescription,
                                          String poiPictureUrl, String poiOpeningHours,
                                          double poiRating, double poiLatitude,
                                          double poiLongitude) {
        POIFragment poiFragment = new POIFragment();
        Bundle args = new Bundle();
        args.putString(POI_NAME, poiName);
        args.putString(POI_DESCRIPTION, poiDescription);
        args.putString(POI_PICTURE_URL, poiPictureUrl);
        args.putString(POI_OPENING_HOURS, poiOpeningHours);
        args.putDouble(POI_RATING, poiRating);
        args.putDouble(POI_LATITUDE, poiLatitude);
        args.putDouble(POI_LONGITUDE, poiLongitude);

        poiFragment.setArguments(args);
        return poiFragment;
    }

    public void setToMapListener(ToMapListener listener) {
        this.toMapListener = listener;
    }

    // ************************************************************************
    //                           LIFECYCLE CALLBACKS
    // ************************************************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve data from fragment's arguments
        if (getArguments() != null) {
            poiName = getArguments().getString(POI_NAME);
            poiDescription = getArguments().getString(POI_DESCRIPTION);
            poiPictureUrl = getArguments().getString(POI_PICTURE_URL);
            poiOpeningHours = getArguments().getString(POI_OPENING_HOURS);
            poiRating = getArguments().getDouble(POI_RATING);
            poiLatitude = getArguments().getDouble(POI_LATITUDE);
            poiLongitude = getArguments().getDouble(POI_LONGITUDE);
        }

        Log.i("POI frag lat", String.valueOf(poiLatitude));
        Log.i("POI frag lng", String.valueOf(poiLongitude));

        // Set toolbar title
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(poiName);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the screen width for picasso to display image correctly
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;

        // Inflate views
        View rootView = inflater.inflate(R.layout.fragment_poi, container, false);
        vName = (TextView) rootView.findViewById(R.id.poiName) ;
        vDescription = (TextView) rootView.findViewById(R.id.poiDescription);
        vPicture = (ImageView) rootView.findViewById(R.id.poiPicture);
        vOpeningHours = (TextView) rootView.findViewById(R.id.poiOpeningHours);
        vRating = (TextView) rootView.findViewById(R.id.poiRating);

        // Set button click listener
        rootView.findViewById(R.id.poi_to_map_button).setOnClickListener(this);

        // Set data into views
        vName.setText(poiName);
        vDescription.setText(poiDescription);
        vOpeningHours.setText(getString(R.string.opening_hours, poiOpeningHours));

        Picasso.with(getContext())
                .load(poiPictureUrl)
                .resize(screenWidth, 0)
                .into(vPicture);

        if (poiRating != null && poiRating >= 0) {
            vRating.setText(getString(R.string.rating, poiRating));
        }

        return rootView;
    }

    @Override
    public void onResume () {
        super.onResume();
        ((MainActivity) getActivity()).drawerToggle.setDrawerIndicatorEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPause () {
        super.onPause();

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).drawerToggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        poiName = null;
        poiDescription = null;
        poiPictureUrl = null;
        poiOpeningHours = null;
        poiRating = null;

//        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        ((MainActivity) getActivity()).drawerToggle.setDrawerIndicatorEnabled(true);

    }

    // ************************************************************************
    //                           INTERACTION METHODS
    // ************************************************************************
    @Override
    public void onClick(View view) {
        toMapListener.onToMapClickListener(poiLatitude, poiLongitude);
    }

    // Interface for Main Activity
    public interface ToMapListener{
        void onToMapClickListener(double lat, double lng);
    }
}
