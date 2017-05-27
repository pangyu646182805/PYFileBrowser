package com.neuroandroid.pyfilebrowser.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.base.SelectAdapter;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.loader.PYFilePicker;
import com.neuroandroid.pyfilebrowser.utils.ImageLoader;
import com.neuroandroid.pyfilebrowser.utils.TimeUtils;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;
import com.neuroandroid.pyfilebrowser.widget.NoPaddingTextView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NeuroAndroid on 2017/5/26.
 */

public class StorageFileAdapter extends SelectAdapter<PYFileBean, StorageFileAdapter.Holder> implements FastScrollRecyclerView.SectionedAdapter {
    public StorageFileAdapter(Context context, ArrayList<PYFileBean> dataList) {
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

    @NonNull
    @Override
    public String getSectionName(int position) {
        String title = mDataList.get(position).getTitle();
        if (UIUtils.isEmpty(title)) return "";
        return title.substring(0, 1);
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
            setImageByType(pyFileBean.getFileType(), pyFileBean);
            setText(pyFileBean);
        }

        private void setText(PYFileBean pyFileBean) {
            mTvTitle.setText(pyFileBean.getTitle());
            mTvSubTitle.setText(TimeUtils.millis2String(pyFileBean.getDate(), "yyyy/MM/dd hh:mm"));
            int childCount = pyFileBean.getChildCount();
            if (childCount == -1) {
                mTvDesc.setText(Formatter.formatFileSize(mContext, pyFileBean.getSize()));
            } else {
                if (childCount > 0) {
                    mTvDesc.setText(childCount + "文件");
                } else {
                    mTvDesc.setText("空目录");
                }
            }
        }

        private int getChildCount(File file) {
            String[] childList = file.list();
            if (childList != null) {
                return childList.length;
            } else {
                return -1;
            }
        }

        private void setImageByType(int type, PYFileBean pyFileBean) {
            if (type == PYFilePicker.TYPE_VIDEO) {
                if (pyFileBean.getThumbnail() != null)
                    mIvImg.setImageBitmap(pyFileBean.getThumbnail());
                return;
            } else if (type == PYFilePicker.TYPE_IMAGE) {
                ImageLoader.getInstance().displayImage(mContext, pyFileBean.getPath(), R.mipmap.ic_type_photo, mIvImg);
                return;
            } else if (type == PYFilePicker.TYPE_APK) {
                mIvImg.setImageDrawable(pyFileBean.getAppIcon());
                return;
            }
            int resId = R.mipmap.ic_type_folder;
            switch (type) {
                case PYFilePicker.TYPE_TXT:
                    resId = R.mipmap.ic_type_txt;
                    break;
                case PYFilePicker.TYPE_HTM:
                    resId = R.mipmap.ic_type_htm;
                    break;
                case PYFilePicker.TYPE_DOC:
                    resId = R.mipmap.ic_type_doc;
                    break;
                case PYFilePicker.TYPE_ZIP:
                    resId = R.mipmap.ic_type_zip;
                    break;
                case PYFilePicker.TYPE_TAR:
                    resId = R.mipmap.ic_type_tar;
                    break;
                case PYFilePicker.TYPE_RAR:
                    resId = R.mipmap.ic_type_rar;
                    break;
                case PYFilePicker.TYPE_UNKNOW:
                    resId = R.mipmap.ic_type_unknown;
                    break;
                case PYFilePicker.TYPE_AUDIO:
                    resId = R.mipmap.ic_type_audio;
                    break;
            }
            mIvImg.setImageResource(resId);
        }
    }
}
