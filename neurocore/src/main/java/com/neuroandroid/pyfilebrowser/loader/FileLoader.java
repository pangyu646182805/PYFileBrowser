package com.neuroandroid.pyfilebrowser.loader;

import android.content.Context;
import android.os.Environment;

import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.filter.PYFileFilter;
import com.neuroandroid.pyfilebrowser.ui.fragment.ClassifyFragment;
import com.neuroandroid.pyfilebrowser.utils.FileUtils;
import com.neuroandroid.pyfilebrowser.utils.L;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/24.
 * 递归实现文件搜索
 */
public class FileLoader {
    private int mClassifyFlag;
    // 文件过滤器
    private PYFileFilter mPYFileFilter;
    private Context mContext;
    // 符合条件的文件列表
    private ArrayList<PYFileBean> mClassifyFileBeanDataList;

    public ArrayList<PYFileBean> getClassifyFileBeanDataList() {
        return mClassifyFileBeanDataList;
    }

    public FileLoader(Context context, int classifyFlag) {
        this.mContext = context;
        mClassifyFlag = classifyFlag;
        mClassifyFileBeanDataList = new ArrayList<>();
    }

    public void loadFile(String[] fileFilter) {
        mPYFileFilter = new PYFileFilter(fileFilter);
        getFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
    }

    /**
     * 递归实现搜索文件系统
     * PYFileFilter : 文件过滤器 {@link PYFileFilter}
     */
    private void getFiles(File file) {
        File[] files = file.listFiles(mPYFileFilter);
        for (File f : files) {
            if (f.isDirectory()) {
                getFiles(f);
            } else {
                String absolutePath = f.getAbsolutePath();
                PYFileBean classifyFileBean = new PYFileBean();
                String title = absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
                classifyFileBean.setTitle(title);
                classifyFileBean.setPath(absolutePath);
                classifyFileBean.setDate(f.lastModified());
                classifyFileBean.setSize(f.length());
                classifyFileBean.setClassifyFlag(mClassifyFlag);
                classifyFileBean.setFile(new File(absolutePath));
                if (mClassifyFlag == ClassifyFragment.CLASSIFY_APK) {
                    classifyFileBean.setAppIcon(FileUtils.getApkIcon(mContext, absolutePath));
                }
                L.e("absolutePath : " + absolutePath);
                mClassifyFileBeanDataList.add(classifyFileBean);
            }
        }
    }
}
