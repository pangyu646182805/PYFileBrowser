package com.neuroandroid.pyfilebrowser.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.neuroandroid.pyfilebrowser.bean.ClassifyFileBean;
import com.neuroandroid.pyfilebrowser.ui.fragment.ClassifyFragment;

import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/25.
 */

public class PhotoLoader {
    @NonNull
    public static ArrayList<ClassifyFileBean> getAllPhotos(@NonNull Context context) {
        Cursor cursor = makePhotoCursor(context, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        return getPhotos(cursor);
    }

    @NonNull
    private static ArrayList<ClassifyFileBean> getPhotos(@Nullable final Cursor cursor) {
        ArrayList<ClassifyFileBean> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getPhotoFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    private static ClassifyFileBean getPhotoFromCursorImpl(Cursor cursor) {
        ClassifyFileBean fileBean = new ClassifyFileBean();
        fileBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE)));
        fileBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
        fileBean.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)));
        fileBean.setDate(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED)));
        fileBean.setClassifyFlag(ClassifyFragment.CLASSIFY_PHOTO);
        return fileBean;
    }

    @Nullable
    private static Cursor makePhotoCursor(@NonNull final Context context, @Nullable final String selection, final String[] selectionValues, final String sortOrder) {
        try {
            return context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            BaseColumns._ID,// 0
                            MediaStore.Images.ImageColumns.TITLE,// 1
                            MediaStore.Images.ImageColumns.SIZE,// 2
                            MediaStore.Images.ImageColumns.DATE_MODIFIED,// 3
                            MediaStore.Images.ImageColumns.DATA,// 5

                    }, selection, selectionValues, sortOrder);
        } catch (SecurityException e) {
            return null;
        }
    }
}
