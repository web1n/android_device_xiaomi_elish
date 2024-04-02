/*
 * Copyright (C) 2023-2024 The LineageOS Project
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

package org.lineageos.pad_parts.button;

import android.os.Bundle;
import android.view.View;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;

import org.lineageos.pad_parts.R;

import static org.lineageos.pad_parts.button.ButtonUtils.*;

public class ButtonSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "ButtonFragment";
    private static final boolean DEBUG = true;

    private static final String PREFERENCE_HEADSET_BUTTON = "headset_button";
    private static final String PREFERENCE_STYLUS_BUTTON = "stylus_button";

    private ListPreference headsetButtonPreference;
    private ListPreference stylusButtonPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.button_settings);

        headsetButtonPreference = (ListPreference) findPreference(PREFERENCE_HEADSET_BUTTON);
        stylusButtonPreference = (ListPreference) findPreference(PREFERENCE_STYLUS_BUTTON);
        headsetButtonPreference.setOnPreferenceChangeListener(this);
        stylusButtonPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        headsetButtonPreference.setValue(getSettingString(
                getContext().getContentResolver(), HEADSET_BUTTON, HEADSET_BUTTON_VOLUME));
        stylusButtonPreference.setValue(getSettingString(
                getContext().getContentResolver(), STYLUS_BUTTON, STYLUS_BUTTON_DEFAULT));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String prop;
        switch (preference.getKey()) {
            case PREFERENCE_HEADSET_BUTTON:
                prop = HEADSET_BUTTON;
                break;
            case PREFERENCE_STYLUS_BUTTON:
                prop = STYLUS_BUTTON;
                break;
            default:
                return true;
        }

        putSettingString(getContext().getContentResolver(), prop, (String) newValue);
        ((ListPreference) preference).setValue((String) newValue);
        return false;
    }
}
