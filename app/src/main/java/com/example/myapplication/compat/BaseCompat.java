package com.example.myapplication.compat;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.plugin.IBase;
import com.example.myapplication.plugin.ISize;

public class BaseCompat implements IBase{

    public static BaseCompat getCompat() {
        int pdkVersion = MyApplication.getContext().getResources().getInteger(R.integer.pdk_code);
        BaseCompat compat = new BaseCompat();
        if (pdkVersion == 1) {
            compat = new BaseCompatV1();
        } else if (pdkVersion == 2) {
            compat = new BaseCompatV2();
        }
        return compat;
    }

    @Override
    public int getFolderIndex() {
        return 0;
    }

    @Override
    public ISize getSizeImpl() {
        return null;
    }
}
