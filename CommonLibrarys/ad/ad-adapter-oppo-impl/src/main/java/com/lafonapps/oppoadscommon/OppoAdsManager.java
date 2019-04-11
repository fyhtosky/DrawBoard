package com.lafonapps.oppoadscommon;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lafonapps.adadapter.AdBean;
import com.lafonapps.adadapter.AdListener;
import com.lafonapps.adadapter.AdType;
import com.lafonapps.adadapter.AdsAdapter;
import com.lafonapps.adadapter.utils.CommonUtils;
import com.oppo.mobad.api.InitParams;
import com.oppo.mobad.api.MobAdManager;
import com.oppo.mobad.api.ad.BannerAd;
import com.oppo.mobad.api.ad.InterstitialAd;
import com.oppo.mobad.api.ad.SplashAd;
import com.oppo.mobad.api.listener.IBannerAdListener;
import com.oppo.mobad.api.listener.IInterstitialAdListener;
import com.oppo.mobad.api.listener.ISplashAdListener;
import com.oppo.mobad.api.params.SplashAdParams;

public class OppoAdsManager implements AdsAdapter {
    private static final String TAG1 = OppoAdsManager.class.getName();

    private BannerAd bannerAd;
    private ViewGroup mViewGroup;
    private Activity mContext;
    private InterstitialAd interstitialAd;
    private int retryDelayForFailed;
    private boolean isInterstitialAdReady;
    private SplashAd splashAd;
    protected int requestTimeOut = 5;
    private static Application application;
    private  Class targetClass;
    private boolean isClick= false;

    @Override
    public void initAdSdk(Application application, String appId, boolean isDebug) {
        this.application = application;
        InitParams initParams = new InitParams.Builder()
                .setDebug(isDebug)
         //true打开SDK日志，当应用发布Release版本时，必须注释掉这行代码的调用，或者设为false
                .build();
        /**
         * 调用这行代码初始化广告SDK
         */
        MobAdManager.getInstance().init(application, appId, initParams);
        Log.e(TAG1, "initAdSdk: " + "initOk" );
    }

    @Override
    public void showAd(AdBean adBean) {
        this.mViewGroup = adBean.getViewGroup();
        this.mContext = adBean.getContext();
        this.targetClass = adBean.getTargetClass();
        switch (adBean.getAdType()){
            case AdType.BANNERTYPE:
                loadBannerAd(adBean.getBannerId(), adBean.getAdListener());
                break;
            case AdType.INTERSTIALTYPE:
                loadInterstialAd(adBean.getInterstitialId(), adBean.getAdListener());
                break;
            case AdType.SPLASHTYPE:
                loadSplashAd(adBean.getSplashId(), adBean.getAdListener());
            case AdType.FLOATTYPE:
                loadFloatAd();
                break;
        }

    }



    private void loadSplashAd(String splashId, final AdListener adListener) {
        /**
         * SplashAd初始化参数、这里可以设置获取广告最大超时时间，
         * 广告下面半屏的应用标题+应用描述
         * 注意：应用标题和应用描述是必传字段，不传将抛出异常
         */
        SplashAdParams splashAdParams = new SplashAdParams.Builder()
                .setFetchTimeout(requestTimeOut*1000)
                .setTitle(CommonUtils.getAppDisplayName(application))
                .setDesc("好用免费")
                .build();

//        splashAd = new SplashAd(mContext, splashId, this, splashAdParams);
        /**
         * 构造SplashAd对象
         * 注意：构造函数传入的几个形参都不能为空，否则将抛出NullPointerException异常。
         *
         */
        isClick = false;
        splashAd = new SplashAd(mContext, splashId, new ISplashAdListener() {
            @Override
            public void onAdDismissed() {
                Log.d(TAG1, "onAdDismissed");
                adListener.onDismiss(AdType.SPLASHTYPE);
                if (!isClick) {
                    CommonUtils.startOtherActivity(mContext,targetClass);
                }
            }

            @Override
            public void onAdShow() {
                Log.d(TAG1, "onAdShow");
                adListener.onAdExposure(AdType.SPLASHTYPE);
                //
                adListener.onLoaded(AdType.SPLASHTYPE);
            }

            @Override
            public void onAdFailed(String s) {
                Log.w(TAG1, "onAdFailed:" + s);
                adListener.onLoadFailed(AdType.SPLASHTYPE);
                CommonUtils.startOtherActivity(mContext,targetClass);
            }

            @Override
            public void onAdClick() {
                Log.d(TAG1, "onAdClick");
                adListener.onAdClick(AdType.SPLASHTYPE);
                isClick = true;
                CommonUtils.startOtherActivity(mContext,targetClass);
            }
        }, splashAdParams);
        //

    }


