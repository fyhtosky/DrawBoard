package com.lafonapps.adadapter;


public interface AdListener {

    public void onTimeOut(int adType);

    public void onLoaded(int adType);

    public void onLoadFailed(int adType);

    public void onCloseClick(int adType);

    public void onDismiss(int adType);

    public void onAdClick(int adType);

    public void onAdExposure(int adType);
}