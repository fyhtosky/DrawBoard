package com.example.liusheng.painboard.bean;

/**
 * Created by chenzhen on 2019/2/15.
 */

public class WorkBean {

    private int imageWidth;
    private int imageHeight;
    private long createDate;
    private String imagePath;


    public WorkBean() {
    }

    public WorkBean(long createDate, String imagePath,int width,int height) {
        this.createDate = createDate;
        this.imagePath = imagePath;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public long getCreatDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }


    @Override
    public String toString() {
        return "WorkBean{" +
                "imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", createDate=" + createDate +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
