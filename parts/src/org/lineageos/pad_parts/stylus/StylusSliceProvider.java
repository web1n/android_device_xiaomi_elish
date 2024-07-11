/*
 * Copyright (C) 2024 The LineageOS Project
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

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.SliceAction;
import androidx.slice.builders.ListBuilder;

import org.lineageos.pad_parts.button.ButtonSettingsActivity;
import org.lineageos.pad_parts.R;

public class StylusSliceProvider extends SliceProvider {

    private static final String TAG = "StylusSliceProvider";
    private static final boolean DEBUG = true;

    @Override
    public boolean onCreateSliceProvider() {
        return true;
    }

    @Override
    public Slice onBindSlice(Uri sliceUri) {
        if (DEBUG) Log.d(TAG, "onBindSlice: " + sliceUri);
        if (getContext() == null) {
            return null;
        }
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();

        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);

        ListBuilder.RowBuilder buttonSettingBuilder = createButtonSettingBuilder();
        if (buttonSettingBuilder != null) {
            listBuilder.addRow(buttonSettingBuilder);
        }

        ListBuilder.RowBuilder hardwareVersionBuilder = createHardwareVersionBuilder();
        if (hardwareVersionBuilder != null) {
            listBuilder.addRow(hardwareVersionBuilder);
        }

        StrictMode.setThreadPolicy(oldPolicy);
        return listBuilder.build();
    }

    private ListBuilder.RowBuilder createButtonSettingBuilder() {
        if (getContext() == null) {
            return null;
        }

        Intent intent = new Intent(getContext(), ButtonSettingsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new ListBuilder.RowBuilder()
                .setTitle(getContext().getString(R.string.stylus_button_title))
                .setPrimaryAction(createSliceAction(pendingIntent));
    }

    private ListBuilder.RowBuilder createHardwareVersionBuilder() {
        if (getContext() == null) {
            return null;
        }

        int hardwareVersion = StylusUtils.getStylusVersion();
        if (hardwareVersion == -1) {
            return null;
        }
        String versionName = getContext().getString(hardwareVersion == 1
                ? R.string.stylus_hardware_version_1 : R.string.stylus_hardware_version_2);

        Intent intent = new Intent(StylusUtils.INTENT_ACTION_DUMMY)
                .setPackage(StylusUtils.RECEIVER_PACKAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new ListBuilder.RowBuilder()
                .setTitle(getContext().getString(R.string.stylus_hardware_version_title))
                .setSubtitle(versionName)
                .setPrimaryAction(createSliceAction(pendingIntent));
    }

    private SliceAction createSliceAction(PendingIntent pendingIntent) {
        if (getContext() == null) {
            return null;
        }

        return SliceAction.create(pendingIntent,
                IconCompat.createWithResource(getContext(), android.R.drawable.stat_sys_data_bluetooth),
                ListBuilder.ICON_IMAGE, getContext().getString(R.string.app_name));
    }
}
