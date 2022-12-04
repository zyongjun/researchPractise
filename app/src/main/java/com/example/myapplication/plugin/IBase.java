package com.example.myapplication.plugin;

public interface IBase {
    int getFolderIndex();

    // v1
    default int getFolderWidth(){
        return 0;
    }

    ISize getSizeImpl();
}
