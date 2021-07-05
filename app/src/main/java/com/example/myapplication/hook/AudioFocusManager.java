package com.example.myapplication.hook;

import android.util.Log;

public class AudioFocusManager {
    private static final String TAG = "AudioFocusManager";
    private AudioFocusManager(){

    }

    private static final class Holder{
        private static final AudioFocusManager INSTANCE = new AudioFocusManager();
    }

    public static AudioFocusManager getInstance(){
        return Holder.INSTANCE;
    }

    public void printA(){
        Log.i(TAG,"print A");
    }

    public void printB(){
        Log.i(TAG, "printB: B");
    }
}
