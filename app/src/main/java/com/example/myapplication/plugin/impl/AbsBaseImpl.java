package com.example.myapplication.plugin.impl;

import android.util.Log;

import com.example.myapplication.plugin.AbsBase;
import com.example.myapplication.plugin.ISize;

public class AbsBaseImpl extends AbsBase {
    private static final String TAG = "AbsBaseImpl";
    @Override
    protected ISize getSizeImpl() {
        return new ISize() {
            @Override
            public int getScreenWidth() {
                Log.d(TAG, "getScreenWidth() called");
                return 4;
            }

            @Override
            public int getScreenHeight() {
                Log.d(TAG, "getScreenHeight() called");
                return 5;
            }
        };
    }
}
