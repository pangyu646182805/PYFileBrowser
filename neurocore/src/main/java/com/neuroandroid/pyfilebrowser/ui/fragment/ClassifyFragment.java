package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.neuroandroid.pyfilebrowser.adapter.ClassifyFileAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseRecyclerViewGridSizeFragment;
import com.neuroandroid.pyfilebrowser.bean.ClassifyBean;
import com.neuroandroid.pyfilebrowser.bean.ISelect;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.event.ClassifyEvent;
import com.neuroandroid.pyfilebrowser.event.SelectedEvent;
import com.neuroandroid.pyfilebrowser.loader.AudioLoader;
import com.neuroandroid.pyfilebrowser.loader.FileLoader;
import com.neuroandroid.pyfilebrowser.loader.PhotoLoader;
import com.neuroandroid.pyfilebrowser.loader.VideoLoader;
import com.neuroandroid.pyfilebrowser.loader.WrappedAsyncTaskLoader;
import com.neuroandroid.pyfilebrowser.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class ClassifyFragment extends BaseRecyclerViewGridSizeFragment<ClassifyFileAdapter, GridLayoutManager> implements LoaderManager.LoaderCallbacks<ArrayList<PYFileBean>> {
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
    public static final int CLASSIFY_MY_APP = 8;
    private int mCurrentClassifyFlag = CLASSIFY_AUDIO;
    private ArrayList<PYFileBean> mSelectedDataList = new ArrayList<>();

    @Override
    protected void initData() {
    }

    public ArrayList<PYFileBean> getSelectedDataList() {
        return mSelectedDataList;
    }

    @Override
    protected GridLayoutManager createLayoutManager() {
        return new GridLayoutManager(mContext, getGridSize());
    }

    @NonNull
    @Override
    protected ClassifyFileAdapter createAdapter(ArrayList<PYFileBean> dataList) {
        return new ClassifyFileAdapter(mContext, dataList, mCurrentClassifyFlag, getItemType());
    }

    @Override
    public void toDest(int position, ClassifyBean classifyBean) {
        if (position <= 5) {
            mCurrentClassifyFlag = position;
            /*if (position >= 3) {
                new Thread(() -> {
                    ArrayList<PYFileBean> classifyFileDataList = PYFileStore.getInstance(mContext).getClassifyFileDataList(mContext, mCurrentClassifyFlag);
                    mActivity.runOnUiThread(() -> invalidateAdapter(classifyFileDataList));
                }).start();
            }
            if (PYFileStore.getInstance(mContext).getClassifyFileDataListSize(mCurrentClassifyFlag) == 0)
                showLoading();*/
            showLoading();
            showRecyclerView();
            EventBus.getDefault().post(new ClassifyEvent());
            getParentFragment().getLoaderManager().initLoader(position, null, ClassifyFragment.this);
        }
    }

    public void restartLoader() {
        getParentFragment().getLoaderManager().restartLoader(mCurrentClassifyFlag, null, ClassifyFragment.this);
    }

    @Override
    protected int getCurrentFragment() {
        return CLASSIFY_FRAGMENT;
    }

    @Override
    public Loader<ArrayList<PYFileBean>> onCreateLoader(int id, Bundle args) {
        return new AsyncFileLoader(mContext, id);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<PYFileBean>> loader, ArrayList<PYFileBean> data) {
        hideLoading();
        if (data != null) {
            invalidateAdapter(data);
            invalidateListener();
        }
    }

    @Override
    protected void invalidateListener() {
        getAdapter().setItemClickListener((view, position, pyFileBean) -> {
            if (mCurrentClassifyFlag == CLASSIFY_PHOTO) {
                MyFileFragment myFileFragment = (MyFileFragment) getParentFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("photo_gallery", getAdapter().getDataList());
                bundle.putInt("current_position", position);
                myFileFragment.replaceFragment(bundle);
            } else {
                FileUtils.openSelectFile(mContext, pyFileBean.getFile());
            }
        });
        getAdapter().setItemLongClickListener((view, position, song) -> {
            if (!getAdapter().isSelectMode()) {
                getAdapter().updateSelectMode(true, position);
            }
        });
        getAdapter().setItemSelectedListener(new ISelect.OnItemSelectedListener<PYFileBean>() {
            @Override
            public void onItemSelected(View view, int position, boolean isSelected, PYFileBean pyFileBean) {
                if (isSelected) {
                    mSelectedDataList.add(pyFileBean);
                } else {
                    mSelectedDataList.remove(pyFileBean);
                }
                EventBus.getDefault().post(new SelectedEvent<PYFileBean>().setSelectedBeans(mSelectedDataList));
            }

            @Override
            public void onNothingSelected() {
                // 一个条目都没有被选择则取消多选模式
                getAdapter().updateSelectMode(false);
            }
        });
    }

    @Override
    public void clearSelected() {
        super.clearSelected();
        // 清除被选中的集合
        if (mSelectedDataList.size() > 0) mSelectedDataList.clear();
    }

    public void selectAll() {
        super.selectAll();
        mSelectedDataList = new ArrayList<>(getAdapter().getDataList());
        EventBus.getDefault().post(new SelectedEvent<PYFileBean>().setSelectedBeans(mSelectedDataList));
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<PYFileBean>> loader) {
        getAdapter().replaceAll(new ArrayList<>());
    }

    public void handleBackPress() {
        getAdapter().replaceAll(new ArrayList<>());
        hideRecyclerView();
        hideLoading();
    }

    @Override
    protected String getEmptyMessage() {
        switch (mCurrentClassifyFlag) {
            case CLASSIFY_AUDIO:
                return "没有音频";
            case CLASSIFY_VIDEO:
                return "没有视频";
            case CLASSIFY_PHOTO:
                return "没有照片";
            case CLASSIFY_DOC:
                return "没有文档";
            case CLASSIFY_APK:
                return "没有安装包";
            case CLASSIFY_ZIP:
                return "没有压缩包";
            default:
                return super.getEmptyMessage();
        }
    }

    private static class AsyncFileLoader extends WrappedAsyncTaskLoader<ArrayList<PYFileBean>> {
        private int mClassifyFlag;

        public AsyncFileLoader(Context context, int classifyFlag) {
            super(context);
            this.mClassifyFlag = classifyFlag;
        }

        @Override
        public ArrayList<PYFileBean> loadInBackground() {
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
                    /*FileLoader fileLoader = new FileLoader(getContext(), mClassifyFlag);
                    fileLoader.loadFile(FILE_FILTER[mClassifyFlag]);
                    ArrayList<PYFileBean> classifyFileBeanDataList = fileLoader.getClassifyFileBeanDataList();*/

                    /*List<PYFileBean> classifyFileDataList = PYFileStore.getInstance(getContext()).getClassifyFileDataList(getContext(), mClassifyFlag);
                    if (!SetUtils.equals(classifyFileDataList, classifyFileBeanDataList)) {
                        L.e("有文件被添加或者删除，去保存文件");
                        PYFileStore.getInstance(getContext()).delete(mClassifyFlag);
                        for (PYFileBean classifyFileBean : classifyFileBeanDataList) {
                            PYFileStore.getInstance(getContext()).addItem(classifyFileBean);
                        }
                        return classifyFileBeanDataList;
                    } else {
                        L.e("不需要保存文件到数据库");
                        return null;
                    }*/
                    return FileLoader.getAllFilesByExtensionName(getContext(), FILE_FILTER[mClassifyFlag], mClassifyFlag);
            }
        }
    }
}
