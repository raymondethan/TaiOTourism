package hk.ust.cse.comp4521.taiotourism;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by amanda on 17/05/16.
 */
public class POIFragment extends Fragment {

    private TextView vName;
    private TextView vDescription;
    private ImageView vPicture;
    private TextView vOpeningHours;
    private TextView vRating;

    // Fragment arguments
    private String poiName;
    private String poiDescription;
    private String poiPictureUrl;
    private String poiOpeningHours;
    private Double poiRating;
    private static final String POI_NAME = "name";
    private static final String POI_DESCRIPTION = "description";
    private static final String POI_PICTURE_URL = "pictureUrl";
    private static final String POI_OPENING_HOURS  = "openingHours";
    private static final String POI_RATING = "rating";

    // ************************************************************************
    //                         CONSTRUCTOR AND FACTORY
    // ************************************************************************
    public POIFragment() {}

    public static POIFragment newInstance(String poiName, String poiDescription,
                                          String poiPictureUrl, String poiOpeningHours,
                                          Double poiRating) {
        POIFragment poiFragment = new POIFragment();
        Bundle args = new Bundle();
        args.putString(POI_NAME, poiName);
        args.putString(POI_DESCRIPTION, poiDescription);
        args.putString(POI_PICTURE_URL, poiPictureUrl);
        args.putString(POI_OPENING_HOURS, poiOpeningHours);
        args.putDouble(POI_RATING, poiRating);

        poiFragment.setArguments(args);
        return poiFragment;
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

        // Set data into views
        vName.setText(poiName);
        vDescription.setText(poiDescription);
        vOpeningHours.setText(poiOpeningHours);

        Picasso.with(getContext())
                .load(poiPictureUrl)
                .resize(screenWidth, 0)
                .into(vPicture);

        if (poiRating != null) {
            vRating.setText(poiRating.toString());
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        poiName = null;
        poiDescription = null;
        poiPictureUrl = null;
        poiOpeningHours = null;
        poiRating = null;
    }
}
