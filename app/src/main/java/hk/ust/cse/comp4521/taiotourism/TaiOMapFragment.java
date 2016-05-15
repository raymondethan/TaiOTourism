package hk.ust.cse.comp4521.taiotourism;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by nickjarzembowski on 15/05/2016.
 */
public class TaiOMapFragment extends Fragment implements View.OnClickListener {

    private View view;
    private RelativeLayout poi_peak;
    boolean poi_closed = true;
    boolean anim_stopped = true;
    private ObjectAnimator anim;

    // used to define the filter setting of the map from the menu
    private static final String ARG_MAP_FILTER_SETTING = "param1";

    private String mapFilterSetting;

    private OnFragmentInteractionListener mListener;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);

        // Get POI peak details container
        poi_peak = (RelativeLayout) view.findViewById(R.id.poi_peak_main);

//        // Used to test POI Peaker
//        TextView map = (TextView) view.findViewById(R.id.map);
//        map.setOnClickListener(this);

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
//            case R.id.map:
//                OnPOITapHandler(v);
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
        TextView m = (TextView) v.findViewById(R.id.gmFragment);
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

    // TODO: setup interface for main activity to change content pane according to events here.
    public interface OnFragmentInteractionListener {
        void OnMapFragmentInteraction();
    }
}
