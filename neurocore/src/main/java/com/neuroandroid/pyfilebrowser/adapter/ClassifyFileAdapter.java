package com.neuroandroid.pyfilebrowser.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.base.SelectAdapter;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.glide.MediaGlideRequest;
import com.neuroandroid.pyfilebrowser.glide.MediaGlideTarget;
import com.neuroandroid.pyfilebrowser.ui.fragment.ClassifyFragment;
import com.neuroandroid.pyfilebrowser.utils.SystemUtils;
import com.neuroandroid.pyfilebrowser.utils.TimeUtils;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;
import com.neuroandroid.pyfilebrowser.widget.NoPaddingTextView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public class ClassifyFileAdapter extends SelectAdapter<PYFileBean, ClassifyFileAdapter.Holder> implements FastScrollRecyclerView.SectionedAdapter {
    private int mClassifyFlag = ClassifyFragment.CLASSIFY_AUDIO;

    public ClassifyFileAdapter(Context context, ArrayList<PYFileBean> dataList, int classifyFlag, int itemType) {
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
        PYFileBean pyFileBean = mDataList.get(position);
        holder.itemView.setActivated(pyFileBean.isSelected());
        switch (mClassifyFlag) {
            case ClassifyFragment.CLASSIFY_AUDIO:
                holder.onBindAudio(pyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_VIDEO:
                holder.onBindVideo(pyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_PHOTO:
                holder.onBindPhoto(pyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_DOC:
                holder.onBindDoc(pyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_APK:
                holder.onBindApk(pyFileBean);
                break;
            case ClassifyFragment.CLASSIFY_ZIP:
                holder.onBindZip(pyFileBean);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentType;
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
        @Nullable
        @BindView(R.id.iv_menu)
        ImageView mIvMenu;
        @Nullable
        @BindView(R.id.tv_desc)
        NoPaddingTextView mTvDesc;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBindAudio(PYFileBean pyFileBean) {
            // ImageLoader.getInstance().displayImageFromAlbumId(mContext, classifyFileBean.getAlbumId(), R.mipmap.ic_type_audio, mIvImg);
            loadImage(pyFileBean, R.mipmap.ic_type_audio);
            setText(pyFileBean);
        }

        public void onBindVideo(PYFileBean pyFileBean) {
            // ImageLoader.getInstance().displayImage(mContext, FileUtils.getVideoPath(mContext, classifyFileBean.getId()), R.mipmap.ic_type_video, mIvImg);
            loadImage(pyFileBean, R.mipmap.ic_type_video);
            setText(pyFileBean);
        }

        public void onBindPhoto(PYFileBean pyFileBean) {
            // ImageLoader.getInstance().displayImage(mContext, classifyFileBean.getPath(), R.mipmap.ic_type_photo, mIvImg);
            loadImage(pyFileBean, R.mipmap.ic_type_photo);
            setText(pyFileBean);
        }

        public void onBindDoc(PYFileBean pyFileBean) {
            mIvImg.setBackgroundResource(R.mipmap.ic_type_doc);
            setText(pyFileBean);
        }

        public void onBindApk(PYFileBean pyFileBean) {
            if (pyFileBean.getAppIcon() == null) {
                mIvImg.setBackgroundResource(R.mipmap.ic_type_apk);
            } else {
                mIvImg.setImageDrawable(pyFileBean.getAppIcon());
            }
            setText(pyFileBean);
        }

        public void onBindZip(PYFileBean pyFileBean) {
            mIvImg.setBackgroundResource(R.mipmap.ic_type_zip);
            setText(pyFileBean);
        }

        private void setText(PYFileBean pyFileBean) {
            mTvTitle.setText(pyFileBean.getTitle());
            mTvSubTitle.setText(TimeUtils.millis2String(pyFileBean.getDate(), "yyyy/MM/dd hh:mm"));
            if (mTvDesc != null)
                mTvDesc.setText(Formatter.formatFileSize(mContext, pyFileBean.getSize()));
        }

        private void loadImage(PYFileBean pyFileBean, int errorImg) {
            int width;
            if (mCurrentType == TYPE_LIST) {
                width = (int) UIUtils.getDimen(R.dimen.x88);
            } else {
                width = SystemUtils.getScreenWidth((Activity) mContext) / 2;
            }
            MediaGlideRequest.Builder.from(Glide.with(mContext), pyFileBean, errorImg)
                    .asBitmap().build(mContext, width).into(new MediaGlideTarget(mIvImg) {
                @Override
                public void onReady(int loadFlag) {
                    if (mCurrentType == TYPE_GRID) {
                        switch (loadFlag) {
                            case MediaGlideTarget.FLAG_START:
                                setIvImgLayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                                break;
                            case MediaGlideTarget.FLAG_SUCCUSS:
                                // setIvImgLayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                                int width = SystemUtils.getScreenWidth((Activity) mContext) / 2;
                                setIvImgLayoutParams(width, width);
                                break;
                            case MediaGlideTarget.FLAG_FAILED:
                                setIvImgLayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                                break;
                        }
                    }
                }
            });
        }

        private void setIvImgLayoutParams(int width, int height) {
            ViewGroup.LayoutParams params = mIvImg.getLayoutParams();
            params.height = height;
            params.width = width;
            mIvImg.setLayoutParams(params);
        }
    }
}
