package com.neuroandroid.pyfilebrowser.event;

import com.neuroandroid.pyfilebrowser.bean.ISelect;

import java.util.List;

/**
 * Created by NeuroAndroid on 2017/5/11.
 * 专门处理多选事件
 */
public class SelectedEvent<BEAN extends ISelect> extends BaseEvent {
    private List<BEAN> mSelectedBeans;

    public SelectedEvent() {
        setEventFlag(EVENT_SELECTED_MODE);
    }

    public List<BEAN> getSelectedBeans() {
        return mSelectedBeans;
    }

    public SelectedEvent setSelectedBeans(List<BEAN> selectedBeans) {
        mSelectedBeans = selectedBeans;
        return this;
    }
}
