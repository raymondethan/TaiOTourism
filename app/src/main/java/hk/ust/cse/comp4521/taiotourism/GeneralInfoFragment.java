package hk.ust.cse.comp4521.taiotourism;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hk.ust.cse.comp4521.taiotourism.Constants;
import hk.ust.cse.comp4521.taiotourism.MainActivity;
import hk.ust.cse.comp4521.taiotourism.R;
import hk.ust.cse.comp4521.taiotourism.TaiODataContract;
import hk.ust.cse.comp4521.taiotourism.TaiODataProvider;

/**
 * Created by amanda on 23/05/16.
 */
public class GeneralInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "General Info Fragment";
    private static int ID_LOADER = 0;

    private static SharedPreferences sharedPreferences;
    private static String lang;

    private TextView vTaiODetails;
    private TextView vYWCADetails;
    private String taiODetails;
    private String ywcaDetails;

    // ************************************************************************
    //                       FRAGMENT LIFECYCLE METHODS
    // ************************************************************************
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate views
        View rootView = inflater.inflate(R.layout.fragment_general_info, container, false);
        vTaiODetails = (TextView) rootView.findViewById(R.id.tai_o_detail);
        vYWCADetails = (TextView) rootView.findViewById(R.id.ywca_detail);
        Log.d(TAG, "End inflating views");

        // Get language setting and fetch details data
        sharedPreferences = getActivity().getSharedPreferences(
                Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        lang = sharedPreferences.getString(Constants.SHARED_PREFERENCES_LANG,
                Constants.SHARED_PREFERENCES_EN);
        Log.d(TAG, "End getting shared pref");
        getLoaderManager().initLoader(ID_LOADER, null, this);

        // Set toolbar title
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle(R.string.toolbar_general_info);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    // ************************************************************************
    //                         LOADER MANAGER METHODS
    // ************************************************************************
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection;
        // Takes action based on the ID of the Loader that's being created
        switch (lang) {
            case Constants.SHARED_PREFERENCES_CH:
                projection = new String[]{TaiODataContract.GeneralInfo.COLUMN_TAIO_INFO_CH,
                        TaiODataContract.GeneralInfo.COLUMN_YWCA_INFO_CH};
                break;
            default:
                projection = new String[]{TaiODataContract.GeneralInfo.COLUMN_TAIO_INFO,
                        TaiODataContract.GeneralInfo.COLUMN_YWCA_INFO};
                break;
        }

        if (id == ID_LOADER) {
            return new CursorLoader(
                    getActivity(),                  // Parent activity context
                    TaiODataProvider.GENERALINFO_URI,  // Table to query
                    projection,                     // Projection to return
                    null,                      // No selection clause
                    null,                  // No selection arguments
                    null                            // Default sort order
            );
        }
        // If wrong loader id.
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setDetailsData(data);
        vTaiODetails.setText(taiODetails);
        vYWCADetails.setText(ywcaDetails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        vTaiODetails.setText(taiODetails);
        vYWCADetails.setText(ywcaDetails);
    }

    private void setDetailsData(Cursor itemCursor) {
        if (itemCursor.isClosed()) return;
        // Initialise the cursor to the first row
        itemCursor.moveToFirst();

        if (!itemCursor.isAfterLast()) {
            // set the strings
            switch (lang) {
                case Constants.SHARED_PREFERENCES_CH:
                    taiODetails = itemCursor.getString(itemCursor.getColumnIndexOrThrow(
                            TaiODataContract.GeneralInfo.COLUMN_TAIO_INFO_CH));
                    ywcaDetails = itemCursor.getString(itemCursor.getColumnIndexOrThrow(
                            TaiODataContract.GeneralInfo.COLUMN_YWCA_INFO_CH));
                    break;
                default:
                    taiODetails = itemCursor.getString(itemCursor.getColumnIndexOrThrow(
                            TaiODataContract.GeneralInfo.COLUMN_TAIO_INFO));
                    ywcaDetails = itemCursor.getString(itemCursor.getColumnIndexOrThrow(
                            TaiODataContract.GeneralInfo.COLUMN_YWCA_INFO));
                    break;
            }
        } else {
            taiODetails = "Unable to fetch information. Please connect to wifi.";
            ywcaDetails = "Unable to fetch information. Please connect to wifi.";
        }

        itemCursor.close();
    }
}
