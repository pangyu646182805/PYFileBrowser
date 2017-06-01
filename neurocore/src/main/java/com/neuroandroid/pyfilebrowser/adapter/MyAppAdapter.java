package com.neuroandroid.pyfilebrowser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.base.BaseRvAdapter;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.utils.TimeUtils;
import com.neuroandroid.pyfilebrowser.widget.NoPaddingTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NeuroAndroid on 2017/6/1.
 */

public class MyAppAdapter extends BaseRvAdapter<PYFileBean, MyAppAdapter.Holder> {
    public MyAppAdapter(Context context, List<PYFileBean> dataList) {
        super(context, dataList);
    }

    @Override
    public Holder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindItemViewHolder(Holder holder, int position) {
        holder.onBind(mDataList.get(position));
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        ImageView mIvImg;
        @BindView(R.id.tv_title)
        NoPaddingTextView mTvTitle;
        @BindView(R.id.tv_sub_title)
        NoPaddingTextView mTvSubTitle;
        @BindView(R.id.iv_menu)
        ImageView mIvMenu;
        @BindView(R.id.tv_desc)
        NoPaddingTextView mTvDesc;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(PYFileBean pyFileBean) {
            if (pyFileBean.getAppIcon() == null) {
                mIvImg.setBackgroundResource(R.mipmap.ic_type_apk);
            } else {
                mIvImg.setImageDrawable(pyFileBean.getAppIcon());
            }
            setText(pyFileBean);
        }

        private void setText(PYFileBean pyFileBean) {
            mTvTitle.setText(pyFileBean.getTitle());
            mTvSubTitle.setText(TimeUtils.millis2String(pyFileBean.getDate(), "yyyy/MM/dd hh:mm"));
            if (mTvDesc != null)
                mTvDesc.setText(Formatter.formatFileSize(mContext, pyFileBean.getSize()));
        }
    }
}