    @Override
    public void disPlayInterstitialAd() {
        Log.d(TAG1, "interstitialAd");
        if (isInterstitialAdReady) {
            Log.d(TAG1, "showAd");
            interstitialAd.showAd();
        } else {
            Log.e(TAG1, "interstitialAd not ready");
        }
    }


    private void loadInterstialAd(final String interstitialId, final AdListener adListener) {
        Log.d(TAG1, "onAdOpened");
        if (interstitialAd != null) {
            interstitialAd.destroyAd();
        }
        interstitialAd = new InterstitialAd(mContext, interstitialId);
        interstitialAd.setAdListener(new IInterstitialAdListener() {
            @Override
            public void onAdShow() {
                Log.d(TAG1, "onAdOpened");
                adListener.onAdExposure(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onAdFailed(String s) {
                Log.w(TAG1, "onAdError:" + s);
                adListener.onLoadFailed(AdType.INTERSTIALTYPE);
                isInterstitialAdReady = false;
                retryDelayForFailed += 2000; //延迟时间增加2秒

                //延迟一段时间后重新加载
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        interstitialAd.loadAd();
                    }
                }, retryDelayForFailed);
            }

            @Override
            public void onAdReady() {
                Log.d(TAG1, "onAdLoaded");
                adListener.onLoaded(AdType.INTERSTIALTYPE);
                isInterstitialAdReady = true;
                retryDelayForFailed = 0;
            }

            @Override
            public void onAdClick() {
                adListener.onAdClick(AdType.INTERSTIALTYPE);
                Log.d(TAG1, "onAdLeftApplication");

            }

            @Override
            public void onAdClose() {
                adListener.onCloseClick(AdType.INTERSTIALTYPE);
                Log.d(TAG1, "onAdClosed");
                isInterstitialAdReady = false;
                loadInterstialAd(interstitialId,adListener);
//                interstitialAd.loadAd();
            }
        });

        interstitialAd.loadAd();
    }

    private void loadFloatAd() {
    }

    private void loadBannerAd(String bannerId, final AdListener adListener) {
        bannerAd = new BannerAd(mContext, bannerId);
        bannerAd.setAdListener(new IBannerAdListener() {
            @Override
            public void onAdClose() {
                Log.d(TAG1, "onAdClosed");
                adListener.onCloseClick(AdType.BANNERTYPE);
            }

            @Override
            public void onAdShow() {
                Log.d(TAG1, "onAdLeftApplication");
                adListener.onAdExposure(AdType.BANNERTYPE);
            }

            @Override
            public void onAdFailed(String s) {
                Log.d(TAG1, "onAdFailedToLoad:" + s);
                adListener.onLoadFailed(AdType.BANNERTYPE);

            }

            @Override
            public void onAdReady() {
                Log.d(TAG1, "onAdLoaded");
                adListener.onLoaded(AdType.BANNERTYPE);
            }

            @Override
            public void onAdClick() {
                Log.d(TAG1, "onAdOpened");
                adListener.onAdClick(AdType.BANNERTYPE);
                //点击后刷新广告
                bannerAd.loadAd();
            }
        });
        View adView = bannerAd.getAdView();
        if (null != adView) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            this.mViewGroup.addView(adView, layoutParams);
        } else {
            Log.w(TAG1, "bannerAd.getAdView() return null");
        }
        bannerAd.loadAd();
    }

    @Override
    public void destoryAd() {
        if (bannerAd != null) {
            bannerAd.destroyAd();
        }
        if (interstitialAd != null) {
            interstitialAd.destroyAd();
        }
        if (splashAd != null) {
            splashAd.destroyAd();
        }
    }
}
