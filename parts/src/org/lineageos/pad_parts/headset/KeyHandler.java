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

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;

public class KeyHandler implements DeviceKeyHandler {

    private static final boolean DEBUG = true;
    private static final String TAG = "KeyHandler";

    private static final String BUTTON_DEVICE_NAME = "kona-mtp-snd-card Button Jack";

    protected static final String BUTTON_JACK_PROP
            = "persist.audio.button_jack.profile.lineage";
    protected static final String PROFILE_VOLUME = "volume";
    protected static final String PROFILE_MUSIC = "music";

    private static final int BUTTON_UP = 257;
    private static final int BUTTON_DOWN = 258;

    private final InputManager mInputManager;

    public KeyHandler(Context context) {
        mInputManager = InputManager.getInstance();
    }

    public KeyEvent handleKeyEvent(KeyEvent event) {
        if(event.getScanCode() != BUTTON_UP && event.getScanCode() != BUTTON_DOWN) {
            return event;
        }
        if(!BUTTON_DEVICE_NAME.equals(
                mInputManager.getInputDevice(event.getDeviceId()).getName())) {
            return event;
        }

        String buttonProfile = SystemProperties.get(BUTTON_JACK_PROP, PROFILE_VOLUME);
        if(!buttonProfile.equals(PROFILE_VOLUME) && !buttonProfile.equals(PROFILE_MUSIC)) {
            return null;
        }

        int keyCode = buttonProfile.equals(PROFILE_VOLUME)
                ? (event.getScanCode() == BUTTON_UP
                        ? KeyEvent.KEYCODE_VOLUME_UP : KeyEvent.KEYCODE_VOLUME_DOWN)
                : (event.getScanCode() == BUTTON_UP
                        ? KeyEvent.KEYCODE_MEDIA_NEXT : KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        KeyEvent key = new KeyEvent(event.getDownTime(), event.getEventTime(),
                event.getAction(), keyCode, event.getRepeatCount());

        if (DEBUG) Log.d(TAG, String.format("inject headset button %d: %d",
                event.getScanCode(), keyCode));
        mInputManager.injectInputEvent(key, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);

        return null;
    }
}
