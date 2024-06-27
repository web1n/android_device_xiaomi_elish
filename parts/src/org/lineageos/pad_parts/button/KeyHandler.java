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

import android.content.Context;
import android.content.ContentResolver;
import android.hardware.input.InputManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;

import static org.lineageos.pad_parts.button.ButtonUtils.*;

public class KeyHandler implements DeviceKeyHandler {

    private static final boolean DEBUG = true;
    private static final String TAG = "KeyHandler";

    private static final String HEADSET_BUTTON_DEVICE_NAME = "kona-mtp-snd-card Button Jack";
    private static final String HEADSET_BUTTON_DEVICE_NAME_2 = "kona-mtp-snd-card Headset Jack";
    private static final String STYLUS_BUTTON_DEVICE_NAME = "Xiaomi Smart Pen";

    private static final int HEADSET_BUTTON_UP = 257;
    private static final int HEADSET_BUTTON_DOWN = 258;
    private static final int STYLUS_BUTTON_UP = 104;
    private static final int STYLUS_BUTTON_DOWN = 109;

    private final ContentResolver mContentResolver;
    private final InputManager mInputManager;

    public KeyHandler(Context context) {
        mContentResolver = context.getContentResolver();
        mInputManager = InputManager.getInstance();
    }

    public KeyEvent handleKeyEvent(KeyEvent event) {
        switch (event.getScanCode()) {
            case HEADSET_BUTTON_UP:
            case HEADSET_BUTTON_DOWN:
                return handleHeadsetButtonEvent(event);
            case STYLUS_BUTTON_UP:
            case STYLUS_BUTTON_DOWN:
                return handleStylusButtonEvent(event);
            default:
                return event;
        }
    }

    private KeyEvent handleHeadsetButtonEvent(KeyEvent event) {
        String deviceName = mInputManager.getInputDevice(event.getDeviceId()).getName();
        if (!HEADSET_BUTTON_DEVICE_NAME.equals(deviceName)
                && !HEADSET_BUTTON_DEVICE_NAME_2.equals(deviceName)) {
            return event;
        }

        int keyCode;
        switch (getSettingString(mContentResolver, HEADSET_BUTTON, HEADSET_BUTTON_VOLUME)) {
            case HEADSET_BUTTON_VOLUME:
                keyCode = event.getScanCode() == HEADSET_BUTTON_UP
                        ? KeyEvent.KEYCODE_VOLUME_UP : KeyEvent.KEYCODE_VOLUME_DOWN;
                break;
            case HEADSET_BUTTON_MUSIC:
                keyCode = event.getScanCode() == HEADSET_BUTTON_UP
                        ? KeyEvent.KEYCODE_MEDIA_NEXT : KeyEvent.KEYCODE_MEDIA_PREVIOUS;
                break;
            default:
                return null;
        }

        injectKeyInput(event, keyCode);
        return null;
    }

    private KeyEvent handleStylusButtonEvent(KeyEvent event) {
        if (!STYLUS_BUTTON_DEVICE_NAME.equals(
                mInputManager.getInputDevice(event.getDeviceId()).getName())) {
            return event;
        }

        int keyCode;
        switch (getSettingString(mContentResolver, STYLUS_BUTTON, STYLUS_BUTTON_DEFAULT)) {
            case STYLUS_BUTTON_UPDOWN:
                keyCode = event.getScanCode() == STYLUS_BUTTON_UP
                        ? KeyEvent.KEYCODE_DPAD_UP : KeyEvent.KEYCODE_DPAD_DOWN;
                break;
            case STYLUS_BUTTON_VOLUME:
                keyCode = event.getScanCode() == STYLUS_BUTTON_UP
                        ? KeyEvent.KEYCODE_VOLUME_UP : KeyEvent.KEYCODE_VOLUME_DOWN;
                break;
            case STYLUS_BUTTON_MUSIC:
                keyCode = event.getScanCode() == STYLUS_BUTTON_UP
                        ? KeyEvent.KEYCODE_MEDIA_NEXT : KeyEvent.KEYCODE_MEDIA_PREVIOUS;
                break;
            case STYLUS_BUTTON_DEFAULT:
                return event;
            default:
                return null;
        }

        injectKeyInput(event, keyCode);
        return null;
    }

    private void injectKeyInput(KeyEvent origEvent, int keyCode) {
        KeyEvent key = new KeyEvent(origEvent.getDownTime(), origEvent.getEventTime(),
                origEvent.getAction(), keyCode, origEvent.getRepeatCount());

        if (DEBUG) Log.d(TAG, String.format("inject button %d: %d",
                origEvent.getScanCode(), keyCode));
        mInputManager.injectInputEvent(key, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }
}
