package com.neuroandroid.pyfilebrowser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.base.BaseRvAdapter;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.glide.MediaGlideRequest;
import com.neuroandroid.pyfilebrowser.glide.MediaGlideTarget;
import com.neuroandroid.pyfilebrowser.widget.photo.PhotoView;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by NeuroAndroid on 2017/5/31.
 */

public class PhotoGalleryAdapter extends BaseRvAdapter<PYFileBean, PhotoGalleryAdapter.Holder> {
    public PhotoGalleryAdapter(Context context, List<PYFileBean> dataList) {
        super(context, dataList);
    }

    @Override
    public Holder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_photo_gallery, parent, false));
    }

    @Override
    public void onBindItemViewHolder(Holder holder, int position) {
        holder.onBind(mDataList.get(position));
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final PhotoView mIvImg;

        public Holder(View itemView) {
            super(itemView);
            mIvImg = ButterKnife.findById(itemView, R.id.iv_img);
            mIvImg.setMaxScale(8f);
            mIvImg.setMinScale(0.8f);
            mIvImg.setOnPhotoTapListener((view, x, y) -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, getLayoutPosition(), getItem(getLayoutPosition()));
                }
            });
        }

        public void onBind(PYFileBean pyFileBean) {
            loadImage(pyFileBean);
        }

        private void loadImage(PYFileBean pyFileBean) {
            MediaGlideRequest.Builder.from(Glide.with(mContext), pyFileBean, R.mipmap.ic_type_photo)
                    .asBitmap().build(mContext, -1).into(new MediaGlideTarget(mIvImg) {
                @Override
                public void onReady(int loadFlag) {
                }
            });
        }
    }
}
