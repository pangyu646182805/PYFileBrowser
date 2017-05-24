package com.neuroandroid.pyfilebrowser.bean;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */
public class ClassifyFileBean implements ISelect {
    private boolean selected;

    private String title;  // 文件名称
    private String path;  // 文件路径
    private long size;  // 文件大小
    private long date;  // 文件修改时间
    private int classifyFlag;

    public ClassifyFileBean() {
    }

    public ClassifyFileBean(boolean selected, String title, String path, long size, long date, int classifyFlag) {
        this.selected = selected;
        this.title = title;
        this.path = path;
        this.size = size;
        this.date = date;
        this.classifyFlag = classifyFlag;
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

        ClassifyFileBean classifyFileBean = (ClassifyFileBean) o;
        if (!title.equals(classifyFileBean.title)) return false;
        if (!path.equals(classifyFileBean.path)) return false;
        if (size != classifyFileBean.size) return false;
        if (date != classifyFileBean.date) return false;
        return true;
    }
}
