package com.surampaksakosoy.btarduino.models;

public class logModel {
    private String event;
    private String time;

    public logModel(String event, String time) {
        this.event = event;
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public String getTime() {
        return time;
    }
}
