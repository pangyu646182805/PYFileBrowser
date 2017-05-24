package com.neuroandroid.pyfilebrowser.base;

import android.support.v7.widget.RecyclerView;

import com.neuroandroid.pyfilebrowser.adapter.base.SelectAdapter;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public abstract class BaseRecyclerViewGridSizeFragment<ADAPTER extends SelectAdapter, LM extends RecyclerView.LayoutManager> extends BaseRecyclerViewFragment<ADAPTER, LM> {
    // RecyclerView列表布局
    protected static final int TYPE_LIST = 101;
    // RecyclerView网格布局
    protected static final int TYPE_GRID = 102;
}
