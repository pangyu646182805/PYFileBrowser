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
import com.neuroandroid.pyfilebrowser.event.ClassifyEvent;
import com.neuroandroid.pyfilebrowser.loader.AudioLoader;
import com.neuroandroid.pyfilebrowser.loader.FileLoader;
import com.neuroandroid.pyfilebrowser.loader.PhotoLoader;
import com.neuroandroid.pyfilebrowser.loader.VideoLoader;
import com.neuroandroid.pyfilebrowser.loader.WrappedAsyncTaskLoader;
import com.neuroandroid.pyfilebrowser.provider.PYFileStore;
import com.neuroandroid.pyfilebrowser.utils.L;
import com.neuroandroid.pyfilebrowser.utils.SetUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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
    private int mCurrentClassifyFlag = CLASSIFY_AUDIO;

    @Override
    protected GridLayoutManager createLayoutManager() {
        return new GridLayoutManager(mContext, getGridSize());
    }

    @NonNull
    @Override
    protected ClassifyFileAdapter createAdapter(ArrayList<ClassifyFileBean> dataList) {
        return new ClassifyFileAdapter(mContext, dataList, mCurrentClassifyFlag, getItemType());
    }

    @Override
    public void toDest(int position, ClassifyBean classifyBean) {
        if (position <= 5) {
            mCurrentClassifyFlag = position;
            if (position >= 3) {
                new Thread(() -> {
                    ArrayList<ClassifyFileBean> classifyFileDataList = PYFileStore.getInstance(mContext).getClassifyFileDataList(mContext, mCurrentClassifyFlag);
                    mActivity.runOnUiThread(() -> invalidateAdapter(classifyFileDataList));
                }).start();
            }
            if (PYFileStore.getInstance(mContext).getClassifyFileDataListSize(mCurrentClassifyFlag) == 0)
                showLoading();
            showRecyclerView();
            EventBus.getDefault().post(new ClassifyEvent());
            getParentFragment().getLoaderManager().restartLoader(position, null, ClassifyFragment.this);
        }
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
        hideLoading();
        if (data != null) {
            invalidateAdapter(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ClassifyFileBean>> loader) {
        getAdapter().replaceAll(new ArrayList<>());
    }

    public void handleBackPress() {
        getAdapter().replaceAll(new ArrayList<>());
        hideRecyclerView();
        hideLoading();
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
                    ArrayList<ClassifyFileBean> classifyFileBeanDataList = fileLoader.getClassifyFileBeanDataList();

                    List<ClassifyFileBean> classifyFileDataList = PYFileStore.getInstance(getContext()).getClassifyFileDataList(getContext(), mClassifyFlag);
                    if (!SetUtils.equals(classifyFileDataList, classifyFileBeanDataList)) {
                        L.e("有文件被添加或者删除，去保存文件");
                        PYFileStore.getInstance(getContext()).delete(mClassifyFlag);
                        for (ClassifyFileBean classifyFileBean : classifyFileBeanDataList) {
                            PYFileStore.getInstance(getContext()).addItem(classifyFileBean);
                        }
                        return classifyFileBeanDataList;
                    } else {
                        L.e("不需要保存文件到数据库");
                        return null;
                    }
            }
        }
    }
}
