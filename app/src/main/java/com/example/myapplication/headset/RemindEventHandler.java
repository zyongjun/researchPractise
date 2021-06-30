package com.example.myapplication.headset;

import android.util.Log;

import java.util.Stack;

public class RemindEventHandler {
    private static final String TAG = "RemindEventHandler";
    private Stack<RemindEvent> mRemindList = new Stack<>();
    private HeadsetStatusInspector mHeadsetStatusInspector = new HeadsetStatusInspector();

    public void postRemindEvent(RemindEvent remindEvent) {
        if (remindEvent == null) {
            Log.w(TAG, "postRemindEvent: event is null");
            return;
        }
        Log.i(TAG, "postRemindEvent: event:" + remindEvent.toString());
        mRemindList.push(remindEvent);
        remindEvent();
    }

    public void remindEvent() {
        if (!mHeadsetStatusInspector.isRemindEnable()) {
            Log.i(TAG, "remindEvent: remind not enable");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int headsetStatus = mHeadsetStatusInspector.checkHeadsetStatus();
                if (headsetStatus == HeadsetStatusInspector.STATE_NONE_OCCUPIED) {
                    realRemind();
                    return;
                }
                if(headsetStatus == HeadsetStatusInspector.STATE_MEDIA_OCCUPIED){
                    // todo 降低音量
                    mHeadsetStatusInspector.turnDownVolume(new Runnable() {
                        @Override
                        public void run() {
                            realRemind();
                        }
                    });
                    return;
                }
                mHeadsetStatusInspector.listenAudioState(headsetStatus, () -> realRemind());
            }
        }).start();
    }

    private void realRemind() {
        if (mRemindList.empty()) {
            Log.i(TAG, "realRemind: list is empty");
            return;
        }
        String remindText = mRemindList.peek().buildRemindText();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "realRemind: remindText:" + remindText);
    }
}
