package com.lafonapps.facebookadscommon;

import android.app.Activity;
import android.app.Application;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeBannerAd;
import com.lafonapps.adadapter.AdBean;
import com.lafonapps.adadapter.AdType;
import com.lafonapps.adadapter.AdsAdapter;
import com.lafonapps.adadapter.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lafonapps.adadapter.AdType.SPLASHTYPE;
import com.lafonapps.facebookadscommon.R;

public class FacebookAdsManager implements AdsAdapter {
    private static final String TAG = FacebookAdsManager.class.getName();
    private ViewGroup mViewGroup;
    private Activity mContext;
    private int retryDelayForFailed;
    private String[] debugDevices;
    private AdView bannerAdView;
    private NativeBannerAd nativeBannerAd;
    private ViewGroup adView;
    private InterstitialAd interstitialAd;
    private NativeAd middelNativeAd;
    private NativeAd largeNativeAd;
    private NativeAd splashAd;;
    private Class targetClass;

    private boolean isShowSplashOk = false;


    @Override
    public void showAd(AdBean adBean) {
        Log.e(TAG,"come in showAd");
        mContext = adBean.getContext();
        mViewGroup = adBean.getViewGroup();
        targetClass = adBean.getTargetClass();
        AdSettings.clearTestDevices();
        AdSettings.addTestDevices(Arrays.asList(adBean.getTestDevices()));
        switch (adBean.getAdType()){
            case AdType.BANNERTYPE:
                loadBannerAd(adBean.getBannerId(), adBean.getAdListener());
                break;
            case AdType.NATIVEBANNERTYPE:
                loadNativeBannerAd(adBean.getNativeBannerId(), adBean.getAdListener());
                break;
            case AdType.INTERSTIALTYPE:
                loadInterstialAd(adBean.getInterstitialId(), adBean.getAdListener());
                break;
            case AdType.NATIVEMTYPE:
                loadMiddleNativeAd( adBean.getNativeMId(), adBean.getAdListener());
                break;
            case AdType.NATIVELTYPE:
                loadBigNativeAd(adBean.getNativeLId(), adBean.getAdListener());
                break;
            case SPLASHTYPE:
                loadSplashAd(adBean.getSplashId(), adBean.getAdListener());
                break;
            case AdType.FLOATTYPE:
                break;

        }
    }

    @Override
    public void disPlayInterstitialAd() {
        if (interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        } else {
            Log.d(TAG, "interstitialAd not ready");
        }
    }

    /*加载bannerView*/
    private void loadBannerAd(String adID, final com.lafonapps.adadapter.AdListener adListener){
        bannerAdView = new AdView(mContext, adID, AdSize.BANNER_HEIGHT_50);
        // Add the ad view to your activity layout
        mViewGroup.addView(bannerAdView);
        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "onError: "+ adError.getErrorMessage());
                adListener.onLoadFailed(AdType.BANNERTYPE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                adListener.onLoaded(AdType.BANNERTYPE);
            }

            @Override
            public void onAdClicked(Ad ad) {
                adListener.onAdClick(AdType.BANNERTYPE);
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        // Request an ad
        bannerAdView.loadAd();
    }

    /*加载nativeBannerView*/
    private void loadNativeBannerAd(String adID, final com.lafonapps.adadapter.AdListener adListener){
        nativeBannerAd = new NativeBannerAd(mContext, adID);
        nativeBannerAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                adListener.onLoadFailed(AdType.NATIVEBANNERTYPE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                if (nativeBannerAd == null || nativeBannerAd != ad) {
                    return;
                }
                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
                adListener.onLoaded(AdType.NATIVEBANNERTYPE);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
                adListener.onAdClick(AdType.NATIVEBANNERTYPE);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        });

