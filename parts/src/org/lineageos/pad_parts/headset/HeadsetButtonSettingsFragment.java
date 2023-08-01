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

package org.lineageos.pad_parts.headset;

import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;

import org.lineageos.pad_parts.R;

public class HeadsetButtonSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "HeadsetFragment";
    private static final boolean DEBUG = true;

    private static final String PREFERENCE_UPDOWN_BUTTON = "headset_updown_button";

    private ListPreference profilePerference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.headset_button_settings);

        profilePerference = (ListPreference) findPreference(PREFERENCE_UPDOWN_BUTTON);
        profilePerference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profilePerference.setValue(SystemProperties.get(
                KeyHandler.BUTTON_JACK_PROP, KeyHandler.PROFILE_VOLUME));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (PREFERENCE_UPDOWN_BUTTON.equals(preference.getKey())) {
            SystemProperties.set(KeyHandler.BUTTON_JACK_PROP, (String) newValue);

            ((ListPreference) preference).setValue((String) newValue);
            return false;
        }

        return true;
    }
}
