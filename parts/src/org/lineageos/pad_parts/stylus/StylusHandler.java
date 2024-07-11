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

package org.lineageos.pad_parts.stylus;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;

import org.lineageos.pad_parts.R;

public class StylusHandler implements DeviceKeyHandler, StylusObserver.StylusListener {

    private static final String TAG = "StylusHandler";
    private static final boolean DEBUG = true;

    private final Context mContext;
    private final StylusObserver mObserver;

    private String mCurrentMac = null;

    public StylusHandler(Context context) {
        if (DEBUG) Log.d(TAG, "StylusHandler");

        mContext = context;
        mObserver = new StylusObserver(context, this);
        mObserver.startListening();
    }

    public KeyEvent handleKeyEvent(KeyEvent event) {
        return event;
    }

    @Override
    public void onStylusConnected(boolean connected, int version) {
        if (DEBUG) Log.d(TAG, String.format("onStylusConnected %b %d", connected, version));
        if (!connected) mCurrentMac = null;

        StylusUtils.enableStylus(connected, 2);

        if (mCurrentMac != null) {
            if (DEBUG) Log.d(TAG, "updateBluetoothDeviceInfo");
            StylusUtils.updateBluetoothDeviceInfo(mCurrentMac);
        }
    }

    @Override
    public void onVisibilityChanged(boolean visibility, String mac) {
        if (DEBUG) Log.d(TAG, String.format("onVisibilityChanged %b %s", visibility, mac));
        if (visibility) mCurrentMac = mac;

        Intent receiverIntent = new Intent(StylusReceiver.INTENT_ACTION_STYLUS_VISIBILITY_CHANGED)
                .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT)
                .setPackage(StylusUtils.RECEIVER_PACKAGE)
                .putExtra(StylusReceiver.EXTRA_MAC_ADDRESS, mac);

        mContext.sendBroadcastAsUser(receiverIntent, UserHandle.CURRENT);
    }

}
