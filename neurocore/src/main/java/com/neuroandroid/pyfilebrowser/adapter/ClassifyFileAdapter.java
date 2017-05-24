package com.neuroandroid.pyfilebrowser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.neuroandroid.pyfilebrowser.adapter.base.SelectAdapter;
import com.neuroandroid.pyfilebrowser.bean.ClassifyFileBean;

import java.util.List;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public class ClassifyFileAdapter extends SelectAdapter<ClassifyFileBean, RecyclerView.ViewHolder> {
    public ClassifyFileAdapter(Context context, List<ClassifyFileBean> dataList) {
        super(context, dataList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
}
