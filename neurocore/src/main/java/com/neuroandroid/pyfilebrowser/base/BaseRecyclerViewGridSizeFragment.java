package com.neuroandroid.pyfilebrowser.base;

import android.support.v7.widget.RecyclerView;

import com.neuroandroid.pyfilebrowser.adapter.base.SelectAdapter;

import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public abstract class BaseRecyclerViewGridSizeFragment<ADAPTER extends SelectAdapter, LM extends RecyclerView.LayoutManager> extends BaseRecyclerViewFragment<ADAPTER, LM> {
    /**
     * 网格大小
     */
    private int mGridSize = 2;

    public final int getGridSize() {
        if (mGridSize == 0) {
            mGridSize = 1;
        }
        return mGridSize;
    }

    /**
     * 设置网格大小
     */
    public void setGridSize(final int gridSize) {
        int oldItemType = getItemType();
        mGridSize = gridSize;
        // only recreate the adapter and layout manager if the layout currentLayoutRes has changed
        if (oldItemType != getItemType()) {
            invalidateLayoutManager();
            invalidateAdapter(new ArrayList<>());
            invalidateListener();
        } else {
            setGridSize(gridSize);
        }
    }

    protected int getItemType() {
        if (getGridSize() > 1) {
            return SelectAdapter.TYPE_GRID;
        }
        return SelectAdapter.TYPE_LIST;
    }

    /**
     * 重新设置监听器
     */
    protected void invalidateListener() {
    }
}
