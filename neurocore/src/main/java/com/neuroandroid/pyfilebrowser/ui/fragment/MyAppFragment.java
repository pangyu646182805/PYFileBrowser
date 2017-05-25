package com.neuroandroid.pyfilebrowser.ui.fragment;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.base.BaseFragment;
import com.neuroandroid.pyfilebrowser.ui.activity.MainActivity;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class MyAppFragment extends BaseFragment implements MainActivity.MainActivityFragmentCallbacks {
    public static MyAppFragment newInstance() {
        return new MyAppFragment();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_my_app;
    }

    @Override
    protected void initView() {

    }

    @Override
    public boolean handleBackPress() {
        return false;
    }
}
