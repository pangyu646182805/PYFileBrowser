package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.neuroandroid.pyfilebrowser.adapter.base.SelectAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseRecyclerViewFragment;
import com.neuroandroid.pyfilebrowser.bean.ClassifyBean;

import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class StorageFragment extends BaseRecyclerViewFragment {
    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return null;
    }

    @NonNull
    @Override
    protected SelectAdapter createAdapter(ArrayList dataList) {
        return null;
    }

    @Override
    public void toDest(int position, ClassifyBean classifyBean) {

    }

    @Override
    protected int getCurrentFragment() {
        return STORAGE_FRAGMENT;
    }
}
