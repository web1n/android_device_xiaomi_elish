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

package org.lineageos.pad_parts.keyboard;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

import org.lineageos.pad_parts.utils.FileUtils;

public class KeyboardUtils {

    private static final String TAG = "KeyboardUtils";
    private static final boolean DEBUG = true;

    protected static final String KEYBOARD_STATUS_PATH = "/sys/devices/platform/soc/soc:xiaomi_keyboard/xiaomi_keyboard_conn_status";
    protected static final String KEYBOARD_ENABLE = "keyboard_enable";

    public static void startService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting service");
        context.startServiceAsUser(new Intent(context, KeyboardService.class), UserHandle.CURRENT);
    }

    protected static void stopService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping service");
        context.stopServiceAsUser(new Intent(context, KeyboardService.class), UserHandle.CURRENT);
    }

    public static void checkKeyboardService(Context context) {
        if (isKeyboardEnabled(context)) {
            startService(context);
        } else {
            stopService(context);
        }
    }

    protected static void enableKeyboard(boolean enable) {
        FileUtils.writeLine(KEYBOARD_STATUS_PATH, enable ? "enable_keyboard" : "disable_keyboard");
    }

    private static boolean isKeyboardEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEYBOARD_ENABLE, false);
    }

    protected static void setShowImeWithHardKeyboard(Context context, boolean show) {
        Secure.putIntForUser(context.getContentResolver(), Secure.SHOW_IME_WITH_HARD_KEYBOARD,
                show ? 1 : 0, UserHandle.USER_CURRENT);
    }

}
