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

package org.lineageos.pad_parts.rotation;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class RotationUtils {

    private static final String TAG = "RotationUtils";
    private static final boolean DEBUG = true;

    protected static final String FORCE_ROTATE_ENABLE = "force_rotate_enable";
    protected static final String LAUNCHER_PACKAGE_NAME = "com.android.launcher3";

    private static void startService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting service");
        context.startServiceAsUser(new Intent(context, RotationService.class), UserHandle.CURRENT);
    }

    private static void stopService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping service");
        context.stopServiceAsUser(new Intent(context, RotationService.class), UserHandle.CURRENT);
    }

    public static void checkRotateService(Context context) {
        if (isForceRotateEnabled(context)) {
            startService(context);
        } else {
            stopService(context);
        }
    }

    private static boolean isForceRotateEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(FORCE_ROTATE_ENABLE, false);
    }
}
