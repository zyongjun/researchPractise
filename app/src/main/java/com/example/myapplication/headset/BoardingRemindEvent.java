package com.example.myapplication.headset;

public class BoardingRemindEvent extends RemindEvent {
    @Override
    public String buildRemindText() {
        return "BoardingRemindEvent";
    }

    @Override
    public String buildDelayRemindText() {
        return "delay" + buildRemindText();
    }
}
