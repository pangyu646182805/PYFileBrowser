package com.neuroandroid.pyfilebrowser.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;

/**
 * Created by NeuroAndroid on 2017/5/26.
 */

public abstract class MediaGlideTarget extends ImageViewTarget<Bitmap> {
    public static final int FLAG_START = 0;
    public static final int FLAG_SUCCUSS = 1;
    public static final int FLAG_FAILED = 2;

    public MediaGlideTarget(ImageView view) {
        super(view);
    }

    @Override
    protected void setResource(Bitmap resource) {
        view.setImageBitmap(resource);
    }

    @Override
    public void onStart() {
        super.onStart();
        onReady(FLAG_START);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        onReady(FLAG_FAILED);
    }

    @Override
    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
        onReady(FLAG_SUCCUSS);
    }

    public abstract void onReady(int loadFlag);
}
