package com.example.myapplication.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;

public class InstallReceiver extends BroadcastReceiver {
    private static final String TAG = "InstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.i(TAG, "onReceive: action:" + intent.getAction());
            final int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS,
                    PackageInstaller.STATUS_FAILURE);
            if (status == PackageInstaller.STATUS_SUCCESS) {
                // success
                Log.i(TAG, "APP Install Success!");
            } else {
                String msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
                Log.e(TAG, "Install FAILURE status_massage" + msg);
            }
        }
    }
}
