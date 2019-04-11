package com.lafonapps.common.update.bean;

import java.util.List;

/**
 * Created by chenjie on 2018/1/31.
 */
public class Version {

    /** 保持沉默，什么都不做 */
    public static final int UPDATE_POLICY_SILENCE = 0;
    /** 提示更新到最新版本 */
    public static final int UPDATE_POLICY_PROMOTE = 1;
    /** 强制更新到最新版本 */
    public static final int UPDATE_POLICY_FORCE = 2;

    private int versionCode;
    private String versionName;
    private List<String> updateLogs;
    private int updatePolicy;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public List<String> getUpdateLogs() {
        return updateLogs;
    }

    public void setUpdateLogs(List<String> updateLogs) {
        this.updateLogs = updateLogs;
    }

    public int getUpdatePolicy() {
        return updatePolicy;
    }

    public void setUpdatePolicy(int updatePolicy) {
        this.updatePolicy = updatePolicy;
    }

    @Override
    public String toString() {
        return "Version{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", updateLogs=" + updateLogs +
                '}';
    }
}
