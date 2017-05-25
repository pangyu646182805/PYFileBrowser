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

public class AudioLoader {
    @NonNull
    public static ArrayList<ClassifyFileBean> getAllAudios(@NonNull Context context) {
        Cursor cursor = makePhotoCursor(context, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        return getAudios(cursor);
    }

    @NonNull
    private static ArrayList<ClassifyFileBean> getAudios(@Nullable final Cursor cursor) {
        ArrayList<ClassifyFileBean> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getAudioFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    private static ClassifyFileBean getAudioFromCursorImpl(Cursor cursor) {
        ClassifyFileBean fileBean = new ClassifyFileBean();
        fileBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
        fileBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)));
        fileBean.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE)));
        fileBean.setDate(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED)));
        fileBean.setClassifyFlag(ClassifyFragment.CLASSIFY_AUDIO);
        return fileBean;
    }

    @Nullable
    private static Cursor makePhotoCursor(@NonNull final Context context, @Nullable final String selection, final String[] selectionValues, final String sortOrder) {
        try {
            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            BaseColumns._ID,// 0
                            MediaStore.Audio.AudioColumns.TITLE,// 1
                            MediaStore.Audio.AudioColumns.SIZE,// 2
                            MediaStore.Audio.AudioColumns.DATE_MODIFIED,// 3
                            MediaStore.Audio.AudioColumns.DATA,// 5
                    }, selection, selectionValues, sortOrder);
        } catch (SecurityException e) {
            return null;
        }
    }
}
