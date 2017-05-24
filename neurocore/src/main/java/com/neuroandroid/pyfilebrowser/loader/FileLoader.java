package com.neuroandroid.pyfilebrowser.loader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.neuroandroid.pyfilebrowser.bean.ClassifyFileBean;
import com.neuroandroid.pyfilebrowser.filter.PYFileFilter;
import com.neuroandroid.pyfilebrowser.provider.PYFileStore;
import com.neuroandroid.pyfilebrowser.utils.L;
import com.neuroandroid.pyfilebrowser.utils.SetUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeuroAndroid on 2017/5/24.
 * 递归实现文件搜索
 */
public class FileLoader extends AsyncTask<String, Void, Void> {
    private int mClassifyFlag;
    // 文件过滤器
    private PYFileFilter mPYFileFilter;
    private Context mContext;
    // 数据库
    private PYFileStore mPYFileStore;
    // 符合条件的文件列表
    private List<ClassifyFileBean> mClassifyFileBeanDataList;

    public FileLoader(Context context, int classifyFlag) {
        this.mContext = context;
        mClassifyFlag = classifyFlag;
    }

    /**
     * AsyncTask异步任务的初始化
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPYFileStore = PYFileStore.getInstance(mContext);
        mClassifyFileBeanDataList = new ArrayList<>();
    }

    /**
     * AsyncTask异步任务
     */
    @Override
    protected Void doInBackground(String... strings) {
        mPYFileFilter = new PYFileFilter(strings);
        getFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
        return null;
    }

    /**
     * AsyncTask异步任务结束的回调
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mCallBack != null) mCallBack.onPostExecute(mClassifyFileBeanDataList);
        if (!SetUtils.equals(mPYFileStore.getClassifyFileDataList(mClassifyFlag), mClassifyFileBeanDataList)) {
            L.e("有文件被添加或者删除，去保存文件");
            new AsyncFileSaver().execute(mClassifyFileBeanDataList);
        } else {
            L.e("不需要保存文件到数据库");
        }
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
                ClassifyFileBean classifyFileBean = new ClassifyFileBean();
                String title = absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
                classifyFileBean.setTitle(title);
                classifyFileBean.setPath(absolutePath);
                classifyFileBean.setDate(f.lastModified());
                classifyFileBean.setSize(f.length());
                classifyFileBean.setClassifyFlag(mClassifyFlag);
                mClassifyFileBeanDataList.add(classifyFileBean);
            }
        }
    }

    private CallBack mCallBack;

    public FileLoader setCallBack(CallBack callBack) {
        mCallBack = callBack;
        return this;
    }

    /**
     * 文件加载的回调接口
     */
    public interface CallBack {
        void onPostExecute(List<ClassifyFileBean> classifyFileBeanDataList);
    }

    /**
     * 保存文件列表
     */
    private class AsyncFileSaver extends AsyncTask<List<ClassifyFileBean>, Void, Void> {
        @Override
        protected Void doInBackground(List<ClassifyFileBean>... lists) {
            List<ClassifyFileBean> list = lists[0];
            for (ClassifyFileBean bean : list) {
                mPYFileStore.addItem(bean);
            }
            return null;
        }
    }
}
