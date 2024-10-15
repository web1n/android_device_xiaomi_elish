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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import android.view.InputDevice;

import org.lineageos.pad_parts.R;
import org.lineageos.pad_parts.utils.SettingsUtils;

import vendor.xiaomi_elish.peripherals.V1_0.IPeripherals;

public class StylusUtils {

    private static final String TAG = "StylusUtils";
    private static final boolean DEBUG = true;

    protected static final String RECEIVER_PACKAGE =  "org.lineageos.pad_parts";

    private static final int STYLUS_NOTIFICATION_ID = 10;
    private static final String STYLUS_NOTIFICATION_CHANNEL_ID = "XIAOMI_STYLUS";

    private static final int INPUT_VENDOR_ID_XIAOMI = 0x1915;
    private static final int INPUT_PRODUCT_ID_XIAOMI_STYLUS = 0xEAEA;
    private static final int INPUT_PRODUCT_ID_XIAOMI_STYLUS_2 = 0x4D81;

    private static final int METADATA_FAST_PAIR_CUSTOMIZED_FIELDS = 25;
    private static final String SLICE_SETTINGS_URI = "content://org.lineageos.pad_parts.stylus/settings";
    private static final String FAST_PAIR_CUSTOMIZED_FIELDS =
            "<HEARABLE_CONTROL_SLICE_WITH_WIDTH>" + SLICE_SETTINGS_URI + "</HEARABLE_CONTROL_SLICE_WITH_WIDTH>";
    protected static final String INTENT_ACTION_DUMMY = "org.lineageos.pad_parts.action.DUMMY";

    public static void startService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting service");
        context.startServiceAsUser(new Intent(context, StylusService.class), UserHandle.CURRENT);
    }

    public static void checkStylusService(Context context) {
        if (SettingsUtils.isSettingEnabled(context, SettingsUtils.COMPATIBLE_STYLUS_ENABLE)) {
            if (DEBUG) Log.d(TAG, "Compatible stylus support enabled");
            enableStylus(true);
        } else {
            startService(context);
        }
    }

    protected static boolean enableStylus(boolean enable) {
        boolean result = false;
        try {
            IPeripherals peripherals = IPeripherals.getService();
            if (peripherals.isStylusEnabled() == enable) {
                return true;
            }

            result = peripherals.setStylusEnable(enable);
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, e.toString());
        }

        if (DEBUG) Log.d(TAG, String.format("setStylueEnable flag: %b, result: %b", enable, result));
        return result;
    }

    protected static int getStylusVersion() {
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(id);

            if (getStylusVersion(device) != -1) {
                return getStylusVersion(device);
            }
        }

        return -1;
    }

    public static int getStylusVersion(InputDevice device) {
        if (device.getVendorId() == INPUT_VENDOR_ID_XIAOMI) {
            if (device.getProductId() == INPUT_PRODUCT_ID_XIAOMI_STYLUS) {
                return 1;
            } else if (device.getProductId() == INPUT_PRODUCT_ID_XIAOMI_STYLUS_2) {
                return 2;
            }
        }

        // can only get when bluetooth connected
        return -1;
    }

    private static BluetoothDevice getBluetoothDevice(String mac) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (mac == null || adapter == null || !adapter.isEnabled()) {
            return null;
        }

        return adapter.getRemoteDevice(mac);
    }

    private static void setBluetoothDeviceMetadata(BluetoothDevice device, int key, String value) {
        if (device == null || value == null || device.getMetadata(key) != null) {
            return;
        }

        device.setMetadata(key, value.getBytes());
    }

    protected static void updateBluetoothDeviceInfo(String mac) {
        BluetoothDevice device = getBluetoothDevice(mac);
        if (device == null) {
            return;
        }

        setBluetoothDeviceMetadata(device,
                BluetoothDevice.METADATA_DEVICE_TYPE, BluetoothDevice.DEVICE_TYPE_STYLUS);
        setBluetoothDeviceMetadata(device,
                METADATA_FAST_PAIR_CUSTOMIZED_FIELDS, FAST_PAIR_CUSTOMIZED_FIELDS);
    }

    protected static String macFormat(String mac) {
        if (mac.length() != 12) {
            return null;
        }

        String upperCase = mac.toUpperCase();
        String[] split = new String[6];

        for (int i = 0; i < mac.length() / 2; i++) {
            split[i] = upperCase.substring(i * 2, i * 2 + 2);
        }

        return String.join(":", split);
    }

    protected static void sendNotification(Context context, String mac) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        Intent pairIntent = new Intent(StylusReceiver.INTENT_ACTION_PAIR_STYLUS)
                .setPackage(context.getPackageName())
                .putExtra(StylusReceiver.EXTRA_MAC_ADDRESS, mac);
        PendingIntent pairPendingIntent = PendingIntent.getBroadcast(context, 0,
                pairIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(context, STYLUS_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.stylus_name))
                .setContentText(context.getString(R.string.stylus_detect_notification))
                .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                .setContentIntent(pairPendingIntent)
                .build();

        if (notificationManager.getNotificationChannel(STYLUS_NOTIFICATION_CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(
                    STYLUS_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.stylus_notification_channel),
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(STYLUS_NOTIFICATION_ID, notification);
    }

    protected static void cancelNotification(Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        notificationManager.cancel(STYLUS_NOTIFICATION_ID);
    }
}
