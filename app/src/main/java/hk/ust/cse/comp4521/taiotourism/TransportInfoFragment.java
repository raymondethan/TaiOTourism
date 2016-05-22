package hk.ust.cse.comp4521.taiotourism;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by amanda on 22/05/16.
 */
public class TransportInfoFragment extends Fragment {

    private static final String TAG = "Transportation Fragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate views
        View rootView = inflater.inflate(R.layout.fragment_transport_info, container, false);

        // Set toolbar title
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle(R.string.toolbar_transportation);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).nvDrawer.getMenu().getItem(((MainActivity)getActivity()).lastSelected).setChecked(false);
        ((MainActivity)getActivity()).nvDrawer.getMenu().findItem(R.id.nav_directions).setChecked(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).nvDrawer.getMenu().findItem(R.id.nav_directions).setChecked(false);
    }
}
