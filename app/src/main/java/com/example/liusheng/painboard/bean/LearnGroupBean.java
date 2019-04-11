package com.example.liusheng.painboard.bean;
import android.os.Parcel;
import java.util.List;

/**
 * Created by chenzhen on 2019/3/26.
 */

public class LearnGroupBean{


    private String groupName;
    private List<LearnItemBean> itemList;

    public LearnGroupBean(String groupName, List<LearnItemBean> list) {
        this.groupName = groupName;
        this.itemList = list;
    }

    protected LearnGroupBean(Parcel in) {
        groupName = in.readString();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<LearnItemBean> getItemList() {
        return itemList;
    }

    public void setItemArray(List<LearnItemBean> list) {
        this.itemList = list;
    }

}
