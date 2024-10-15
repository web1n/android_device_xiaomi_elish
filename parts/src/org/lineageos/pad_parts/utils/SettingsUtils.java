/*
 * Copyright (C) 2024 The CyanogenMod Project
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

package org.lineageos.pad_parts.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Map;

import org.lineageos.pad_parts.keyboard.KeyboardUtils;
import org.lineageos.pad_parts.rotation.RotationUtils;
import org.lineageos.pad_parts.stylus.StylusUtils;

import static com.android.settingslib.drawer.SwitchesProvider.METHOD_GET_DYNAMIC_SUMMARY;
// import static com.android.settingslib.drawer.SwitchesProvider.METHOD_IS_CHECKED;

public final class SettingsUtils {

    private static final boolean DEBUG = true;
    private static final String TAG = "SettingsUtils";

    public static final String AUTHORITY_SETTINGS = "org.lineageos.pad_parts.settings";

    public static final String FORCE_ROTATE_ENABLE = "force_rotate_enable";
    public static final String KEYBOARD_ENABLE = "keyboard_enable";
    public static final String COMPATIBLE_STYLUS_ENABLE = "compatible_stylus_enable";

    public static final Uri SETTINGS_AUTHORITY_URI =
            Uri.parse("content://org.lineageos.pad_parts.settings");
    public static final String METHOD_GET_CONFIG_VALUE = "getConfigValue";
    public static final String EXTRA_CONFIG_VALUE = "org.lineageos.pad_parts.CONFIG_VALUE";

    public static boolean isValidSwitchKey(String key) {
        return FORCE_ROTATE_ENABLE.equals(key)
                || KEYBOARD_ENABLE.equals(key)
                || COMPATIBLE_STYLUS_ENABLE.equals(key);
    }

    public static boolean isSettingEnabled(Context context, String prefKey) {
        if (context == null || !isValidSwitchKey(prefKey)) {
            return false;
        }

        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(prefKey, false);
    }

    public static void setSettingEnabled(Context context, String prefKey, boolean enabled) {
        if (DEBUG) Log.d(TAG, "setSettingEnabled: " + prefKey + " enabled: " + enabled);
        if (context == null || !isValidSwitchKey(prefKey)) {
            return;
        }

        // put to shared preference
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(prefKey, enabled).commit();

        checkService(context, prefKey);
        notifySettingChange(context, prefKey);
    }

    public static String getConfigValueString(Context context, String prefKey) {
        if (context == null) {
            return null;
        }

        Map<String, ?> allEntries =
                PreferenceManager.getDefaultSharedPreferences(context).getAll();
        Object value = allEntries.get(prefKey);
        return value != null ? value.toString() : null;
    }

    public static String getConfigValueString(ContentResolver resolver, String prefKey, String def) {
        Uri configValueUri = SETTINGS_AUTHORITY_URI.buildUpon()
                .appendPath(METHOD_GET_CONFIG_VALUE)
                .appendPath(prefKey)
                .build();

        Bundle result = resolver.call(SETTINGS_AUTHORITY_URI,
                METHOD_GET_CONFIG_VALUE, configValueUri.toString(), null);
        if (result != null) {
            String configValue = result.getString(EXTRA_CONFIG_VALUE);
            Log.e(TAG, "getSettingString: " + prefKey + " value: " + configValue);
            if (configValue != null) return configValue;
        }

        return def;
    }

    private static void checkService(Context context, String key) {
        if (DEBUG) Log.d(TAG, "checkService: " + key);
        if (context == null || !isValidSwitchKey(key)) {
            return;
        }

        switch (key) {
            case FORCE_ROTATE_ENABLE:
                RotationUtils.checkRotateService(context);
                break;
            case KEYBOARD_ENABLE:
                KeyboardUtils.checkKeyboardService(context);
                break;
        }
    }

    private static void notifySettingChange(Context context, String key) {
        if (DEBUG) Log.d(TAG, "notifyChange: " + key);
        if (context == null || !isValidSwitchKey(key)) {
            return;
        }

        ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(buildSettingProviderUri(METHOD_GET_DYNAMIC_SUMMARY, key), null);
        // resolver.notifyChange(buildSettingProviderUri(METHOD_IS_CHECKED, key), null);
    }

    private static Uri buildSettingProviderUri(String method, String key) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY_SETTINGS)
                .appendPath(method)
                .appendPath(key)
                .build();
    }

}
