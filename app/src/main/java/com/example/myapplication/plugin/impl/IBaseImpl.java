package com.example.myapplication.plugin.impl;

import android.util.Log;

import com.example.myapplication.plugin.IBase;
import com.example.myapplication.plugin.ISize;

public class IBaseImpl implements IBase {
    private static final String TAG = "IBaseImpl";

    @Override
    public int getFolderIndex() {
        Log.d(TAG, "getFolderIndex() called");
        return 2;
    }

    @Override
    public ISize getSizeImpl() {
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
