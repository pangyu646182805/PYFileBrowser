package com.neuroandroid.pyfilebrowser.ui.fragment;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

import com.neuroandroid.pyfilebrowser.adapter.StorageFileAdapter;
import com.neuroandroid.pyfilebrowser.base.BaseRecyclerViewFragment;
import com.neuroandroid.pyfilebrowser.bean.ClassifyBean;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.event.BaseEvent;
import com.neuroandroid.pyfilebrowser.event.StorageEvent;
import com.neuroandroid.pyfilebrowser.loader.PYFilePicker;
import com.neuroandroid.pyfilebrowser.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/23.
 */

public class StorageFragment extends BaseRecyclerViewFragment<StorageFileAdapter, GridLayoutManager> {
    // 当前的目录等级，0代表根目录
    private int mCurrentLevel;
    private File mParentFile;  // 当前显示文件夹的父目录

    public int getCurrentLevel() {
        return mCurrentLevel;
    }

    @Override
    protected GridLayoutManager createLayoutManager() {
        return new GridLayoutManager(mContext, 1);
    }

    @NonNull
    @Override
    protected StorageFileAdapter createAdapter(ArrayList dataList) {
        return new StorageFileAdapter(mContext, dataList);
    }

    @Override
    public void toDest(int position, ClassifyBean classifyBean) {
        mCurrentLevel++;
        showLoading();
        showRecyclerView();
        mParentFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        startScan(mParentFile);
        EventBus.getDefault().post(new StorageEvent());
    }

    @Override
    protected int getCurrentFragment() {
        return STORAGE_FRAGMENT;
    }

    private void startScan(File file) {
        new StorageAsyncTask().execute(file);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BaseEvent baseEvent) {
        if (baseEvent != null) {
            switch (baseEvent.getEventFlag()) {
                case BaseEvent.EVENT_STORAGE_BACK:
                    mCurrentLevel--;
                    if (mCurrentLevel == 0) {
                        // 显示手机内存item
                        hideRecyclerView();
                    } else {
                        mParentFile = mParentFile.getParentFile();
                        startScan(mParentFile);
                    }
                    break;
            }
        }
    }

    class StorageAsyncTask extends AsyncTask<File, Void, ArrayList<PYFileBean>> {
        @Override
        protected ArrayList<PYFileBean> doInBackground(File... files) {
            return PYFilePicker.startScan(mContext, files[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<PYFileBean> dataList) {
            super.onPostExecute(dataList);
            hideLoading();
            getAdapter().replaceAll(dataList);
            getAdapter().setItemClickListener((view, position, pyFileBean) -> {
                if (pyFileBean.getChildCount() == -1) {
                    // 点击的是文件
                    FileUtils.openSelectFile(mContext, pyFileBean.getFile());
                } else {
                    // 点击的是目录
                    mCurrentLevel++;
                    mParentFile = pyFileBean.getFile();
                    startScan(mParentFile);
                }
            });
        }
    }
}
