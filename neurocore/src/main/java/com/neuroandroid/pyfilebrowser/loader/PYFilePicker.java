package com.neuroandroid.pyfilebrowser.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.neuroandroid.pyfilebrowser.bean.PYFileBean;
import com.neuroandroid.pyfilebrowser.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NeuroAndroid on 2017/5/26.
 */

public class PYFilePicker {
    public static final int TYPE_TXT = 0;
    public static final int TYPE_HTM = 1;
    public static final int TYPE_DOC = 2;
    public static final int TYPE_ZIP = 3;
    public static final int TYPE_TAR = 4;
    public static final int TYPE_RAR = 5;
    public static final int TYPE_FOLDER = 6;
    public static final int TYPE_UNKNOW = 7;
    public static final int TYPE_AUDIO = 8;
    public static final int TYPE_VIDEO = 9;
    public static final int TYPE_IMAGE = 10;
    public static final int TYPE_APK = 11;

    /**
     * 开始扫描
     *
     * @param currentDir 当前目录
     */
    public static ArrayList<PYFileBean> startScan(Context context, File currentDir) {
        File[] childFiles = currentDir.listFiles();
        if (childFiles == null || childFiles.length == 0) {
            // 表示是一个空目录
            return null;
        }
        ArrayList<PYFileBean> dataList = new ArrayList<>();
        PYFileBean pyFileBean;
        for (File child : childFiles) {
            pyFileBean = new PYFileBean();
            String absolutePath = child.getAbsolutePath();
            String title = absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
            pyFileBean.setTitle(title);
            pyFileBean.setPath(absolutePath);
            pyFileBean.setDate(child.lastModified());
            pyFileBean.setSize(child.length());
            pyFileBean.setFile(new File(absolutePath));
            int fileType = getFileType(title);
            if (child.isDirectory()) {
                pyFileBean.setFileType(TYPE_FOLDER);
            } else {
                pyFileBean.setFileType(fileType);
            }
            if (fileType == TYPE_APK) {
                pyFileBean.setAppIcon(FileUtils.getApkIcon(context, absolutePath));
            } else if (fileType == TYPE_VIDEO) {
                pyFileBean.setThumbnail(getVideoThumbnail(absolutePath, 100, 100, MediaStore.Video.Thumbnails.MICRO_KIND));
            }
            pyFileBean.setChildCount(getChildCount(child));
            dataList.add(pyFileBean);
        }
        return dataList;
    }

    private static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    private static int getChildCount(File file) {
        String[] childList = file.list();
        if (childList != null) {
            return childList.length;
        } else {
            return -1;
        }
    }

    private static int getFileType(String title) {
        if (title.endsWith(".txt")) {
            return TYPE_TXT;
        } else if (title.endsWith(".html") || title.endsWith(".htm")) {
            return TYPE_HTM;
        } else if (title.endsWith(".doc")) {
            return TYPE_DOC;
        } else if (title.endsWith(".zip")) {
            return TYPE_ZIP;
        } else if (title.endsWith(".tar")) {
            return TYPE_TAR;
        } else if (title.endsWith(".rar")) {
            return TYPE_RAR;
        } else if (title.endsWith(".mp3")) {
            return TYPE_AUDIO;
        } else if (title.endsWith(".mp4") || title.endsWith(".wmv")) {
            return TYPE_VIDEO;
        } else if (title.endsWith(".png") || title.endsWith(".jpg")) {
            return TYPE_IMAGE;
        } else if (title.endsWith(".apk")) {
            return TYPE_APK;
        } else {
            return TYPE_UNKNOW;
        }
    }
}
