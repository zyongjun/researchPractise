package com.example.myapplication.compat;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.plugin.IBase;
import com.example.myapplication.plugin.ISize;

public class BaseCompat implements IBase{
    protected IBase mBase;
    public BaseCompat(IBase base) {
        mBase = base;
    }

    public static BaseCompat getCompat(IBase base) {
        int pdkVersion = MyApplication.getContext().getResources().getInteger(R.integer.pdk_code);
        BaseCompat compat = new BaseCompat(base);
        if (pdkVersion == 1) {
            compat = new BaseCompatV1(base);
        } else if (pdkVersion == 2) {
            compat = new BaseCompatV2(base);
        }
        return compat;
    }

    @Override
    public int getFolderIndex() {
        return mBase.getFolderIndex();
    }

    @Override
    public ISize getSizeImpl() {
        return mBase.getSizeImpl();
    }
}
