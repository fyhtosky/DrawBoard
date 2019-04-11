package com.lafonapps.admobadscommon;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.lafonapps.adadapter.AdBean;
import com.lafonapps.adadapter.AdType;
import com.lafonapps.adadapter.AdsAdapter;
import com.lafonapps.adadapter.utils.AdSize;
import com.lafonapps.adadapter.utils.CommonUtils;

import static com.lafonapps.adadapter.AdType.SPLASHTYPE;

public class AdmobAdsManager implements AdsAdapter {
    private static final String TAG = AdmobAdsManager.class.getCanonicalName();
    private AdView bannerAdView;
    private InterstitialAd interstitialAdView;
    private NativeExpressAdView nativeExpressAdView;
    private NativeExpressAdView nativeExpressAd132HView;
    private NativeExpressAdView nativeExpressAd250HView;
    private NativeExpressAdView splashAdView;
    private ViewGroup mViewGroup;
    private Activity mContext;
    private int retryDelayForFailed;
    private String[] testDevices;
    private Class targetClass;
    @Override
    public void showAd(AdBean adBean) {
        mContext = adBean.getContext();
        mViewGroup = adBean.getViewGroup();
        testDevices = adBean.getTestDevices();
        targetClass = adBean.getTargetClass();
        switch (adBean.getAdType()){
            case AdType.BANNERTYPE:
                loadBannerAd(adBean.getBannerId(), adBean.getAdListener());
                break;
            case AdType.INTERSTIALTYPE:
                loadInterstialAd(adBean.getInterstitialId(), adBean.getAdListener());
                break;
            case AdType.NATIVESTYPE:
                loadSmallNativeAd(new AdSize(320, 80, mContext), adBean.getNativeSId(), adBean.getAdListener());
                break;
            case AdType.NATIVEMTYPE:
                loadMiddleNativeAd(new AdSize(320, 132, mContext), adBean.getNativeMId(), adBean.getAdListener());
                break;
            case AdType.NATIVELTYPE:
                loadBigNativeAd(new AdSize(320, 250, mContext), adBean.getNativeLId(), adBean.getAdListener());
                break;
            case SPLASHTYPE:
                loadSplashAd(adBean.getSplashId(), adBean.getAdListener());
            case AdType.FLOATTYPE:
                loadFloatAd();
                break;

        }
    }

    @Override
    public void disPlayInterstitialAd() {

    }

