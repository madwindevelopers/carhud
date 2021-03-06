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

    public static final String CURRENT_ADDRESS_UPDATE_INTERVAL_KEY = "address_update_interval";
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
        EditTextPreference map_animation_speed_preference;
        EditTextPreference currentAddressUpdateIntervalPreference;
        EditText minimum_et;
        EditText maximum_et;
        SharedPreferences sp;
        String minimum_preference_key = "minimum_zoom_level";
        String maximum_preference_key = "maximum_zoom_level";
        String map_animation_speed_key = "map_animation_speed";


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());

            minimum_zoom_level_preference =
                    (EditTextPreference) findPreference(minimum_preference_key);
            maximum_zoom_level_preference =
                    (EditTextPreference) findPreference(maximum_preference_key);
            map_animation_speed_preference =
                    (EditTextPreference) findPreference(map_animation_speed_key);
            currentAddressUpdateIntervalPreference =
                    (EditTextPreference) findPreference(CURRENT_ADDRESS_UPDATE_INTERVAL_KEY);



            String minimum_summary = "Value = " + sp.getString(minimum_preference_key, "14");
            String maximum_summary = "Value = " + sp.getString(maximum_preference_key, "16");
            String map_animation_summary = "Value = "
                    + sp.getString(map_animation_speed_key, "900");
            final String currentUpdateIntervalSummary = "Value = "
                    + sp.getString(CURRENT_ADDRESS_UPDATE_INTERVAL_KEY, "5");

            minimum_zoom_level_preference.setSummary(minimum_summary);
            maximum_zoom_level_preference.setSummary(maximum_summary);
            map_animation_speed_preference.setSummary(map_animation_summary);
            currentAddressUpdateIntervalPreference.setSummary(currentUpdateIntervalSummary);

            minimum_et = minimum_zoom_level_preference.getEditText();
            maximum_et = maximum_zoom_level_preference.getEditText();

            minimum_zoom_level_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (Double.parseDouble(o.toString()) < 10) {
                        sp.edit().putString(minimum_preference_key, "10").apply();
                        Toast.makeText(MainActivity.getAppContext(), "Minimum zoom must be between 10 and 19", Toast.LENGTH_SHORT).show();
                    } else if (Double.parseDouble(o.toString()) > 19) {
                        sp.edit().putString(minimum_preference_key, "19").apply();
                        Toast.makeText(MainActivity.getAppContext(), "Minimum zoom must be between 10 and 19", Toast.LENGTH_SHORT).show();
                    } else {
                        sp.edit().putString(minimum_preference_key, o.toString()).apply();
                    }
                    String minimum_summary = "Value = " + sp.getString(minimum_preference_key, "14");
                    minimum_zoom_level_preference.setSummary(minimum_summary);
                    return false;
                }
            });

            maximum_zoom_level_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (Double.parseDouble(o.toString()) < 11) {
                        sp.edit().putString(maximum_preference_key, "11").apply();
                        Toast.makeText(MainActivity.getAppContext(), "Maximum zoom must be between 11 and 20", Toast.LENGTH_SHORT).show();
                    } else if (Double.parseDouble(o.toString()) > 20) {
                        sp.edit().putString(maximum_preference_key, "20").apply();
                        Toast.makeText(MainActivity.getAppContext(), "Minimum zoom must be between 11 and 20", Toast.LENGTH_SHORT).show();
                    } else {
                        sp.edit().putString(maximum_preference_key, o.toString()).apply();
                    }
                    String maximum_summary = "Value = " + sp.getString(maximum_preference_key, "16");
                    maximum_zoom_level_preference.setSummary(maximum_summary);
                    return false;
                }
            });

            map_animation_speed_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (isInteger(o.toString())) {
                        if (Integer.parseInt(o.toString()) > 900) {
                            Toast.makeText(MainActivity.getAppContext(),
                                    "Setting animation speed too long can cause map tiles to not load",
                                    Toast.LENGTH_SHORT).show();
                            sp.edit().putString(map_animation_speed_key, o.toString()).apply();
                            String map_animation_summary = "Value = "
                                    + sp.getString(map_animation_speed_key, "900");
                            map_animation_speed_preference.setSummary(map_animation_summary);
                        } else if (Integer.parseInt(o.toString()) < 0) {
                            Toast.makeText(MainActivity.getAppContext(),
                                    "Animation speed should be a positive number",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            sp.edit().putString(map_animation_speed_key, o.toString()).apply();
                            String map_animation_summary = "Value = "
                                    + sp.getString(map_animation_speed_key, "900");
                            map_animation_speed_preference.setSummary(map_animation_summary);
                        }
                    } else {
                        Toast.makeText(MainActivity.getAppContext(),
                                "Animation speed should be an integer in ms",
                                Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });

            currentAddressUpdateIntervalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (isInteger(newValue.toString())) {
                        if (Integer.parseInt(newValue.toString()) <= 0) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Enter a value greater than 0", Toast.LENGTH_SHORT).show();
                        } else {
                            sp.edit().putString(CURRENT_ADDRESS_UPDATE_INTERVAL_KEY, newValue.toString()).apply();
                            String currentAddressUpdateIntervalSummary = "Value = "
                                    + sp.getString(CURRENT_ADDRESS_UPDATE_INTERVAL_KEY, "5");
                            currentAddressUpdateIntervalPreference.setSummary(currentAddressUpdateIntervalSummary);
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Value must be an integer.", Toast.LENGTH_SHORT).show();
                    }

                    return false;
                }
            });
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
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
