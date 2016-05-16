package hk.ust.cse.comp4521.taiotourism;

import android.content.pm.InstrumentationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by amanda on 15/05/16.
 */
public class POIActivity extends AppCompatActivity {

    private String TAG = "POI Detailed View Activity";

    private Toolbar toolbar;
    private TextView vName;
    private TextView vDescription;
    private ImageView vPicture;
    private TextView vOpeningHours;
    private TextView vRating;

    private String poiName;
    private String poiDescription;
    private String poiPictureUrl;
    private String poiOpeningHours;
    private Double poiRating;
    private static String POI_NAME = "name";
    private static String POI_DESCRIPTION = "description";
    private static String POI_PICTURE_URL = "pictureUrl";
    private static String POI_OPENING_HOURS  = "openingHours";
    private static String POI_RATING = "rating";

    // ************************************************************************
    //                     ACTIVITY FLOW OVERRIDE METHODS
    // ************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout.
        setContentView(R.layout.activity_poi);
        vName = (TextView) findViewById(R.id.poiName) ;
        vDescription = (TextView) findViewById(R.id.poiDescription);
        vPicture = (ImageView) findViewById(R.id.poiPicture);
        vOpeningHours = (TextView) findViewById(R.id.poiOpeningHours);
        vRating = (TextView) findViewById(R.id.poiRating);

        // Retrieve intent extra
        Bundle extras = getIntent().getExtras();
        poiName = extras.getString(POI_NAME);
        poiDescription = extras.getString(POI_DESCRIPTION);
        poiPictureUrl = extras.getString(POI_PICTURE_URL);
        poiOpeningHours = extras.getString(POI_OPENING_HOURS);
        poiRating = extras.getDouble(POI_RATING);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Set data into views
        vName.setText(poiName);
        vDescription.setText(poiDescription);
        vOpeningHours.setText(poiOpeningHours);
        if (poiRating != null) {
            vRating.setText(poiRating.toString());
        }
        Picasso.with(this)
                .load(poiPictureUrl)
                .into(vPicture);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
