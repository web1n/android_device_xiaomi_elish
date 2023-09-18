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

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import org.lineageos.pad_parts.R;

public class StylusService extends Service implements StylusObserver.StylusListener {

    private static final String TAG = "StylusService";
    private static final boolean DEBUG = true;

    private StylusObserver mObserver;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");
        super.onCreate();

        mObserver = new StylusObserver(this, this);
        mObserver.startListening();

        int stylusVersion = StylusUtils.getStylusVersion();
        if(stylusVersion != -1) {
            onStylusConnected(true, stylusVersion);
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
        StylusUtils.enableStylus(connected, 2);

        if(connected) {
            String message = getString(
                    R.string.stylus_is_connected,
                    getString(version == 1 ? R.string.stylus_name : R.string.stylus_gen2_name)
            );

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onVisibilityChanged(boolean visibility, String mac) {
        if (DEBUG) Log.d(TAG, String.format("onVisibilityChanged %b %s", visibility, mac));

        Intent receiverIntent = new Intent(StylusReceiver.INTENT_ACTION_STYLUS_VISIBILITY_CHANGED)
                .setPackage(getPackageName())
                .putExtra(StylusReceiver.EXTRA_MAC_ADDRESS, mac);

        sendBroadcastAsUser(receiverIntent, UserHandle.CURRENT);
    }

}