    /*加载bannerView*/
    private void loadBannerAd(String idID, final com.lafonapps.adadapter.AdListener adListener){
        bannerAdView = new AdView(mContext);
        bannerAdView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
        bannerAdView.setAdUnitId(idID);
        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
                adListener.onCloseClick(AdType.BANNERTYPE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad:" + i);
                adListener.onLoadFailed(AdType.BANNERTYPE);

            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
                adListener.onAdClick(AdType.BANNERTYPE);
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
                adListener.onAdExposure(AdType.BANNERTYPE);
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                adListener.onLoaded(AdType.BANNERTYPE);
            }
        });
        this.mViewGroup.addView(bannerAdView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bannerAdView.loadAd(loadAdmob());
    }


    /*加载interstialAdView*/
    private void loadInterstialAd(String idID, final com.lafonapps.adadapter.AdListener adListener){
        interstitialAdView = new InterstitialAd(mContext);
        interstitialAdView.setAdUnitId(idID);
        interstitialAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
                adListener.onCloseClick(AdType.INTERSTIALTYPE);
                adListener.onDismiss(AdType.INTERSTIALTYPE);
                loadAd();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad:" + i);
                adListener.onLoadFailed(AdType.INTERSTIALTYPE);
                 retryDelayForFailed += 2000;//延迟时间增加2秒
                //延迟一段时间后重新加载
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAd();
                    }
                }, retryDelayForFailed);
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
                adListener.onAdClick(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
                adListener.onAdExposure(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                adListener.onLoaded(AdType.INTERSTIALTYPE);
                retryDelayForFailed = 0;
            }
        });

        loadAd();
        showInterstialAdView(mContext);
    }


    /* 加载广告 */
    private void loadAd() {

        this.interstitialAdView.loadAd(loadAdmob());
    }

    public void showInterstialAdView(Activity activity) {
        if (interstitialAdView != null && interstitialAdView.isLoaded()) {
            interstitialAdView.show();
        } else {
            Log.d(TAG, "interstitialAd not ready");
        }
    }


    /*加载smallNativeAd*/
    private void loadSmallNativeAd(AdSize adSize, String nativeAdID, com.lafonapps.adadapter.AdListener adListener){
        nativeExpressAdView = new NativeExpressAdView(mContext);
        loadNativeAd(adSize, nativeAdID, this.nativeExpressAdView, adListener,AdType.NATIVESTYPE);
    }

    /*加载loadMiddleNativeAd*/
    private void loadMiddleNativeAd(AdSize adSize, String nativeAdID, com.lafonapps.adadapter.AdListener adListener){
        nativeExpressAd132HView = new NativeExpressAdView(mContext);
        loadNativeAd(adSize, nativeAdID, this.nativeExpressAd132HView, adListener,AdType.NATIVEMTYPE);
    }

    /*加载loadBigNativeAd*/
    private void loadBigNativeAd(AdSize adSize, String nativeAdID, com.lafonapps.adadapter.AdListener adListener){
        nativeExpressAd250HView = new NativeExpressAdView(mContext);
        loadNativeAd(adSize, nativeAdID, this.nativeExpressAd250HView, adListener,AdType.NATIVELTYPE);
    }


    /*加载nativeAd*/
    private void loadNativeAd(AdSize adSize, String nativeAdID, NativeExpressAdView nativeView, final com.lafonapps.adadapter.AdListener adListener, final int adType){
        nativeView.setAdSize(new com.google.android.gms.ads.AdSize(adSize.getWidth(), adSize.getHeight()));
        nativeView.setAdUnitId(nativeAdID);
        nativeView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
                adListener.onCloseClick(adType);
                adListener.onDismiss(adType);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad:" + i);
                adListener.onLoadFailed(adType);
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
                adListener.onAdClick(adType);
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
                adListener.onAdExposure(adType);
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                adListener.onLoaded(adType);
            }
        });

        Log.e(TAG, "loadNativeAd: this.mViewGroup.addView");
        this.mViewGroup.addView(nativeView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nativeView.loadAd(loadAdmob());

    }

    private void loadSplashAd(String adID, final com.lafonapps.adadapter.AdListener adListener){
        splashAdView = new NativeExpressAdView(mContext);
        int dpWidth = new AdSize(mContext).px2dp(mViewGroup.getWidth());
        int dpHeight = new AdSize(mContext).px2dp(mViewGroup.getHeight());
        splashAdView.setAdSize(new com.google.android.gms.ads.AdSize(dpWidth, dpHeight));
        splashAdView.setAdUnitId(adID);
        splashAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
                adListener.onCloseClick(AdType.SPLASHTYPE);
                adListener.onDismiss(AdType.SPLASHTYPE);
                CommonUtils.startOtherActivity(mContext,targetClass);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad:" + i);
                adListener.onLoadFailed(AdType.SPLASHTYPE);
//                dismissSplashAd();
                CommonUtils.startOtherActivity(mContext,targetClass);
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
                adListener.onAdClick(AdType.SPLASHTYPE);
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
                adListener.onAdExposure(AdType.SPLASHTYPE);
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                adListener.onLoaded(AdType.SPLASHTYPE);
            }
        });

        mViewGroup.addView(splashAdView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        splashAdView.loadAd(loadAdmob());

    }

    private AdRequest loadAdmob() {
        AdRequest.Builder requestBuilder = new AdRequest.Builder();
        for (String testDevice : testDevices) {
            requestBuilder.addTestDevice(testDevice);
        }
        requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        return  requestBuilder.build();
    }


    /*加载loadfloatAd*/
    private void loadFloatAd(){

    }

    @Override
    public void initAdSdk(Application application, String appId, boolean isDebug) {
        MobileAds.initialize(application, appId);
    }

    @Override
    public void destoryAd() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
        }

        if (nativeExpressAdView != null) {
            nativeExpressAdView.destroy();
        }
        if (nativeExpressAd132HView != null) {
            nativeExpressAd132HView.destroy();
        }
        if (nativeExpressAd250HView != null) {
            nativeExpressAd250HView.destroy();
        }
        if (splashAdView != null) {
            splashAdView.destroy();
        }
    }
}
