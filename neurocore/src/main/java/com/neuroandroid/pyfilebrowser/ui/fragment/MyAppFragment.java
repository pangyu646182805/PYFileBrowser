package com.neuroandroid.pyfilebrowser.ui.fragment;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.base.BaseFragment;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class MyAppFragment extends BaseFragment {
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
}
