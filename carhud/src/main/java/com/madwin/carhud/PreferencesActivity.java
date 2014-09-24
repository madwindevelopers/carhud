package com.madwin.carhud;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {

    String TAG = "PreferencesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment()).commit();

    }

    public static class PrefsFragment extends PreferenceFragment {

        EditTextPreference minimum_zoom_level_preference;
        EditTextPreference maximum_zoom_level_preference;
        EditText minimum_et;
        EditText maximum_et;
        SharedPreferences sp;
        String minimum_preference_key = "minimum_zoom_level";
        String maximum_preference_key = "maximum_zoom_level";


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());

            minimum_zoom_level_preference =
                    (EditTextPreference) findPreference(minimum_preference_key);
            maximum_zoom_level_preference =
                    (EditTextPreference) findPreference(maximum_preference_key);

            String minimum_summary = "Value = " + sp.getString(minimum_preference_key, "13");
            String maximum_summary = "Value = " + sp.getString(maximum_preference_key, "19");

            minimum_zoom_level_preference.setSummary(minimum_summary);
            maximum_zoom_level_preference.setSummary(maximum_summary);

            minimum_et = minimum_zoom_level_preference.getEditText();
            maximum_et = maximum_zoom_level_preference.getEditText();

            minimum_zoom_level_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (Double.parseDouble(o.toString()) < 10) {
                        sp.edit().putString(minimum_preference_key, "10").commit();
                        Toast.makeText(MainActivity.getAppContext(), "Minimum zoom must be between 10 and 19", Toast.LENGTH_SHORT).show();
                    } else if (Double.parseDouble(o.toString()) > 19) {
                        sp.edit().putString(minimum_preference_key, "19").commit();
                        Toast.makeText(MainActivity.getAppContext(), "Minimum zoom must be between 10 and 19", Toast.LENGTH_SHORT).show();
                    } else {
                        sp.edit().putString(minimum_preference_key, o.toString()).commit();
                    }
                    String minimum_summary = "Value = " + sp.getString(minimum_preference_key, "13");
                    minimum_zoom_level_preference.setSummary(minimum_summary);
                    return false;
                }
            });

            maximum_zoom_level_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (Double.parseDouble(o.toString()) < 11) {
                        sp.edit().putString(maximum_preference_key, "11").commit();
                        Toast.makeText(MainActivity.getAppContext(), "Maximum zoom must be between 11 and 20", Toast.LENGTH_SHORT).show();
                    } else if (Double.parseDouble(o.toString()) > 20) {
                        sp.edit().putString(maximum_preference_key, "20").commit();
                        Toast.makeText(MainActivity.getAppContext(), "Minimum zoom must be between 11 and 20", Toast.LENGTH_SHORT).show();
                    } else {
                            sp.edit().putString(maximum_preference_key, o.toString()).commit();
                    }
                    String maximum_summary = "Value = " + sp.getString(maximum_preference_key, "19");
                    maximum_zoom_level_preference.setSummary(maximum_summary);
                    return false;
                }
            });
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
