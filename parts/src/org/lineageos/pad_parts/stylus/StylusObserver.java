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

package org.lineageos.pad_parts.stylus;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.UEventObserver;
import android.util.Log;
import android.view.InputDevice;

import java.util.Objects;

public class StylusObserver extends UEventObserver implements InputManager.InputDeviceListener {

    private static final String TAG = "StylusObserver";
    private static final boolean DEBUG = true;

    interface StylusListener {
        void onStylusConnected(boolean connected, int version);

        void onVisibilityChanged(boolean visibility, String mac);
    }

    private final InputManager mInputManager;
    private final StylusListener mListener;

    private int mConnectedDevice = -1;
    private String mCurrentMac = null;

    public StylusObserver(Context context, StylusListener listener) {
        this.mInputManager = context.getSystemService(InputManager.class);
        this.mListener = listener;
    }

    @Override
    public void onUEvent(UEvent event) {
        String mac = StylusUtils.macFormat(event.get("POWER_SUPPLY_PEN_MAC"));

        if (!Objects.equals(mCurrentMac, mac)) {
            if (DEBUG) Log.d(TAG, String.format("stylus mac changed: %s", mac));

            mListener.onVisibilityChanged(mac != null, mac);
        }

        mCurrentMac = mac;
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        if (mConnectedDevice != -1) {
            return;
        }
        int stylus = StylusUtils.getStylusVersion(InputDevice.getDevice(deviceId));
        if (stylus == -1) {
            return;
        }
        if (DEBUG) Log.d(TAG, "stylus connected");

        mConnectedDevice = deviceId;
        mListener.onStylusConnected(true, stylus);
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        if (mConnectedDevice != deviceId) {
            return;
        }
        if (DEBUG) Log.d(TAG, "stylus disconnected");

        mConnectedDevice = -1;
        mListener.onStylusConnected(false, -1);
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        if (DEBUG) Log.d(TAG, "onInputDeviceChanged");
    }

    public void startListening() {
        super.startObserving("POWER_SUPPLY_PEN_MAC");

        mInputManager.registerInputDeviceListener(this, null);
    }

    public void stopListening() {
        super.stopObserving();

        mInputManager.unregisterInputDeviceListener(this);
    }
}
