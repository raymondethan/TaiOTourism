package hk.ust.cse.comp4521.taiotourism;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by nickjarzembowski on 15/05/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Home Fragment";

    private View view;

    // used to define the filter setting of the map from the menu
    private static final String ARG_MAP_FILTER_SETTING = "param1";

    private String mapFilterSetting;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    // TODO: complete home layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_4, container, false);

        // Add click listeners to home screen buttons
        ((Button) view.findViewById(R.id.home_map_button)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.home_tour_button)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.home_transport_button)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.home_about_button)).setOnClickListener(this);

        // Set toolbar title
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

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
    public void onClick(View v) {
        mListener.OnHomeFragmentInteraction(v);
    }

    // TODO: setup interface for main activity to change content pane according to events here.
    public interface OnFragmentInteractionListener {
        void OnHomeFragmentInteraction(View view);
    }
}
