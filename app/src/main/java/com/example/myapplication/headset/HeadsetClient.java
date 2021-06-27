package com.example.myapplication.headset;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.util.Log;

import com.example.myapplication.MyApplication;

import java.util.Arrays;

public class HeadsetClient {
    private static final String TAG = "HeadsetClient";

    public boolean checkAudioStatus() {
        Context context = MyApplication.getContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        if (devices == null || devices.length == 0) {
            return false;
        }
        Arrays.stream(devices).forEach(audioDeviceInfo ->
                Log.i(TAG, "accept: output type:" + audioDeviceInfo.getType()));
        return Arrays.stream(devices).map(AudioDeviceInfo::getType)
                .anyMatch(type -> type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                        || type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);
    }
}
