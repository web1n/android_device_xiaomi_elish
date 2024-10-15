/*
 * Copyright (C) 2024 The LineageOS Project
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

import android.content.ComponentName;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.InputDevice;

import java.util.Arrays;

import lineageos.providers.LineageSettings;

import org.lineageos.pad_parts.Constants;
import org.lineageos.pad_parts.R;

import static org.lineageos.pad_parts.stylus.StylusUtils.getStylusVersion;

public class ButtonUtils {

    private static final String HEADSET_BUTTON_DEVICE_NAME = "kona-mtp-snd-card Button Jack";
    private static final String HEADSET_BUTTON_DEVICE_NAME_2 = "kona-mtp-snd-card Headset Jack";

    protected static final String HEADSET_BUTTON = "padparts.button.headset";
    protected static final String HEADSET_BUTTON_VOLUME = "volume";
    protected static final String HEADSET_BUTTON_MUSIC = "music";
    protected static final String STYLUS_BUTTON = "padparts.button.stylus";
    protected static final String STYLUS_BUTTON_DEFAULT = "default";
    protected static final String STYLUS_BUTTON_UPDOWN = "updown";
    protected static final String STYLUS_BUTTON_VOLUME = "volume";
    protected static final String STYLUS_BUTTON_MUSIC = "music";

    protected static String getSettingString(ContentResolver resolver, String key, String def) {
        String str = LineageSettings.Global.getStringForUser(resolver, key, 0);
        return str == null ? def : str;
    }

    protected static void putSettingString(ContentResolver resolver, String key, String value) {
        LineageSettings.Global.putStringForUser(resolver, key, value, 0);
    }

    protected static boolean isStylusButtonsEnabled(ContentResolver resolver) {
        return Settings.Secure.getIntForUser(resolver,
                Settings.Secure.STYLUS_BUTTONS_ENABLED, 1, UserHandle.USER_CURRENT) == 1;
    }

    protected static boolean isHeadsetButtonDevice(InputDevice device) {
        if (device == null) {
            return false;
        }
        final String deviceName = device.getName();

        return HEADSET_BUTTON_DEVICE_NAME.equals(deviceName)
                || HEADSET_BUTTON_DEVICE_NAME_2.equals(deviceName);
    }

    public static boolean isStylusDevice(InputDevice device) {
        if (device == null) {
            return false;
        }

        return getStylusVersion(device) != -1;
    }

    public static String getButtonSettingsSummary(Context context) {
        if (context == null) {
            return null;
        }
        Resources res = context.getResources();
        ContentResolver resolver = context.getContentResolver();

        int headsetIndex = Arrays.asList(res.getStringArray(R.array.headset_button_values))
                .indexOf(getSettingString(resolver, HEADSET_BUTTON, HEADSET_BUTTON_MUSIC));
        int stylusIndex = Arrays.asList(res.getStringArray(R.array.stylus_button_values))
                .indexOf(getSettingString(resolver, STYLUS_BUTTON, STYLUS_BUTTON_DEFAULT));
        headsetIndex = headsetIndex == -1 ? 0 : headsetIndex;
        stylusIndex = stylusIndex == -1 ? 0 : stylusIndex;

        return String.format("%s: %s, %s: %s",
                res.getString(R.string.headset_button_title),
                res.getStringArray(R.array.headset_button_entries)[headsetIndex],
                res.getString(R.string.stylus_button_title),
                res.getStringArray(R.array.stylus_button_entries)[stylusIndex]);
    }

    public static void enableButtonSettingsActivity(Context context) {
        if (context == null) {
            return;
        }
        final ComponentName component = new ComponentName(
                Constants.PACKAGE_NAME, ButtonSettingsActivity.class.getName());

        context.getPackageManager().setComponentEnabledSetting(component,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static boolean isButtonSettingsActivityEnabled(Context context) {
        if (context == null) {
            return false;
        }
        final ComponentName component = new ComponentName(
                Constants.PACKAGE_NAME, ButtonSettingsActivity.class.getName());

        PackageManager pm = context.getPackageManager();
        int state = pm.getComponentEnabledSetting(component);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            try {
                return pm.getActivityInfo(component, 0).enabled;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }
        return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

}
