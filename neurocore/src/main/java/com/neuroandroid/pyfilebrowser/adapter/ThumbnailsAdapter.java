package com.neuroandroid.pyfilebrowser.adapter;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.base.SelectAdapter;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.glide.MediaGlideRequest;
import com.neuroandroid.pyfilebrowser.glide.MediaGlideTarget;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by NeuroAndroid on 2017/5/31.
 */

public class ThumbnailsAdapter extends SelectAdapter<PYFileBean, ThumbnailsAdapter.Holder> {
    public ThumbnailsAdapter(Context context, ArrayList<PYFileBean> dataList) {
        super(context, dataList);
    }

    @Override
    public Holder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_thumbnails, parent, false));
    }

    @Override
    public void onBindItemViewHolder(Holder holder, int position) {
        holder.onBind(mDataList.get(position));
    }

    public void update(int position, boolean selected) {
        mDataList.get(position).setSelected(selected);
        // notifyItemChanged(position);
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final ImageView mIvImg;

        public Holder(View itemView) {
            super(itemView);
            mIvImg = ButterKnife.findById(itemView, R.id.iv_img);
        }

        public void onBind(PYFileBean pyFileBean) {
            ColorMatrix matrix = new ColorMatrix();
            if (pyFileBean.isSelected()) {
                matrix.setSaturation(1);

                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                mIvImg.setColorFilter(filter);
                itemView.setAlpha(1.0f);
            } else {
                matrix.setSaturation(0);

                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                mIvImg.setColorFilter(filter);
                itemView.setAlpha(0.5f);
            }
            loadImage(pyFileBean);
        }

        private void loadImage(PYFileBean pyFileBean) {
            int width = (int) UIUtils.getDimen(R.dimen.x180);
            MediaGlideRequest.Builder.from(Glide.with(mContext), pyFileBean, R.mipmap.ic_type_photo)
                    .asBitmap().build(mContext, width).into(new MediaGlideTarget(mIvImg) {
                @Override
                public void onReady(int loadFlag) {
                }
            });
        }
    }
}
