package com.neuroandroid.pyfilebrowser.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */
public class PYFileBean implements ISelect {
    private boolean selected;

    private int id;
    private String title;  // 文件名称
    private String path;  // 文件路径
    private long size;  // 文件大小
    private long date;  // 文件修改时间
    private int classifyFlag;
    private File file;
    private int fileType;  // 文件类型
    private int childCount;  // 多少个子目录
    private Bitmap thumbnail;  // 缩略图

    // 音频albumId
    private int albumId;
    // apk缩略图
    private Drawable appIcon;

    public PYFileBean() {
    }

    public PYFileBean(boolean selected, String title, String path, long size, long date, int classifyFlag) {
        this.selected = selected;
        this.title = title;
        this.path = path;
        this.size = size;
        this.date = date;
        this.classifyFlag = classifyFlag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getClassifyFlag() {
        return classifyFlag;
    }

    public void setClassifyFlag(int classifyFlag) {
        this.classifyFlag = classifyFlag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setText(String text) {

    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "title : " + title +
                " path : " + path +
                " size : " + size +
                " date : " + date +
                " classifyFlag : " + classifyFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PYFileBean classifyFileBean = (PYFileBean) o;
        if (!title.equals(classifyFileBean.title)) return false;
        if (!path.equals(classifyFileBean.path)) return false;
        if (size != classifyFileBean.size) return false;
        if (date != classifyFileBean.date) return false;
        return true;
    }
}
