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

import android.content.ContentResolver;

import lineageos.providers.LineageSettings;

public class ButtonUtils {
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

}
