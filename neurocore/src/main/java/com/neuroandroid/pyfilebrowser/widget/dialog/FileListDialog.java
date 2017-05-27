package com.neuroandroid.pyfilebrowser.widget.dialog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;

import com.neuroandroid.pyfilebrowser.R;
import com.neuroandroid.pyfilebrowser.adapter.StorageFileAdapter;
import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.loader.PYFilePicker;
import com.neuroandroid.pyfilebrowser.utils.DialogUtils;
import com.neuroandroid.pyfilebrowser.utils.ShowUtils;
import com.neuroandroid.pyfilebrowser.utils.UIUtils;
import com.neuroandroid.pyfilebrowser.widget.LoadingLayout;
import com.neuroandroid.pyfilebrowser.widget.NoPaddingTextView;
import com.neuroandroid.pyfilebrowser.widget.dialog.base.BaseDialog;
import com.neuroandroid.pyfilebrowser.widget.dialog.base.WindowConfig;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by NeuroAndroid on 2017/5/27.
 */

public class FileListDialog extends BaseDialog<FileListDialog> {
    @BindView(R.id.tv_cancel)
    NoPaddingTextView mTvCancel;
    @BindView(R.id.tv_complete)
    NoPaddingTextView mTvComplete;
    @BindView(R.id.rv_file_list)
    FastScrollRecyclerView mRvFileList;
    @BindView(R.id.loading_layout)
    LoadingLayout mLoadingLayout;
    private StorageFileAdapter mStorageFileAdapter;
    // 当前的目录等级，0代表根目录
    private int mCurrentLevel = 1;
    // 当前显示文件夹的父目录
    private File mParentFile;

    public FileListDialog(@NonNull Context context, WindowConfig config) {
        super(context, DialogUtils.getCustomConfig(true, true, DEFAULT_DIM_AMOUNT, Gravity.BOTTOM, true, R.style.AnimationsGrowFromBottom,
                WindowConfig.LAYOUT_PARM_MATCH_PARENT, (int) UIUtils.getDimen(R.dimen.y620)));
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.dialog_file_list;
    }

    @Override
    protected void initView() {
        mRvFileList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvFileList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .colorResId(R.color.split).sizeResId(R.dimen.y2).build());
        mStorageFileAdapter = new StorageFileAdapter(mContext, new ArrayList<>());
        mStorageFileAdapter.updateSelectMode(false);
        mRvFileList.setAdapter(mStorageFileAdapter);
        mLoadingLayout.setStatus(LoadingLayout.STATUS_LOADING);

        mParentFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        startScan(mParentFile);
        mTvCancel.setOnClickListener(view -> dismiss());
    }

    private void startScan(File file) {
        new StorageAsyncTask().execute(file);
    }

    public FileListDialog setCompleteClickListener(CompleteClickListener completeClickListener) {
        mTvComplete.setOnClickListener(view -> {
            if (completeClickListener != null) {
                completeClickListener.onCompleteClick(view, FileListDialog.this, mParentFile);
            }
        });
        return this;
    }

    class StorageAsyncTask extends AsyncTask<File, Void, ArrayList<PYFileBean>> {
        @Override
        protected ArrayList<PYFileBean> doInBackground(File... files) {
            return PYFilePicker.startScan(mContext, files[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<PYFileBean> dataList) {
            super.onPostExecute(dataList);
            mLoadingLayout.hide();
            mStorageFileAdapter.replaceAll(dataList);
            mStorageFileAdapter.setItemClickListener((view, position, pyFileBean) -> {
                if (pyFileBean.getChildCount() == -1) {
                    // 点击的是文件
                    ShowUtils.showToast("点击的是文件");
                } else {
                    // 点击的是目录
                    mCurrentLevel++;
                    mParentFile = pyFileBean.getFile();
                    startScan(mParentFile);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentLevel > 1) {
            mCurrentLevel--;
            mParentFile = mParentFile.getParentFile();
            startScan(mParentFile);
        } else {
            super.onBackPressed();
        }
    }

    public interface CompleteClickListener {
        void onCompleteClick(View v, FileListDialog fileListDialog, File destFile);
    }
}
