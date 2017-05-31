package com.neuroandroid.pyfilebrowser.glide;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.ui.fragment.ClassifyFragment;
import com.neuroandroid.pyfilebrowser.utils.FileUtils;

/**
 * Created by NeuroAndroid on 2017/5/26.
 */

public class MediaGlideRequest {
    public static final DiskCacheStrategy DEFAULT_DISK_CACHE_STRATEGY = DiskCacheStrategy.NONE;
    public static final int DEFAULT_ANIMATION = android.R.anim.fade_in;

    public static class Builder {
        final RequestManager requestManager;
        final PYFileBean mClassifyFileBean;
        final int mErrorImage;

        public static Builder from(@NonNull RequestManager requestManager, PYFileBean classifyFileBean, int errorImage) {
            return new Builder(requestManager, classifyFileBean, errorImage);
        }

        private Builder(@NonNull RequestManager requestManager, PYFileBean classifyFileBean, int errorImage) {
            this.requestManager = requestManager;
            this.mClassifyFileBean = classifyFileBean;
            this.mErrorImage = errorImage;
        }

        public BitmapBuilder asBitmap() {
            return new BitmapBuilder(this, mErrorImage);
        }

        public DrawableRequestBuilder<GlideDrawable> build(Context context, int width) {
            //noinspection unchecked
            return createBaseRequest(context, requestManager, mClassifyFileBean)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(mErrorImage)
                    .placeholder(mErrorImage)
                    .override(width, width)
                    .animate(DEFAULT_ANIMATION)
                    .signature(createSignature(mClassifyFileBean));
        }
    }

    public static class BitmapBuilder {
        private final Builder builder;
        final int mErrorImage;

        public BitmapBuilder(Builder builder, int errorImage) {
            this.builder = builder;
            this.mErrorImage = errorImage;
        }

        public BitmapRequestBuilder<?, Bitmap> build(Context context, int width) {
            //noinspection unchecked
            if (width == -1) {
                return createBaseRequest(context, builder.requestManager, builder.mClassifyFileBean)
                        .asBitmap()
                        .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                        .error(mErrorImage)
                        .placeholder(mErrorImage)
                        .animate(DEFAULT_ANIMATION)
                        .signature(createSignature(builder.mClassifyFileBean));
            }
            return createBaseRequest(context, builder.requestManager, builder.mClassifyFileBean)
                    .asBitmap()
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(mErrorImage)
                    .placeholder(mErrorImage)
                    .override(width, width)
                    .animate(DEFAULT_ANIMATION)
                    .signature(createSignature(builder.mClassifyFileBean));
        }
    }

    public static DrawableTypeRequest createBaseRequest(Context context, RequestManager requestManager,
                                                        PYFileBean pyFileBean) {
        switch (pyFileBean.getClassifyFlag()) {
            case ClassifyFragment.CLASSIFY_AUDIO:
                return requestManager.load(getMediaStoreAlbumCoverUri(pyFileBean.getAlbumId()));
            case ClassifyFragment.CLASSIFY_VIDEO:
                return requestManager.load(FileUtils.getVideoThumbnail(context, pyFileBean.getId()));
            case ClassifyFragment.CLASSIFY_PHOTO:
            default:
                // return requestManager.load(FileUtils.getPhotoThumbnail(context, classifyFileBean.getId()));
                return requestManager.load(pyFileBean.getFile());
        }
    }

    private static Uri getMediaStoreAlbumCoverUri(int albumId) {
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public static Key createSignature(PYFileBean classifyFileBean) {
        return new MediaStoreSignature(classifyFileBean.getTitle(), classifyFileBean.getDate(), 0);
    }
}
