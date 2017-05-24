package com.neuroandroid.pyfilebrowser.bean;

/**
 * Created by NeuroAndroid on 2017/5/24.
 */

public class ClassifyBean {
    private String classifyTitle;
    private int classifyImg;

    public ClassifyBean(String classifyTitle, int classifyImg) {
        this.classifyTitle = classifyTitle;
        this.classifyImg = classifyImg;
    }

    public String getClassifyTitle() {
        return classifyTitle;
    }

    public void setClassifyTitle(String classifyTitle) {
        this.classifyTitle = classifyTitle;
    }

    public int getClassifyImg() {
        return classifyImg;
    }

    public void setClassifyImg(int classifyImg) {
        this.classifyImg = classifyImg;
    }
}
