package com.lafonapps.adadapter;

import android.app.Activity;
import android.app.Application;
import android.view.ViewGroup;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/08/08
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public interface AdsAdapter {

    //广告初始化
    public void initAdSdk(Application application ,String appId,boolean isDebug);
    //显示广告  加载插屏广告
    public void showAd (AdBean adBean);
    //显示插屏广告
    public void disPlayInterstitialAd();
    //销毁
    public void destoryAd ();

}
