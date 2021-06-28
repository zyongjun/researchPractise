package com.example.myapplication.headset;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.myapplication.MyApplication;

import java.util.Arrays;

public class HeadsetClient {
    private static final String TAG = "HeadsetClient";
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            Log.i(TAG, "onCallStateChanged: state:" + state);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG, "onCallStateChanged: CALL_STATE_IDLE");
                    TelephonyManager telephonyManager = (TelephonyManager) MyApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
                    telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "onCallStateChanged: CALL_STATE_OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG, "onCallStateChanged: CALL_STATE_RINGING");
                    break;
            }
        }
    };

    public boolean checkAudioStatus() {
        Context context = MyApplication.getContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        if (devices == null || devices.length == 0) {
            Log.i(TAG, "no device outputs");
            return false;
        }
        Arrays.stream(devices).forEach(audioDeviceInfo ->
                Log.i(TAG, "accept: output type:" + audioDeviceInfo.getType()));
        return Arrays.stream(devices).map(AudioDeviceInfo::getType)
                .anyMatch(type -> type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                        || type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);
    }

    public int checkPhoneState() {
        Context context = MyApplication.getContext();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int callstate = telephonyManager.getCallState();
        Log.i(TAG, "checkPhoneState: call state:" + callstate);
        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        return callstate;
    }

    public int checkNetCall() {
        Context context = MyApplication.getContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getMode();
        Log.i(TAG, "checkNetCall: mode:" + mode);
        return mode;
    }
}
