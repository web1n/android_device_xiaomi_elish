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

package org.lineageos.pad_parts.rotation;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.Service;
import android.app.TaskStackListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.HashSet;
import java.util.Map;

public class RotationService extends Service implements
    SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "RotationService";
    private static final boolean DEBUG = true;

    private IActivityTaskManager mActivityTaskManager;
    private WindowManager mWindowManager;

    private View mView;
    private WindowManager.LayoutParams mParams;

    private final HashSet<String> mForceRotatePackages = new HashSet<>();

    private String mPreviousApp;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");
        super.onCreate();

        initWindowManager();
        initSharedPreference();
        initTaskManager();
    }

    private void initWindowManager() {
        mWindowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);

        mParams = new WindowManager.LayoutParams();
        mParams.setTitle("ForceRotate");
        mParams.type = WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mParams.privateFlags = WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY;
        mParams.width = 0;
        mParams.height = 0;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;

        mView = new View(this);
        mWindowManager.addView(mView, mParams);
    }

    private void initSharedPreference() {
        SharedPreferences prefs = getSharedPreferences(
                RotationUtils.FORCE_ROTATE_ENABLE, Context.MODE_PRIVATE);

        mForceRotatePackages.addAll(prefs.getAll().keySet());
        mForceRotatePackages.add(RotationUtils.LAUNCHER_PACKAGE_NAME);

        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    private void initTaskManager() {
        try {
            mActivityTaskManager = ActivityTaskManager.getService();
            mActivityTaskManager.registerTaskStackListener(mTaskListener);
        } catch (RemoteException e) {
            // Do nothing
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

        try {
            mActivityTaskManager.unregisterTaskStackListener(mTaskListener);
        } catch (RemoteException e) {
            // Do nothing
        }

        getSharedPreferences(RotationUtils.FORCE_ROTATE_ENABLE, Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);

        mWindowManager.removeViewImmediate(mView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        boolean enable = prefs.getBoolean(key, false);
        if(enable) {
            mForceRotatePackages.add(key);
        } else {
            mForceRotatePackages.remove(key);
        }

        if (DEBUG) Log.d(TAG, String.format("%s force rotate: %b", key, enable));
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String packageName = (String) msg.obj;
            boolean forceRotate = mForceRotatePackages.contains(packageName);
            boolean previousForceRotate =
                    mParams.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER;

            if(forceRotate != previousForceRotate) {
                if(forceRotate) {
                    if(DEBUG) Log.d(TAG, String.format("force rotate: %s", packageName));
    
                    mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
                } else {
                    mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
                }

                mWindowManager.updateViewLayout(mView, mParams);
            }
        }
    };

    private final TaskStackListener mTaskListener = new TaskStackListener() {
        @Override
        public void onTaskMovedToFront(RunningTaskInfo info) {
            String packageName = info.topActivity.getPackageName();
            if (!packageName.equals(mPreviousApp)) {
                mPreviousApp = packageName;
            } else {
                return;
            }

            Message msg = new Message();
            msg.obj = packageName;
        
            mHandler.sendMessage(msg);
        }
    };
}
