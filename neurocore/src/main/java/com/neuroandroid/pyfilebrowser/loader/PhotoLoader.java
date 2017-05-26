package com.neuroandroid.pyfilebrowser.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.ui.fragment.ClassifyFragment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/25.
 */

public class PhotoLoader {
    @NonNull
    public static ArrayList<PYFileBean> getAllPhotos(@NonNull Context context) {
        String selection = MediaStore.Images.Media.MIME_TYPE + "=? or " +
                MediaStore.Images.Media.MIME_TYPE + "=? or " +
                MediaStore.Images.Media.MIME_TYPE + "=? or " +
                MediaStore.Images.Media.MIME_TYPE + "=?";
        String[] selectionValues = new String[]{"image/jpeg", "image/png","image/jpg","image/gif"};
        Cursor cursor = makePhotoCursor(context, selection, selectionValues, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        return getPhotos(cursor);
    }

    @NonNull
    private static ArrayList<PYFileBean> getPhotos(@Nullable final Cursor cursor) {
        ArrayList<PYFileBean> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getPhotoFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    private static PYFileBean getPhotoFromCursorImpl(Cursor cursor) {
        PYFileBean fileBean = new PYFileBean();
        fileBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE)));
        fileBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
        fileBean.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)));
        fileBean.setDate(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED)));
        fileBean.setFile(new File(fileBean.getPath()));
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
