package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

import com.neuroandroid.pyfilebrowser.adapter.ClassifyFileAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseRecyclerViewGridSizeFragment;
import com.neuroandroid.pyfilebrowser.bean.ClassifyBean;
import com.neuroandroid.pyfilebrowser.loader.FileLoader;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class ClassifyFragment extends BaseRecyclerViewGridSizeFragment<ClassifyFileAdapter, GridLayoutManager> {
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
        new FileLoader(mContext, position).setCallBack(classifyFileBeanDataList -> {

        }).execute(FILE_FILTER[position]);
    }

    @Override
    protected int getCurrentFragment() {
        return CLASSIFY_FRAGMENT;
    }
}
