package com.example.myapplication.headset;

public abstract class RemindEvent {
    private String remindTime;
    private String remindMsg;


    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getRemindMsg() {
        return remindMsg;
    }

    public void setRemindMsg(String remindMsg) {
        this.remindMsg = remindMsg;
    }

    public abstract String buildRemindText();

    public String buildDelayRemindText(){
        return buildRemindText();
    }
}
