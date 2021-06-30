package com.example.myapplication.headset;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.support.annotation.IntDef;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.myapplication.MyApplication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public class HeadsetStatusInspector {
    public static final int STATE_NONE_OCCUPIED = 0;
    public static final int STATE_PHONE_CALL = 1;
    public static final int STATE_MEDIA_OCCUPIED = 2;
    public static final int STATE_NET_CALL_OCCUPIED = 3;
    private static final String TAG = "HeadsetStatusInspector";
    private Runnable mRunnable;

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            Log.i(TAG, "onCallStateChanged: state:" + state);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG, "onCallStateChanged: CALL_STATE_IDLE");
                    mRunnable.run();
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

    @IntDef({STATE_PHONE_CALL, STATE_MEDIA_OCCUPIED, STATE_NET_CALL_OCCUPIED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HeadsetStatus {
    }

    public void listenAudioState(@HeadsetStatus int status, Runnable runnable) {
        Log.i(TAG, "listenAudioState: status:" + status);
        mRunnable = runnable;
        if (status == STATE_PHONE_CALL) {
            Context context = MyApplication.getContext();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int callstate = telephonyManager.getCallState();
            Log.i(TAG, "checkPhoneState: call state:" + callstate);
            telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            return;
        }
        if (status == STATE_NET_CALL_OCCUPIED) {
            listenAudioFocusChange();
        }
    }

    public @HeadsetStatus
    int checkHeadsetStatus() {
        if (isInPhoneCall()) {
            return STATE_PHONE_CALL;
        }
        if (isInNetCall()) {
            return STATE_NET_CALL_OCCUPIED;
        }
        if (isMediaPlaying()) {
            return STATE_MEDIA_OCCUPIED;
        }
        return STATE_NONE_OCCUPIED;
    }

    private boolean isInPhoneCall() {
        Context context = MyApplication.getContext();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int callstate = telephonyManager.getCallState();
        Log.i(TAG, "checkPhoneState: call state:" + callstate);
        return callstate == TelephonyManager.CALL_STATE_OFFHOOK || callstate == TelephonyManager.CALL_STATE_RINGING;
    }

    public boolean isRemindEnable() {
        return isHeadsetSwitchOpen() && isHeadsetEnable();
    }

    private boolean isHeadsetSwitchOpen() {
        boolean isSwitchOpen = true;
        Log.i(TAG, "isHeadsetSwitchOpen: " + isSwitchOpen);
        return isSwitchOpen;
    }

    public boolean isHeadsetEnable() {
        Context context = MyApplication.getContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        if (devices == null || devices.length == 0) {
            Log.i(TAG, "no device outputs");
            return false;
        }
        boolean isHeadsetEnable = Arrays.stream(devices).map(AudioDeviceInfo::getType)
                .anyMatch(type -> type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                        || type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);
        Log.i(TAG, "isHeadsetEnable: " + isHeadsetEnable);
        return isHeadsetEnable;
    }

    public int checkPhoneState() {
        Context context = MyApplication.getContext();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int callstate = telephonyManager.getCallState();
        Log.i(TAG, "checkPhoneState: call state:" + callstate);
        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        return callstate;
    }

    private boolean isMediaPlaying() {
        Context context = MyApplication.getContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getMode();
        // audioManager.isMicrophoneMute()
        // todo video check
        boolean isMediaPlaying = audioManager.isMusicActive();
        Log.i(TAG, "isMediaPlaying: " + isMediaPlaying);
        return isMediaPlaying;
    }

    private boolean isInNetCall() {
        Context context = MyApplication.getContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getMode();
        Log.i(TAG, "isInNetCall: mode:" + mode);
        return mode == AudioManager.MODE_IN_COMMUNICATION || mode == AudioManager.MODE_IN_CALL;
    }

    public int checkNetCall() {
        Context context = MyApplication.getContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getMode();
        Log.i(TAG, "checkNetCall: mode:" + mode);
        return mode;
    }

    private AudioFocusRequest mAudioFocusVoice;

    public void turnDownVolume(Runnable runnable) {
        Context context = MyApplication.getContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //1、创建声音焦点 requestBuilder
        AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder();
        attributesBuilder.setUsage(AudioAttributes.USAGE_MEDIA); // 表明是那种音源类型：这里是语音识别类型
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);//上下文，一般用不上
        //2、这个参数 AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)下面会进行详细说明
        AudioFocusRequest.Builder requestBuilder = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);//申请声音焦点类型
        requestBuilder.setAudioAttributes(attributesBuilder.build())
                .setAcceptsDelayedFocusGain(false) //是否接受延迟获取声音焦点
                //设置对声音焦点监听
                .setOnAudioFocusChangeListener(focusChange -> {
                    Log.i(TAG, "onAudioFocusChange: focusChange:" + focusChange);
                    if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        runnable.run();
                        audioManager.abandonAudioFocusRequest(mAudioFocusVoice);
                    }
                });
        mAudioFocusVoice = requestBuilder.build();
        //4、向AudioManager发起声音焦点申请。
        int voiceFocus = audioManager.requestAudioFocus(mAudioFocusVoice);
        Log.i(TAG, "setAudio: voiceFocus" + voiceFocus);
    }

    private void listenAudioFocusChange() {
        Context context = MyApplication.getContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //1、创建声音焦点 requestBuilder
        AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder();
        attributesBuilder.setUsage(AudioAttributes.USAGE_MEDIA); // 表明是那种音源类型：这里是语音识别类型
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);//上下文，一般用不上
        //2、这个参数 AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)下面会进行详细说明
        AudioFocusRequest.Builder requestBuilder = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);//申请声音焦点类型
        requestBuilder.setAudioAttributes(attributesBuilder.build())
                .setAcceptsDelayedFocusGain(true) //是否接受延迟获取声音焦点
                //设置对声音焦点监听
                .setOnAudioFocusChangeListener(focusChange -> {
                    Log.i(TAG, "onAudioFocusChange: focusChange:" + focusChange);
                    if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        audioManager.abandonAudioFocusRequest(mAudioFocusVoice);
                        mRunnable.run();

                    }
                });
        mAudioFocusVoice = requestBuilder.build();
        //4、向AudioManager发起声音焦点申请。
        int voiceFocus = audioManager.requestAudioFocus(mAudioFocusVoice);
        Log.i(TAG, "setAudio: voiceFocus" + voiceFocus);
    }
}
