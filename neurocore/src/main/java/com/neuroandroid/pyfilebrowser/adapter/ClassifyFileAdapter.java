package com.neuroandroid.pyfilebrowser.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.base.SelectAdapter;
import com.neuroandroid.pyfilebrowser.bean.ClassifyFileBean;
import com.neuroandroid.pyfilebrowser.ui.fragment.ClassifyFragment;
import com.neuroandroid.pyfilebrowser.utils.FileUtils;
import com.neuroandroid.pyfilebrowser.utils.ImageLoader;
import com.neuroandroid.pyfilebrowser.utils.TimeUtils;
import com.neuroandroid.pyfilebrowser.widget.NoPaddingTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public class ClassifyFileAdapter extends SelectAdapter<ClassifyFileBean, ClassifyFileAdapter.Holder> {
    private int mClassifyFlag = ClassifyFragment.CLASSIFY_AUDIO;

    public ClassifyFileAdapter(Context context, List<ClassifyFileBean> dataList, int classifyFlag, int itemType) {
        super(context, dataList);
        this.mClassifyFlag = classifyFlag;
        mCurrentType = itemType;
    }

    @Override
    public Holder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        Holder holder = null;
        switch (viewType) {
            case TYPE_LIST:
                holder = new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false));
                break;
            case TYPE_GRID:
                holder = new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_grid, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindItemViewHolder(Holder holder, int position) {
        ClassifyFileBean classifyFileBean = mDataList.get(position);
        switch (mClassifyFlag) {
            case ClassifyFragment.CLASSIFY_AUDIO:
                holder.onBindAudio(classifyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_VIDEO:
                holder.onBindVideo(classifyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_PHOTO:
                holder.onBindPhoto(classifyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_DOC:
                holder.onBindDoc(classifyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_APK:
                holder.onBindApk(classifyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_ZIP:
                holder.onBindZip(classifyFileBean);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentType;
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        ImageView mIvImg;
        @BindView(R.id.tv_title)
        NoPaddingTextView mTvTitle;
        @BindView(R.id.tv_sub_title)
        NoPaddingTextView mTvSubTitle;
        @Nullable
        @BindView(R.id.iv_menu)
        ImageView mIvMenu;
        @Nullable
        @BindView(R.id.tv_size)
        NoPaddingTextView mTvSize;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBindAudio(ClassifyFileBean classifyFileBean) {
            ImageLoader.getInstance().displayImageFromAlbumId(mContext, classifyFileBean.getAlbumId(), R.mipmap.ic_type_audio, mIvImg);
            setText(classifyFileBean);
        }

        public void onBindVideo(ClassifyFileBean classifyFileBean) {
            ImageLoader.getInstance().displayImage(mContext, FileUtils.getVideoPath(mContext, classifyFileBean.getId()), R.mipmap.ic_type_video, mIvImg);
            setText(classifyFileBean);
        }

        public void onBindPhoto(ClassifyFileBean classifyFileBean) {
            ImageLoader.getInstance().displayImage(mContext, classifyFileBean.getPath(), R.mipmap.ic_type_photo, mIvImg);
            setText(classifyFileBean);
        }

        public void onBindDoc(ClassifyFileBean classifyFileBean) {
            mIvImg.setBackgroundResource(R.mipmap.ic_type_doc);
            setText(classifyFileBean);
        }

        public void onBindApk(ClassifyFileBean classifyFileBean) {
            if (classifyFileBean.getAppIcon() == null) {
                mIvImg.setBackgroundResource(R.mipmap.ic_type_apk);
            } else {
                mIvImg.setImageDrawable(classifyFileBean.getAppIcon());
            }
            setText(classifyFileBean);
        }

        public void onBindZip(ClassifyFileBean classifyFileBean) {
            mIvImg.setBackgroundResource(R.mipmap.ic_type_zip);
            setText(classifyFileBean);
        }

        private void setText(ClassifyFileBean classifyFileBean) {
            mTvTitle.setText(classifyFileBean.getTitle());
            mTvSubTitle.setText(TimeUtils.millis2String(classifyFileBean.getDate(), "yyyy/MM/dd hh:mm"));
            if (mTvSize != null) mTvSize.setText(Formatter.formatFileSize(mContext, classifyFileBean.getSize()));
        }
    }
}
