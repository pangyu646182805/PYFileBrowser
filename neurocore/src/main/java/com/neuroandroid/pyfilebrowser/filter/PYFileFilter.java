package com.neuroandroid.pyfilebrowser.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by NeuroAndroid on 2017/5/24.
 * 文件过滤器
 */
public class PYFileFilter implements FileFilter {
    private String[] mFileType;

    public PYFileFilter(String[] fileType) {
        mFileType = fileType;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            // 目录层级太大则过滤
            int length = file.getAbsolutePath().split("/").length - 4;
            return length <= 3;
        }
        if (mFileType != null && mFileType.length > 0) {
            for (int i = 0; i < mFileType.length; i++) {
                if (file.getName().endsWith(mFileType[i].toLowerCase()) || file.getName().endsWith(mFileType[i].toUpperCase())) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
