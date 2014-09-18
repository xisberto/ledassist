package net.xisberto.ledassist;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by 00790186373 on 18/09/14.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
