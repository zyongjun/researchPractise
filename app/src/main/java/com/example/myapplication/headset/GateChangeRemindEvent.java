package com.example.myapplication.headset;

public class GateChangeRemindEvent extends RemindEvent {
    @Override
    public String buildRemindText() {
        return "GateChangeRemindEvent";
    }

    @Override
    public String buildDelayRemindText() {
        return "delay" + buildRemindText();
    }
}
