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
import android.os.FileObserver;
import android.util.Log;

import java.util.Objects;

import org.lineageos.pad_parts.utils.FileUtils;

public class KeyboardObserver extends FileObserver {

    private static final String TAG = "KeyboardObserver";
    private static final boolean DEBUG = true;

    interface KeyboardListener {
        void onKeyboardConnected(boolean connected);
    }

    private final KeyboardListener listener;

    private Integer mCurrentStatus = null;

    public KeyboardObserver(KeyboardListener listener) {
        super(KeyboardUtils.KEYBOARD_STATUS_PATH, FileObserver.MODIFY);

        this.listener = listener;
    }

    @Override
    public void onEvent(int event, String path) {
        String status = FileUtils.readOneLine(KeyboardUtils.KEYBOARD_STATUS_PATH);
        if (status == null || status.length() != 1) {
            return;
        }

        if (!Objects.equals(mCurrentStatus, Integer.parseInt(status))) {
            mCurrentStatus = Integer.parseInt(status);

            if (DEBUG) Log.d(TAG, String.format("connection status changed: %d", mCurrentStatus));
        } else {
            return;
        }

        switch (mCurrentStatus) {
            case 0:
                listener.onKeyboardConnected(false);
                break;
            case 1:
                listener.onKeyboardConnected(true);
                break;
            default:
                if (DEBUG) Log.d(TAG, "unknown status " + status);
                break;
        }
    }
}
