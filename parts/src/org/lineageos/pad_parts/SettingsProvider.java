/*
 * Copyright (C) 2024 The Android Open Source Project
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

package org.lineageos.pad_parts;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import org.lineageos.pad_parts.button.ButtonUtils;
import org.lineageos.pad_parts.utils.SettingsUtils;

import static com.android.settingslib.drawer.SwitchesProvider.EXTRA_SWITCH_CHECKED_STATE;
import static com.android.settingslib.drawer.SwitchesProvider.METHOD_GET_DYNAMIC_SUMMARY;
import static com.android.settingslib.drawer.SwitchesProvider.METHOD_IS_CHECKED;
import static com.android.settingslib.drawer.SwitchesProvider.METHOD_ON_CHECKED_CHANGED;

import static com.android.settingslib.drawer.TileUtils.META_DATA_PREFERENCE_SUMMARY;

public class SettingsProvider extends ContentProvider {

    private static final boolean DEBUG = true;
    private static final String TAG = "SettingsProvider";

    private static final String KEY_BUTTON = "button";

    @Override
    public Bundle call(String method, String uri, Bundle extras) {
        final String key = getKeyFromUriStr(uri);
        if (DEBUG) Log.d(TAG, "method: " + method + " key: " + key + " extras: " + extras);
        if (method == null || !isValidKey(key)) {
            return null;
        }

        switch (method) {
            case METHOD_GET_DYNAMIC_SUMMARY:
                return handleDynamicSummaryCall(key);
            case METHOD_IS_CHECKED:
                return handleIsCheckedCall(key);
            case METHOD_ON_CHECKED_CHANGED:
                return handleOnCheckedChangedCall(key, extras);
            default:
                Log.w(TAG, "Unsupported method: " + method);
                return null;
        }
    }

    private Bundle handleDynamicSummaryCall(String key) {
        String summary = null;
        if (KEY_BUTTON.equals(key)) {
            summary = ButtonUtils.getButtonSettingsSummary(getContext());
        } else if (SettingsUtils.isValidPrefKey(key)) {
            boolean enabled = SettingsUtils.isSettingEnabled(getContext(), key);
            summary = getContext().getString(
                    enabled ? R.string.summary_enabled : R.string.summary_disabled);
        }

        Bundle bundle = new Bundle();
        bundle.putString(META_DATA_PREFERENCE_SUMMARY, summary);
        return bundle;
    }

    private Bundle handleIsCheckedCall(String key) {
        boolean isChecked = SettingsUtils.isSettingEnabled(getContext(), key);

        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_SWITCH_CHECKED_STATE, isChecked);
        return bundle;
    }

    private Bundle handleOnCheckedChangedCall(String key, Bundle extras) {
        boolean checked = extras.getBoolean(EXTRA_SWITCH_CHECKED_STATE);

        SettingsUtils.setSettingEnabled(getContext(), key, checked);
        return new Bundle();
    }

    private static boolean isValidKey(String key) {
        return KEY_BUTTON.equals(key) || SettingsUtils.isValidPrefKey(key);
    }

    /** Returns method and key of the complete uri. */
    private static String getKeyFromUriStr(String uri) {
        if (uri == null) {
            return null;
        }
        final List<String> pathSegments = Uri.parse(uri).getPathSegments();
        if (pathSegments == null || pathSegments.size() < 2) {
            return null;
        }
        return pathSegments.get(1);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

}
