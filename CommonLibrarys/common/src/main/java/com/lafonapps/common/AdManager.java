package com.lafonapps.common;

import android.app.Activity;
import android.app.Application;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lafonapps.adadapter.AdBean;
import com.lafonapps.adadapter.AdListener;
import com.lafonapps.adadapter.AdType;
import com.lafonapps.adadapter.AdsAdapter;
import com.lafonapps.adadapter.utils.CommonUtils;
import com.lafonapps.adadapter.utils.ViewUtil;

import java.util.ArrayList;
import java.util.HashSet;

import static com.lafonapps.adadapter.AdType.SPLASHTYPE;

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

public class AdManager {
    private volatile static AdManager adManager;

    private AdManager() {};

    private AdsAdapter[] adsAdapters = new AdsAdapter[6];

    public static Application sharedApplication;


    public static AdManager getInstance() {
        if (adManager ==null) {
            synchronized (AdManager.class) {
                if (adManager ==null) {
                    adManager = new AdManager();
                }
            }
        }
        return adManager;
    }

    private Class targetClass;

    protected Class getTargetClass() {
        return targetClass;
    }

    protected void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    String[] adClassManagerName = new String[]{
            "com.lafonapps.xiaomiad.XiaoMiManager",
            "com.lafonapps.tuiaadscommon.TuiAAdsManager",
            "com.lafonapps.admobadscommon.AdmobAdsManager",
            "com.lafonapps.oppoadscommon.OppoAdsManager" ,
            "com.lafonapps.tencentad.TencentAdManager" ,
            "com.lafonapps.facebookadscommon.FacebookAdsManager"
    };

