package com.neuroandroid.pyfilebrowser.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.ui.fragment.ClassifyFragment;
import com.neuroandroid.pyfilebrowser.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public class PYFileStore extends SQLiteOpenHelper {
    @Nullable
    private static PYFileStore sInstance = null;
    private static final String DATABASE_NAME = "py_file_browser.db";
    private static final String TABLE_NAME = "tab_classify";
    private int mClassifyFlag;

    public PYFileStore(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase, TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * 单例模式
     */
    @NonNull
    public static synchronized PYFileStore getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new PYFileStore(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * 创建表格
     */
    private void createTable(@NonNull final SQLiteDatabase db, final String tableName) {
        String sql = "create table " + tableName + " (_id integer primary key autoincrement, title varchar(20) not null, " +
                "path string not null, size long not null, date long not null, classifyFlag int not null)";
        db.execSQL(sql);
    }

    public void delete(int classifyFlag) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "classifyFlag=?", new String[]{"" + classifyFlag});
        db.close();
    }

    /**
     * 添加classifyFileBean到数据库
     */
    public synchronized void addItem(PYFileBean classifyFileBean) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", classifyFileBean.getTitle());
        values.put("path", classifyFileBean.getPath());
        values.put("size", classifyFileBean.getSize());
        values.put("date", classifyFileBean.getDate());
        values.put("classifyFlag", classifyFileBean.getClassifyFlag());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * @param classifyFlag {@link ClassifyFragment#CLASSIFY_AUDIO}...
     * @return 根据classifyFlag返回文件列表
     */
    public ArrayList<PYFileBean> getClassifyFileDataList(Context context, int classifyFlag) {
        Cursor cursor = getReadableDatabase().query(TABLE_NAME, null,
                "classifyFlag=?", new String[]{classifyFlag + ""}, null, null, null);
        ArrayList<PYFileBean> dataList = new ArrayList<>();
        PYFileBean fileBean;
        while (cursor.moveToNext()) {
            fileBean = new PYFileBean();
            fileBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            fileBean.setPath(cursor.getString(cursor.getColumnIndex("path")));
            fileBean.setSize(cursor.getLong(cursor.getColumnIndex("size")));
            fileBean.setDate(cursor.getLong(cursor.getColumnIndex("date")));
            fileBean.setAppIcon(FileUtils.getApkIcon(context, fileBean.getPath()));
            fileBean.setFile(new File(fileBean.getPath()));
            fileBean.setClassifyFlag(classifyFlag);
            dataList.add(fileBean);
        }
        return dataList;
    }

    /**
     * @param classifyFlag : 如果此值为-1则返回所有的数量
     * @return 根据classifyFlag返回文件列表的数量 {@link ClassifyFragment#CLASSIFY_AUDIO}...
     */
    public int getClassifyFileDataListSize(int classifyFlag) {
        Cursor cursor;
        if (classifyFlag == -1) {
            cursor = getReadableDatabase().query(TABLE_NAME, null,
                    null, null, null, null, null);
        } else {
            cursor = getReadableDatabase().query(TABLE_NAME, null,
                    "classifyFlag=?", new String[]{classifyFlag + ""}, null, null, null);
        }
        return cursor.getCount();
    }
}
