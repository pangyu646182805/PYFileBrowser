package com.neuroandroid.pyfilebrowser.event;

import com.neuroandroid.pyfilebrowser.bean.PYFileBean;

import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/26.
 */

public class StorageEvent extends BaseEvent {
    private ArrayList<PYFileBean> dataList;

    public StorageEvent() {
        setEventFlag(EVENT_STORAGE);
    }

    public ArrayList<PYFileBean> getDataList() {
        return dataList;
    }

    public StorageEvent setDataList(ArrayList<PYFileBean> dataList) {
        this.dataList = dataList;
        return this;
    }
}