    //广告初始化
    public void initAdSdk (Application app) {
        sharedApplication = app;
        String[] adName = {CommonConfig.sharedCommonConfig.interstitialAdName,
                CommonConfig.sharedCommonConfig.nativeLAdName,
                CommonConfig.sharedCommonConfig.nativeSAdName,
                CommonConfig.sharedCommonConfig.nativeMAdName,
                CommonConfig.sharedCommonConfig.vedioAdName,
                CommonConfig.sharedCommonConfig.bannerAdName,
                CommonConfig.sharedCommonConfig.standbyBannerAdName,
                CommonConfig.sharedCommonConfig.floatAdName,
                CommonConfig.sharedCommonConfig.splashAdName
        };
        ArrayList<String> adNameList=new ArrayList<String>();

        for (int i=0 ; i<adName.length ; i++) {
            adNameList.add(adName[i]);
        }
        HashSet<String> adNameSetList=new HashSet<String>();
        adNameSetList.addAll(adNameList);

        for (int i = 0; i <adNameSetList.size() ; i++) {
            Log.i("info",(String) adNameSetList.toArray()[i]);
            String className = "";
            String appId = "";
            try {
                switch ((String) adNameSetList.toArray()[i]) {
                    case "xiaomi":
                        className = adClassManagerName[0];
                        appId = CommonConfig.sharedCommonConfig.appID4XiaoMi;
                        Class<AdsAdapter> xiaomi = (Class<AdsAdapter>) Class.forName(className);
                        adsAdapters[0] = xiaomi.newInstance();
                        adsAdapters[0].initAdSdk(app,appId, CommonConfig.sharedCommonConfig.isDebug);
                        break;
                    case "tuia":
                        className = adClassManagerName[1];
                        Class<AdsAdapter> tuia = (Class<AdsAdapter>) Class.forName(className);
                        adsAdapters[1] = tuia.newInstance();
                        adsAdapters[1].initAdSdk(app,appId, CommonConfig.sharedCommonConfig.isDebug);
                        break;
                    case "admob":
                        className = adClassManagerName[2];
                        appId = CommonConfig.sharedCommonConfig.appID4Admob;
                        Class<AdsAdapter> admob = (Class<AdsAdapter>) Class.forName(className);
                        adsAdapters[2] = admob.newInstance();
                        adsAdapters[2].initAdSdk(app,appId, CommonConfig.sharedCommonConfig.isDebug);
                        break;
                    case "oppo":
                        className = adClassManagerName[3];
                        appId = CommonConfig.sharedCommonConfig.appID4OPPO;
                        Class<AdsAdapter> oppo = (Class<AdsAdapter>) Class.forName(className);
                        adsAdapters[3] = oppo.newInstance();
                        adsAdapters[3].initAdSdk(app,appId, CommonConfig.sharedCommonConfig.isDebug);
                        break;
                    case "tencent":
                        className = adClassManagerName[4];
                        Class<AdsAdapter> tencent = (Class<AdsAdapter>) Class.forName(className);
                        adsAdapters[4] = tencent.newInstance();
                        adsAdapters[4].initAdSdk(app,appId, CommonConfig.sharedCommonConfig.isDebug);
                        break;
                    case "facebook":
                        className = adClassManagerName[5];
                        Class<AdsAdapter> facebook= (Class<AdsAdapter>) Class.forName(className);
                        adsAdapters[5] = facebook.newInstance();
                        adsAdapters[5].initAdSdk(app,appId, CommonConfig.sharedCommonConfig.isDebug);
                        break;
                    default:
                        break;
                }
            }catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    //广告显示／插屏加载
    public void showAd(String adPlatformType, final Activity context, ViewGroup viewGroup, final int adType, AdListener adListener){

        AdBean adBean = new AdBean();
        boolean isShowSplash = CommonConfig.sharedCommonConfig.isShowSplash;
        boolean isShowOtherAd = CommonConfig.sharedCommonConfig.isShowOtherAd;
        adBean.setContext(context);
        adBean.setAdType(adType);
        adBean.setAdListener(adListener);
        if (targetClass !=null) {
            adBean.setTargetClass(targetClass);
        }
        String bannerId = "";
        String splashId = "";
        String floatId = "";
        String nativeSId = "";
        String nativeMId = "";
        String nativeLId = "";
        String videoId = "";
        String interstitialId = "";
        String nativeBannerId = "";
        //二次点击下载判断
        AdAdapterLayout adAdapterLayout = new AdAdapterLayout(context);
        if (viewGroup != null){
            viewGroup.addView(adAdapterLayout, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        }
        adAdapterLayout.setTouchListener(new AdAdapterLayout.TouchListener() {
            @Override
            public boolean shouldComfirmBeforeDownloadApp() {
                if(adType == SPLASHTYPE) {
                    return CommonConfig.sharedCommonConfig.shouldComfirmBeforeDownloadAppOnSplashAdClick;
                } else if(adType == AdType.BANNERTYPE) {
                    return CommonConfig.sharedCommonConfig.shouldComfirmBeforeDownloadAppOnBannerViewClick;
                } else {
                    return CommonConfig.sharedCommonConfig.shouldComfirmBeforeDownloadAppOnOtherAdViewClick;
                }
            }

            @Override
            public Rect exceptRect() {
                if (adType == SPLASHTYPE) {
                    return new Rect(ViewUtil.getDeviceWidthInDP(context) - 100, 0, ViewUtil.getDeviceWidthInDP(context), 60);
                } else {
                    return new Rect();
                }
            }
        });
        adBean.setViewGroup(adAdapterLayout);

        //是否显示广告
        if (adType==SPLASHTYPE&&!isShowSplash)  {
            CommonUtils.startOtherActivity(context,targetClass);
            return;
        }
        if (adType!=SPLASHTYPE&&!isShowOtherAd) {
            return;
        }

        switch (adPlatformType) {
            case "xiaomi":
                bannerId = CommonConfig.sharedCommonConfig.bannerAdUnitID4XiaoMi;
                splashId = CommonConfig.sharedCommonConfig.splashAdUnitID4XiaoMi;
                floatId = CommonConfig.sharedCommonConfig.floatAdUnitID4XiaoMi;
                nativeSId = CommonConfig.sharedCommonConfig.nativeAdUnitID4XiaoMi;
                nativeMId = CommonConfig.sharedCommonConfig.nativeAdUnitID132H4XiaoMi;
                nativeLId = CommonConfig.sharedCommonConfig.nativeAdUnitID250H4XiaoMi;
                videoId = CommonConfig.sharedCommonConfig.videoAdUnitID4XiaoMi;
                interstitialId = CommonConfig.sharedCommonConfig.interstitialAdUnitID4XiaoMi;
                adBean.setGravity(CommonConfig.sharedCommonConfig.gravity);
                break;
            case "tuia":
                bannerId = CommonConfig.sharedCommonConfig.bannerAdUnitID4Adtuia;
                splashId = CommonConfig.sharedCommonConfig.splashAdUnitID4Adtuia;
                floatId = CommonConfig.sharedCommonConfig.floatAdUnitID4tuia;
                nativeSId = CommonConfig.sharedCommonConfig.nativeAdUnitID180H4tuia;
                nativeLId = CommonConfig.sharedCommonConfig.nativeAdUnitID420H4tuia;
                videoId = CommonConfig.sharedCommonConfig.videoAdUnitID4tuia;
                interstitialId = CommonConfig.sharedCommonConfig.interstitialAdUnitID4Adtuia;
                break;
            case "admob":
                bannerId = CommonConfig.sharedCommonConfig.bannerAdUnitID4Admob;
                splashId = CommonConfig.sharedCommonConfig.splashAdUnitID4Admob;
                floatId = CommonConfig.sharedCommonConfig.floatAdUnitID4Admob;
                nativeSId = CommonConfig.sharedCommonConfig.nativeAdUnitID4Admob;
                nativeMId = CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Admob;
                nativeLId = CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Admob;
                videoId = CommonConfig.sharedCommonConfig.videoAdUnitID4Admob;
                interstitialId = CommonConfig.sharedCommonConfig.interstitialAdUnitID4Admob;
                adBean.setTestDevices(CommonConfig.sharedCommonConfig.testDevices);
                break;
            case "oppo":
                bannerId = CommonConfig.sharedCommonConfig.bannerAdUnitID4OPPO;
                splashId = CommonConfig.sharedCommonConfig.splashAdUnitID4OPPO;
                floatId = CommonConfig.sharedCommonConfig.floatAdUnitID4OPPO;
                nativeSId = CommonConfig.sharedCommonConfig.nativeAdUnitID4OPPO;
                nativeMId = CommonConfig.sharedCommonConfig.nativeAdUnitID132H4OPPO;
                nativeLId = CommonConfig.sharedCommonConfig.nativeAdUnitID250H4OPPO;
                videoId = CommonConfig.sharedCommonConfig.videoAdUnitID4OPPO;
                interstitialId = CommonConfig.sharedCommonConfig.interstitialAdUnitID4OPPO;
                break;
            case "tencent":
                bannerId = CommonConfig.sharedCommonConfig.bannerAdUnitID4Tencent;
                splashId = CommonConfig.sharedCommonConfig.splashAdUnitID4Tencent;
                floatId = CommonConfig.sharedCommonConfig.floatAdUnitID4Tencent;
                nativeSId = CommonConfig.sharedCommonConfig.nativeAdUnitID4Tencent;
                nativeMId = CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Tencent;
                nativeLId = CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Tencent;
                videoId = CommonConfig.sharedCommonConfig.videoAdUnitID4Tencent;
                interstitialId = CommonConfig.sharedCommonConfig.interstitialAdUnitID4Tencent;
                adBean.setTencentAppId(CommonConfig.sharedCommonConfig.appID4Tencent);
                break;
            case "facebook":
                bannerId = CommonConfig.sharedCommonConfig.bannerAdUnitID4Facebook;
                nativeBannerId = CommonConfig.sharedCommonConfig.nativeBannerAdUnitID4Facebook;
                splashId = CommonConfig.sharedCommonConfig.splashAdUnitID4Facebook;
                floatId = CommonConfig.sharedCommonConfig.floatAdUnitID4Facebook;
                nativeSId = CommonConfig.sharedCommonConfig.nativeAdUnitID4Facebook;
                nativeMId = CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Facebook;
                nativeLId = CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Facebook;
                videoId = CommonConfig.sharedCommonConfig.videoAdUnitID4Facebook;
                interstitialId = CommonConfig.sharedCommonConfig.interstitialAdUnitID4Facebook;
                adBean.setTestDevices(CommonConfig.sharedCommonConfig.testDevices);
                break;
        }
        adBean.setBannerId(bannerId);
        adBean.setNativeBannerId(nativeBannerId);
        adBean.setSplashId(splashId);
        adBean.setFloatId(floatId);
        adBean.setNativeSId(nativeSId);
        adBean.setNativeMId(nativeMId);
        adBean.setNativeLId(nativeLId);
        adBean.setVideoId(videoId);
        adBean.setInterstitialId(interstitialId);
        try {
            setAdWay(adBean,adPlatformType,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //插屏广告显示
    public void disPlayInterstitial(String adPlatformType) {
        try {
            setAdWay(null,adPlatformType,2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //销毁
    public void destoryAd(String adPlatformType){
        try {
            setAdWay(null,adPlatformType,3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setAdWay (AdBean adBean,String adPlatformType,int sign) {
        switch (adPlatformType) {
            case "xiaomi":
                if (adsAdapters[0]!=null) {
                    switch (sign) {
                        case 1:
                            adsAdapters[0].showAd(adBean);
                            break;
                        case 2:
                            adsAdapters[0].disPlayInterstitialAd();
                            break;
                        case 3:
                            adsAdapters[0].destoryAd();
                            break;
                    }
                }
                break;
            case "tuia":
                if (adsAdapters[1]!=null) {
                    switch (sign) {
                        case 1:
                            adsAdapters[1].showAd(adBean);
                            break;
                        case 2:
                            adsAdapters[1].disPlayInterstitialAd();
                            break;
                        case 3:
                            adsAdapters[1].destoryAd();
                            break;
                    }
                }
                break;
            case "admob":
                if (adsAdapters[2]!=null) {
                    switch (sign) {
                        case 1:
                            adsAdapters[2].showAd(adBean);
                            break;
                        case 2:
                            adsAdapters[2].disPlayInterstitialAd();
                            break;
                        case 3:
                            adsAdapters[2].destoryAd();
                            break;
                    }
                }
                break;
            case "oppo":
                if (adsAdapters[3]!=null) {
                    switch (sign) {
                        case 1:
                            adsAdapters[3].showAd(adBean);
                            break;
                        case 2:
                            adsAdapters[3].disPlayInterstitialAd();
                            break;
                        case 3:
                            adsAdapters[3].destoryAd();
                            break;
                    }
                }
                break;
            case "tencent":
                if (adsAdapters[4]!=null) {
                    switch (sign) {
                        case 1:
                            adsAdapters[4].showAd(adBean);
                            break;
                        case 2:
                            adsAdapters[4].disPlayInterstitialAd();
                            break;
                        case 3:
                            adsAdapters[4].destoryAd();
                            break;
                    }
                }
                break;
            case "facebook":
                if (adsAdapters[5]!=null) {
                    switch (sign) {
                        case 1:
                            adsAdapters[5].showAd(adBean);
                            break;
                        case 2:
                            adsAdapters[5].disPlayInterstitialAd();
                            break;
                        case 3:
                            adsAdapters[5].destoryAd();
                            break;
                    }
                }
                break;

        }

    }

}
