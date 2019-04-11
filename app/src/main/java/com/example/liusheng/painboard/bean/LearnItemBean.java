package com.example.liusheng.painboard.bean;

/**
 * Created by chenzhen on 2019/3/26.
 */

public class LearnItemBean{


    private String groupName;
    private int backgroundImageId;
    private int guideImageId;

    public LearnItemBean(String groupName, int backgroundImageId, int guideImageId) {
        this.groupName = groupName;
        this.backgroundImageId = backgroundImageId;
        this.guideImageId = guideImageId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(int backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
    }

    public int getGuideImageId() {
        return guideImageId;
    }

    public void setGuideImageId(int guideImageId) {
        this.guideImageId = guideImageId;
    }

}
