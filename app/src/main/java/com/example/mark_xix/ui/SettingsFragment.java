package com.example.mark_xix.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.example.mark_xix.api.ApiServiceGenerator;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        EditTextPreference editTextPreference=new EditTextPreference(context);
        editTextPreference.setKey("ip_address");
        editTextPreference.setTitle("IP Address");
        editTextPreference.setSummary("IP Address for Robot.");

        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String pattern="^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

                if (!newValue.toString().matches(pattern)){
                    Toast.makeText(getContext(),"Should be a valid IP address.",Toast.LENGTH_SHORT).show();
                    return false;
                }else {
                    ApiServiceGenerator.setApiBaseUrl(newValue.toString());
                    return true;
                }
            }
        });

        screen.addPreference(editTextPreference);

        setPreferenceScreen(screen);
    }
}
