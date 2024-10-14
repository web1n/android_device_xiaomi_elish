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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;

import org.lineageos.pad_parts.R;

public class StylusService extends Service implements StylusObserver.StylusListener {

    private static final String TAG = "StylusService";
    private static final boolean DEBUG = true;

    private StylusObserver mObserver;

    private String mCurrentMac = null;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");
        super.onCreate();

        mObserver = new StylusObserver(this, this);
        mObserver.startListening();

        if (StylusUtils.getStylusVersion() != -1) {
            StylusUtils.enableStylus(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        super.onDestroy();

        mObserver.stopListening();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStylusConnected(boolean connected, int version) {
        if (DEBUG) Log.d(TAG, String.format("onStylusConnected %b %d", connected, version));
        if (!connected) mCurrentMac = null;

        StylusUtils.enableStylus(connected);

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

        sendBroadcastAsUser(receiverIntent, UserHandle.CURRENT);
    }

}
