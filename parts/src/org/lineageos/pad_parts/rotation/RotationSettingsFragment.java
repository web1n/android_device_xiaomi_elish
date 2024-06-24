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

package org.lineageos.pad_parts.rotation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.android.settingslib.widget.MainSwitchPreference;

import java.util.Set;

import org.lineageos.pad_parts.R;

public class RotationSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener, OnCheckedChangeListener {
    
    private static final String CATEGORY_APP_LIST = "app_list";

    private SharedPreferences mPrefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.rotation_settings);

        mPrefs = getContext().getSharedPreferences(
                RotationUtils.FORCE_ROTATE_ENABLE, Context.MODE_PRIVATE);

        MainSwitchPreference enablePreference =
                (MainSwitchPreference) findPreference(RotationUtils.FORCE_ROTATE_ENABLE);
        enablePreference.addOnSwitchChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    
        updateApplicationList();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        RotationUtils.checkRotateService(getContext());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if((boolean) newValue) {
            mPrefs.edit().putBoolean(preference.getKey(), true).apply();
        } else {
            mPrefs.edit().remove(preference.getKey()).apply();
        }
        ((SwitchPreference) preference).setChecked((boolean) newValue);

        return false;
    }

    private void updateApplicationList() {
        PackageManager packageManager = getContext().getPackageManager();
        PreferenceCategory category = (PreferenceCategory) findPreference(CATEGORY_APP_LIST);
        Set<String> packages = mPrefs.getAll().keySet();

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        for (ResolveInfo info : packageManager.queryIntentActivities(i, 0)) {
            ApplicationInfo appInfo = info.activityInfo.applicationInfo;
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                continue;
            }

            SwitchPreference preference = new SwitchPreference(getContext());
            preference.setKey(appInfo.packageName);
            preference.setTitle(appInfo.loadLabel(packageManager).toString());
            preference.setSummary(appInfo.packageName);
            preference.setChecked(packages.contains(appInfo.packageName));

            category.addPreference(preference);
            preference.setOnPreferenceChangeListener(this);
        }
    }
}
