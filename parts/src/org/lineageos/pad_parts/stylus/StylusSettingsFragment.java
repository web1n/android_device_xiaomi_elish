/*
 * Copyright (C) 2023 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.pad_parts.stylus;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.widget.Switch;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import org.lineageos.pad_parts.R;

public class StylusSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener, OnMainSwitchChangeListener {

    private static final String TAG = "StylusFragment";
    private static final boolean DEBUG = true;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.stylus_settings);

        MainSwitchPreference enablePreference = (MainSwitchPreference) findPreference(StylusUtils.STYLUS_ENABLE);
        ListPreference driverPerference = (ListPreference) findPreference(StylusUtils.STYLUS_DRIVER_VERSION);

        enablePreference.addOnSwitchChangeListener(this);
        driverPerference.setOnPreferenceChangeListener(this);

        int penVersion = StylusUtils.getStylusVersion(getContext());
        if (penVersion != -1) {
            if (DEBUG) Log.d(TAG, String.format("Xiaomi Smart Pen version: %d", penVersion));
        }
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        Toast.makeText(getContext(), R.string.stylus_restart_needed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (StylusUtils.STYLUS_DRIVER_VERSION.equals(preference.getKey())) {
            if(StylusUtils.getStylusVersion(getContext()) != -1) {
                StylusUtils.enableStylus(getContext(), false);
                StylusUtils.enableStylus(getContext(), true, Integer.parseInt((String) newValue));
            }
        }

        return true;
    }
}
