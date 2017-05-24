package com.neuroandroid.pyfilebrowser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.base.BaseRvAdapter;

import java.util.List;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public class StorageAdapter extends BaseRvAdapter<String, StorageAdapter.Holder> {
    public StorageAdapter(Context context, List<String> dataList) {
        super(context, dataList);
    }

    @Override
    public StorageAdapter.Holder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_storage, parent, false));
    }

    @Override
    public void onBindItemViewHolder(StorageAdapter.Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }
}
