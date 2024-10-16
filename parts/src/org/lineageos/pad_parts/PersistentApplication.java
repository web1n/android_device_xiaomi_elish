/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
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

package org.lineageos.pad_parts;

import android.app.Application;
import android.util.Log;

import org.lineageos.pad_parts.keyboard.KeyboardUtils;
import org.lineageos.pad_parts.rotation.RotationUtils;
import org.lineageos.pad_parts.stylus.StylusUtils;

public class PersistentApplication extends Application {

    private static final boolean DEBUG = true;
    private static final String TAG = "PadParts";

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d(TAG, "PadParts Application onCreate");

        KeyboardUtils.checkKeyboardService(this);
        RotationUtils.checkRotateService(this);
        StylusUtils.checkStylusService(this);
    }
}
