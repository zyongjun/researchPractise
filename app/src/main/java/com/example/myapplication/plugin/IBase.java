package com.example.myapplication.plugin;

public interface IBase {
    int getFolderIndex();
    default int getFolderWidth(){
        return 0;
    }
    ISize getSizeImpl();
}
