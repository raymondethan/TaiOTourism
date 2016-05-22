package hk.ust.cse.comp4521.taiotourism;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by nickjarzembowski on 22/05/16.
 */
public class PreferencesFragment extends PreferenceFragment {

    public static PreferencesFragment newInstance() {
        PreferencesFragment fragment = new PreferencesFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
