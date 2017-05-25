package com.neuroandroid.pyfilebrowser.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.neuroandroid.pyfilebrowser.bean.ClassifyFileBean;
import com.neuroandroid.pyfilebrowser.ui.fragment.ClassifyFragment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/25.
 */

public class VideoLoader {
    @NonNull
    public static ArrayList<ClassifyFileBean> getAllVideos(@NonNull Context context) {
        Cursor cursor = makePhotoCursor(context, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        return getVideos(cursor);
    }

    @NonNull
    private static ArrayList<ClassifyFileBean> getVideos(@Nullable final Cursor cursor) {
        ArrayList<ClassifyFileBean> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getVideoFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    private static ClassifyFileBean getVideoFromCursorImpl(Cursor cursor) {
        ClassifyFileBean fileBean = new ClassifyFileBean();
        fileBean.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
        fileBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE)));
        fileBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)));
        fileBean.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE)));
        fileBean.setDate(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED)));
        fileBean.setFile(new File(fileBean.getPath()));
        fileBean.setClassifyFlag(ClassifyFragment.CLASSIFY_VIDEO);
        return fileBean;
    }

    @Nullable
    private static Cursor makePhotoCursor(@NonNull final Context context, @Nullable final String selection, final String[] selectionValues, final String sortOrder) {
        try {
            return context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            BaseColumns._ID,// 0
                            MediaStore.Video.VideoColumns.TITLE,// 1
                            MediaStore.Video.VideoColumns.SIZE,// 2
                            MediaStore.Video.VideoColumns.DATE_MODIFIED,// 3
                            MediaStore.Video.VideoColumns.DATA// 5
                    }, selection, selectionValues, sortOrder);
        } catch (SecurityException e) {
            return null;
        }
    }
}
