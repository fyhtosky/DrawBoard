package com.lafonapps.adadapter;

import android.app.Activity;
import android.view.ViewGroup;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/08/09
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class AdBean {
    //
    private String nativeBannerId;
    private String bannerId;
    private String nativeSId;
    private String nativeMId;
    private String nativeLId;
    private String splashId;
    private String floatId;
    private String interstitialId;
    private String videoId;
    private int gravity;//小米悬浮广告位置
    private int adType;//广告位类型

    private Activity context;
    private ViewGroup viewGroup;

    private AdListener adListener;

    private Class targetClass;

    private String[] testDevices;

    private String tencentAppId;

    public String getTencentAppId() {
        return tencentAppId;
    }

    public void setTencentAppId(String tencentAppId) {
        this.tencentAppId = tencentAppId;
    }

    public String[] getTestDevices() {
        return testDevices;
    }

    public void setTestDevices(String[] testDevices) {
        this.testDevices = testDevices;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getNativeSId() {
        return nativeSId;
    }

    public void setNativeSId(String nativeSId) {
        this.nativeSId = nativeSId;
    }

    public String getNativeMId() {
        return nativeMId;
    }

    public void setNativeMId(String nativeMId) {
        this.nativeMId = nativeMId;
    }

    public String getNativeLId() {
        return nativeLId;
    }

    public void setNativeLId(String nativeLId) {
        this.nativeLId = nativeLId;
    }


    public String getSplashId() {
        return splashId;
    }

    public void setSplashId(String splashId) {
        this.splashId = splashId;
    }

    public String getFloatId() {
        return floatId;
    }

    public void setFloatId(String floatId) {
        this.floatId = floatId;
    }

    public String getInterstitialId() {
        return interstitialId;
    }

    public void setInterstitialId(String interstitialId) {
        this.interstitialId = interstitialId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public void setViewGroup(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    public String getNativeBannerId() {
        return nativeBannerId;
    }

    public void setNativeBannerId(String nativeBannerId) {
        this.nativeBannerId = nativeBannerId;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public AdListener getAdListener() {
        return adListener;
    }

    public void setAdListener(AdListener adListener) {
        this.adListener = adListener;
    }
}
