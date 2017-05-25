package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;

import com.neuroandroid.pyfilebrowser.adapter.ClassifyFileAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseRecyclerViewGridSizeFragment;
import com.neuroandroid.pyfilebrowser.bean.ClassifyBean;
import com.neuroandroid.pyfilebrowser.bean.ClassifyFileBean;
import com.neuroandroid.pyfilebrowser.loader.AudioLoader;
import com.neuroandroid.pyfilebrowser.loader.FileLoader;
import com.neuroandroid.pyfilebrowser.loader.PhotoLoader;
import com.neuroandroid.pyfilebrowser.loader.VideoLoader;
import com.neuroandroid.pyfilebrowser.loader.WrappedAsyncTaskLoader;
import com.neuroandroid.pyfilebrowser.utils.L;

import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class ClassifyFragment extends BaseRecyclerViewGridSizeFragment<ClassifyFileAdapter, GridLayoutManager> implements LoaderManager.LoaderCallbacks<ArrayList<ClassifyFileBean>> {
    private static final String[][] FILE_FILTER = {{"mp3"}, {"mp4"}, {"jpg", "png"},
            {"txt", "html", "doc"}, {"apk"}, {"zip", "tar", "rar"}};
    public static final int CLASSIFY_AUDIO = 0;
    public static final int CLASSIFY_VIDEO = 1;
    public static final int CLASSIFY_PHOTO = 2;
    public static final int CLASSIFY_DOC = 3;
    public static final int CLASSIFY_APK = 4;
    public static final int CLASSIFY_ZIP = 5;
    public static final int CLASSIFY_DOWNLOAD = 6;
    public static final int CLASSIFY_COLLECTION = 7;

    @Override
    protected GridLayoutManager createLayoutManager() {
        return null;
    }

    @NonNull
    @Override
    protected ClassifyFileAdapter createAdapter() {
        return null;
    }

    @Override
    public void toDest(int position, ClassifyBean classifyBean) {
        if (position <= 5)
            getParentFragment().getLoaderManager().initLoader(position, null, ClassifyFragment.this);
        /*new FileLoader(mContext, position).setCallBack(classifyFileBeanDataList -> {
        }).execute(FILE_FILTER[position]);*/
    }

    @Override
    protected int getCurrentFragment() {
        return CLASSIFY_FRAGMENT;
    }

    @Override
    public Loader<ArrayList<ClassifyFileBean>> onCreateLoader(int id, Bundle args) {
        return new AsyncFileLoader(mContext, id);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ClassifyFileBean>> loader, ArrayList<ClassifyFileBean> data) {
        L.e("size : " + data.size());
        for (ClassifyFileBean bean : data) {
            L.e(bean.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ClassifyFileBean>> loader) {

    }

    private static class AsyncFileLoader extends WrappedAsyncTaskLoader<ArrayList<ClassifyFileBean>> {
        private int mClassifyFlag;

        public AsyncFileLoader(Context context, int classifyFlag) {
            super(context);
            this.mClassifyFlag = classifyFlag;
        }

        @Override
        public ArrayList<ClassifyFileBean> loadInBackground() {
            switch (mClassifyFlag) {
                case CLASSIFY_AUDIO:
                    return AudioLoader.getAllAudios(getContext());
                case CLASSIFY_VIDEO:
                    return VideoLoader.getAllVideos(getContext());
                case CLASSIFY_PHOTO:
                    return PhotoLoader.getAllPhotos(getContext());
                case CLASSIFY_DOC:
                case CLASSIFY_APK:
                case CLASSIFY_ZIP:
                default:
                    FileLoader fileLoader = new FileLoader(getContext(), mClassifyFlag);
                    fileLoader.loadFile(FILE_FILTER[mClassifyFlag]);
                    return fileLoader.getClassifyFileBeanDataList();
            }
        }
    }
}
