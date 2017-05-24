package com.neuroandroid.pyfilebrowser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.base.BaseRvAdapter;
import com.neuroandroid.pyfilebrowser.bean.ClassifyBean;
import com.neuroandroid.pyfilebrowser.widget.NoPaddingTextView;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public class ClassifyAdapter extends BaseRvAdapter<ClassifyBean, ClassifyAdapter.Holder> {
    private int mTotalHeight;

    public void setTotalHeight(int totalHeight) {
        mTotalHeight = totalHeight;
        notifyDataSetChanged();
    }

    public ClassifyAdapter(Context context, List<ClassifyBean> dataList) {
        super(context, dataList);
    }

    @Override
    public Holder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_classify, parent, false));
    }

    @Override
    public void onBindItemViewHolder(Holder holder, int position) {
        holder.onBind(mDataList.get(position));
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView mIvImg;
        private NoPaddingTextView mTvTitle;

        public Holder(View itemView) {
            super(itemView);
            mIvImg = ButterKnife.findById(itemView, R.id.iv_img);
            mTvTitle = ButterKnife.findById(itemView, R.id.tv_title);
        }

        public void onBind(ClassifyBean classifyBean) {
            mIvImg.setBackgroundResource(classifyBean.getClassifyImg());
            mTvTitle.setText(classifyBean.getClassifyTitle());
            itemView.getLayoutParams().height = mTotalHeight / 3;
        }
    }
}
