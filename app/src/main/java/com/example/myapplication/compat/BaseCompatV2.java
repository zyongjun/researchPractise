package com.example.myapplication.compat;

import com.example.myapplication.plugin.IBase;
import com.example.myapplication.plugin.ISize;

public class BaseCompatV2 extends BaseCompatV1{
    public BaseCompatV2(IBase base) {
        super(base);
    }

    @Override
    public ISize getSizeImpl() {
        return new ISizeV2(mBase.getSizeImpl());
    }

    static class ISizeV2 extends ISizeV1{
        ISizeV2(ISize iSize) {
            super(iSize);
        }
    }
}
