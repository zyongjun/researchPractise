package com.example.myapplication.compat;

import com.example.myapplication.plugin.IBase;
import com.example.myapplication.plugin.ISize;

public class BaseCompatV1 extends BaseCompat{
    public BaseCompatV1(IBase base) {
        super(base);
    }

    @Override
    public int getFolderWidth() {
        return mBase.getFolderWidth();
    }

    @Override
    public ISize getSizeImpl() {
        return new ISizeV1(mBase.getSizeImpl());
    }

    static class ISizeV1 implements ISize {
        protected ISize mSize;
        ISizeV1(ISize iSize) {
            mSize = iSize;
        }

        @Override
        public int getScreenWidth() {
            return mSize.getScreenWidth();
        }

        @Override
        public int getScreenHeight() {
            return mSize.getScreenHeight();
        }
    }
}
