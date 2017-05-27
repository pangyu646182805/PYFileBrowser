package com.neuroandroid.pyfilebrowser.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
     * 效率不高 : 不推荐使用
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

    public static void getSpecificTypeOfFile(Context context, String[] extension) {
        //从外存中获取
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        //构造筛选语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
        //获取内容解析器对象
        ContentResolver resolver = context.getContentResolver();
        //获取游标
        Cursor cursor = resolver.query(fileUri, projection, selection, null, sortOrder);
        if (cursor == null)
            return;
        //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
        if (cursor.moveToLast()) {
            do {
                //输出文件的完整路径
                String data = cursor.getString(0);
                Log.d("tag", data);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
    }

    /**
     * 根据文件扩展名获取所有符合条件的文件
     */
    @NonNull
    public static ArrayList<PYFileBean> getAllFilesByExtensionName(@NonNull Context context, String[] extension, int classifyFlag) {
        Cursor cursor = makeFileCursor(context, extension);
        return getFiles(cursor, context, classifyFlag);
    }

    @NonNull
    private static ArrayList<PYFileBean> getFiles(@Nullable final Cursor cursor, @NonNull Context context, int classifyFlag) {
        ArrayList<PYFileBean> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getFileFromCursorImpl(cursor, context, classifyFlag));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    private static PYFileBean getFileFromCursorImpl(Cursor cursor, @NonNull Context context, int classifyFlag) {
        PYFileBean fileBean = new PYFileBean();
        fileBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
        fileBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)));
        fileBean.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE)));
        fileBean.setFile(new File(fileBean.getPath()));
        fileBean.setDate(fileBean.getFile().lastModified());
        fileBean.setClassifyFlag(classifyFlag);
        if (classifyFlag == ClassifyFragment.CLASSIFY_APK) {
            fileBean.setAppIcon(FileUtils.getApkIcon(context, fileBean.getPath()));
        }
        return fileBean;
    }

    private static Cursor makeFileCursor(@NonNull final Context context, String[] extension) {
        //从外存中获取
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.SIZE
        };
        //构造筛选语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        try {
            return context.getContentResolver().query(fileUri, projection, selection, null, MediaStore.Files.FileColumns.DATE_MODIFIED);
        } catch (SecurityException e) {
            return null;
        }
    }
}
