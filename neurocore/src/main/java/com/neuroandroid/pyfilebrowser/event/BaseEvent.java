package com.neuroandroid.pyfilebrowser.event;

/**
 * Created by NeuroAndroid on 2017/5/19.
 */

public class BaseEvent {
    public static final int EVENT_CLASSIFY = 200;
    public static final int EVENT_STORAGE = 300;
    public static final int EVENT_STORAGE_BACK = 400;
    private int eventFlag;

    public int getEventFlag() {
        return eventFlag;
    }

    public BaseEvent setEventFlag(int eventFlag) {
        this.eventFlag = eventFlag;
        return this;
    }
}