        // load the ad
        nativeBannerAd.loadAd();
    }
    //原生横幅广告的构建
    private void inflateAd(NativeBannerAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_unit, mViewGroup, false);
        mViewGroup.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeBannerAd, true);
        adChoicesContainer.addView(adChoicesView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById( R.id.native_ad_sponsored_label);
        AdIconView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        nativeAdMedia.setListener(new MediaViewListener() {
            @Override
            public void onPlay(MediaView mediaView) {
                displayTimer.cancel(); //暂停倒计时
            }

            @Override
            public void onVolumeChange(MediaView mediaView, float v) {

            }

            @Override
            public void onPause(MediaView mediaView) {

            }

            @Override
            public void onComplete(MediaView mediaView) {
                displayTimer.start();
            }

            @Override
            public void onEnterFullscreen(MediaView mediaView) {

            }

            @Override
            public void onExitFullscreen(MediaView mediaView) {

            }

            @Override
            public void onFullscreenBackground(MediaView mediaView) {

            }

            @Override
            public void onFullscreenForeground(MediaView mediaView) {

            }
        });

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }

    /**加载interstialAdView*/
    private void loadInterstialAd(String adID, final com.lafonapps.adadapter.AdListener adListener){
        interstitialAd = new InterstitialAd(mContext, adID);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Log.d(TAG, "onInterstitialDisplayed");
                adListener.onAdExposure(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.d(TAG, "onInterstitialDismissed");
                adListener.onDismiss(AdType.INTERSTIALTYPE);
                loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(TAG, "onError:" + adError.getErrorMessage());
                adListener.onLoadFailed(AdType.INTERSTIALTYPE);
                retryDelayForFailed += 2000; //延迟时间增加2秒

                //延迟一段时间后重新加载
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAd();
                    }
                }, retryDelayForFailed);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "onAdLoaded");
                retryDelayForFailed = 0;
                adListener.onLoaded(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(TAG, "onAdClicked");
                adListener.onAdClick(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        loadAd();
    }


    /** 加载插屏广告 */
    private void loadAd() {
        this.interstitialAd.loadAd();
    }

    /**加载loadMiddleNativeAd*/
    private void loadMiddleNativeAd(String adID, final com.lafonapps.adadapter.AdListener adListener){
        middelNativeAd = new NativeAd(mContext, adID);
        middelNativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "onError: " + adError.getErrorMessage());
                adListener.onLoadFailed(AdType.NATIVEMTYPE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                adListener.onLoaded(AdType.NATIVEMTYPE);
                View adView = NativeAdView.render(mContext, middelNativeAd, NativeAdView.Type.HEIGHT_300);
                // Add the Native Ad View to your ad container
                mViewGroup.addView(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {
                adListener.onAdClick(AdType.NATIVEMTYPE);
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        // Initiate a request to load an ad.
        middelNativeAd.loadAd();
    }

    /**加载loadBigNativeAd*/
    private void loadBigNativeAd(String adID, final com.lafonapps.adadapter.AdListener adListener){
        largeNativeAd = new NativeAd(mContext, adID);
        largeNativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "onError: " + adError.getErrorMessage());
                adListener.onLoadFailed(AdType.NATIVELTYPE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                adListener.onLoaded(AdType.NATIVELTYPE);
                View adView = NativeAdView.render(mContext, largeNativeAd, NativeAdView.Type.HEIGHT_400);
                // Add the Native Ad View to your ad container
                mViewGroup.addView(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {
                adListener.onAdClick(AdType.NATIVELTYPE);
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        // Initiate a request to load an ad.
        largeNativeAd.loadAd();    }

    private Button skipButton;
    protected int displayDuration = 6;
    protected int requestTimeOut = 5;
    private CountDownTimer displayTimer = new CountDownTimer(displayDuration * 1000, 1000) {
        @Override
        public void onTick(long l) {
            displayDuration--;
            Log.d(TAG, "Display countdown = " + displayDuration);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "displayTimer finish");
            dismissSplashAd();
        }
    };
    private CountDownTimer requestTimer = new CountDownTimer(requestTimeOut * 1000, 1000) {
        @Override
        public void onTick(long l) {
            requestTimeOut--;
            Log.d(TAG, "Request countdown = " + requestTimeOut);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "requestTimer finish");
            //
            dismissSplashAd();
        }
    };

    private void loadSplashAd(String adID, final com.lafonapps.adadapter.AdListener adListener){
        Log.e(TAG,"come in loadSplashAd");
        splashAd = new NativeAd(mContext, adID);
        isShowSplashOk = false;
        splashAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                Log.d(TAG, "onMediaDownloaded:");

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(TAG, "onAdFailedToLoad:" + adError.getErrorMessage());
                dismissSplashAd();
                adListener.onLoadFailed(AdType.SPLASHTYPE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "onAdLoaded");
                adListener.onAdExposure(AdType.SPLASHTYPE);
                requestTimer.cancel();
                displayTimer.start();
                if (splashAd == ad) {
                    // Inflate Native Ad into Container
                    setupSplashAd(splashAd);
                    mViewGroup.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(TAG, "onAdClosed");
                adListener.onAdClick(AdType.SPLASHTYPE);
                dismissSplashAd();

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "onLoggingImpression");
            }
        });


        splashAd.loadAd();
        requestTimer.start();

    }


    private void setupSplashAd(NativeAd splashAd) {
        splashAd.unregisterView();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        View adView = (LinearLayout) inflater.inflate(R.layout.splash_native_ad_facebook, mViewGroup, false);
        skipButton = adView.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissSplashAd();
            }
        });
        mViewGroup.addView(adView);

        // Add the AdChoices icon
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(mContext, splashAd, true);
        adChoicesContainer.addView(adChoicesView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        nativeAdMedia.setListener(new MediaViewListener() {
            @Override
            public void onPlay(MediaView mediaView) {
                displayTimer.cancel(); //暂停倒计时
            }

            @Override
            public void onVolumeChange(MediaView mediaView, float v) {

            }

            @Override
            public void onPause(MediaView mediaView) {

            }

            @Override
            public void onComplete(MediaView mediaView) {
                displayTimer.start();
            }

            @Override
            public void onEnterFullscreen(MediaView mediaView) {

            }

            @Override
            public void onExitFullscreen(MediaView mediaView) {

            }

            @Override
            public void onFullscreenBackground(MediaView mediaView) {

            }

            @Override
            public void onFullscreenForeground(MediaView mediaView) {

            }
        });
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        // Set the Text.
        nativeAdTitle.setText(splashAd.getAdvertiserName());
        nativeAdBody.setText(splashAd.getAdBodyText());
        nativeAdSocialContext.setText(splashAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(splashAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(splashAd.getAdCallToAction());
        sponsoredLabel.setText(splashAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        splashAd.registerViewForInteraction(
                mViewGroup,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }

    private void dismissSplashAd() {
        if (!isShowSplashOk) {
            CommonUtils.startOtherActivity(mContext,targetClass);
            isShowSplashOk = true;
        }

    }


    @Override
    public void initAdSdk(Application application, String appId, boolean isDebug) {

    }

    @Override
    public void destoryAd() {
        requestTimer.cancel();
        displayTimer.cancel();
        if (bannerAdView != null) {
            bannerAdView.destroy();
        }
        if (nativeBannerAd != null) {
            nativeBannerAd.destroy();
        }
        if (middelNativeAd != null){
            middelNativeAd.destroy();
        }
        if (largeNativeAd != null){
            largeNativeAd.destroy();
        }
        if (interstitialAd != null){
            interstitialAd.destroy();
        }
        if (splashAd!=null) {
            splashAd.destroy();
        }

    }
}
