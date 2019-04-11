package com.lafonapps.tuiaadscommon;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.db.ta.sdk.TMAwView;
import com.db.ta.sdk.TMBrTmView;
import com.db.ta.sdk.TMItTm;
import com.db.ta.sdk.TMNaTmView;
import com.db.ta.sdk.TMShTmListener;
import com.db.ta.sdk.TMShTmView;
import com.db.ta.sdk.TaSDK;
import com.db.ta.sdk.TmListener;
import com.lafonapps.adadapter.AdBean;
import com.lafonapps.adadapter.AdListener;
import com.lafonapps.adadapter.AdType;
import com.lafonapps.adadapter.AdsAdapter;
import com.lafonapps.tuiaadscommon.R;

import static com.lafonapps.adadapter.AdType.SPLASHTYPE;

public class  TuiAAdsManager  implements AdsAdapter {

    private TMBrTmView mSmallBannerView;
    private TMItTm mInterstialAd;
    private TMNaTmView mSmallNewsFeedView;
    private TMNaTmView mBigNewsFeedView;
    private TMAwView mfloatAdView;
    private TMShTmView mSplashView;
    private ViewGroup mViewGroup;
    private Activity mContext;

    public void loadSplashView(String adID, final AdListener adListener, Class targetClass){
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.splash_view,mViewGroup);
        mSplashView =  view.findViewById(R.id.splashView);
        mSplashView.setTargetClass(mContext,targetClass);
        mSplashView.setAdListener(new TMShTmListener(){
            @Override
            public void onTimeOut() {
                Log.d("TMShActivity", "onTimeOut");
                adListener.onTimeOut(AdType.SPLASHTYPE);
            }

            @Override
            public void onReceiveAd() {
                Log.d("TMShActivity", "onReceiveAd");
                adListener.onLoaded(AdType.SPLASHTYPE);
            }

            @Override
            public void onFailedToReceiveAd() {
                Log.d("TMShActivity", "onFailedToReceiveAd");
                adListener.onLoadFailed(AdType.SPLASHTYPE);
            }

            @Override
            public void onLoadFailed() {
                Log.d("TMShActivity", "onLoadFailed");
                adListener.onLoadFailed(AdType.SPLASHTYPE);
            }

            @Override
            public void onCloseClick() {
                Log.d("TMShActivity", "onCloseClick");
                adListener.onCloseClick(AdType.SPLASHTYPE);
            }

            @Override
            public void onAdClick() {
                Log.d("TMShActivity", "onClick");
                adListener.onAdClick(AdType.SPLASHTYPE);
            }

            @Override
            public void onAdExposure() {
                Log.d("TMShActivity", "onAdExposure");
                adListener.onAdExposure(AdType.SPLASHTYPE);
            }
        });
        mSplashView.loadAd(Integer.valueOf(adID));
    }


    /*加载smallBannerView*/
    private void loadSmallBannerAd(String adID, final AdListener adListener){
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.small_banner_view,mViewGroup);
        mSmallBannerView = view.findViewById(R.id.SmallBannerView);
        mSmallBannerView.setAdListener(new TmListener() {
            @Override
            public void onReceiveAd() {
                Log.d("========", "onReceivesmallBannerAd");
                adListener.onLoaded(AdType.BANNERTYPE);
            }

            @Override
            public void onFailedToReceiveAd() {
                Log.d("========", "onFailedToReceivesmallBannerAd");
                adListener.onLoadFailed(AdType.BANNERTYPE);
            }

            @Override
            public void onLoadFailed() {
                Log.d("========", "onLoadsmallBannerAdFailed");
                adListener.onLoadFailed(AdType.BANNERTYPE);
            }

            @Override
            public void onCloseClick() {
                Log.d("========", "onCloseClicksmallBannerAd");
                adListener.onCloseClick(AdType.BANNERTYPE);
            }

            @Override
            public void onAdClick() {
                Log.d("========", "onsmallBannerAdClick");
                adListener.onAdClick(AdType.BANNERTYPE);
            }

            @Override
            public void onAdExposure() {
                Log.d("========", "onExposure");
                adListener.onAdExposure(AdType.BANNERTYPE);
            }
        });
        mSmallBannerView.loadAd(Integer.valueOf(adID));
    }

    /*加载interstialAdView*/
    private void loadInterstialAd(String adID, final AdListener adListener){
        mInterstialAd = new TMItTm(mContext);
        mInterstialAd.setAdListener(new TmListener() {
            @Override
            public void onReceiveAd() {
                Log.d("========", "onReceiveInterstialAdView");
                adListener.onLoaded(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onFailedToReceiveAd() {
                Log.d("========", "onFailedToReceiveInterstialAdView");
                adListener.onLoadFailed(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onLoadFailed() {
                Log.d("========", "onLoadFailedInterstialAdView");
                adListener.onLoadFailed(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onCloseClick() {
                Log.d("========", "onCloseClickInterstialAdView");
                adListener.onCloseClick(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onAdClick() {
                Log.d("========", "onAdClickInterstialAdView");
                adListener.onAdClick(AdType.INTERSTIALTYPE);
            }

            @Override
            public void onAdExposure() {
                Log.d("========", "onAdExposure");
                adListener.onAdExposure(AdType.INTERSTIALTYPE);
            }
        });
        mInterstialAd.loadAd(Integer.valueOf(adID));
    }

    /*加载SmallNewsFeedAd*/
    private void loadSmallNewsFeedAd(final String adID, final AdListener adListener){
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.news_feed_small_view,mViewGroup);
        mSmallNewsFeedView = view.findViewById(R.id.newsFeedSmallView);
        mSmallNewsFeedView.setAdListener(new TmListener() {
            @Override
            public void onReceiveAd() {
                adListener.onLoaded(AdType.NATIVESTYPE);
            }

            @Override
            public void onFailedToReceiveAd() {
                adListener.onLoadFailed(AdType.NATIVESTYPE);
            }

            @Override
            public void onLoadFailed() {
                adListener.onLoadFailed(AdType.NATIVESTYPE);
            }

            @Override
            public void onCloseClick() {
                adListener.onCloseClick(AdType.NATIVESTYPE);
            }

            @Override
            public void onAdClick() {
                adListener.onAdClick(AdType.NATIVESTYPE);
            }

            @Override
            public void onAdExposure() {
                adListener.onAdExposure(AdType.NATIVESTYPE);
            }
        });
        mSmallNewsFeedView.loadAd(Integer.valueOf(adID));
    }

    /*加载SmallNewsFeedAd*/
    private void loadBigNewsFeedAd(String adID, final AdListener adListener){
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.news_feed_big_view,mViewGroup);
        mBigNewsFeedView = view.findViewById(R.id.newsFeedBigView);
        mBigNewsFeedView.setAdListener(new TmListener() {
            @Override
            public void onReceiveAd() {
                adListener.onLoaded(AdType.NATIVELTYPE);
            }

            @Override
            public void onFailedToReceiveAd() {
                adListener.onLoadFailed(AdType.NATIVELTYPE);
            }

            @Override
            public void onLoadFailed() {
                adListener.onLoadFailed(AdType.NATIVELTYPE);
            }

            @Override
            public void onCloseClick() {
                adListener.onCloseClick(AdType.NATIVELTYPE);
            }

            @Override
            public void onAdClick() {
                adListener.onAdClick(AdType.NATIVELTYPE);
            }

            @Override
            public void onAdExposure() {
                adListener.onAdExposure(AdType.NATIVELTYPE);
            }
        });
        mBigNewsFeedView.loadAd(Integer.valueOf(adID));
    }

    /*加载SmallNewsFeedAd*/
    private void loadfloatAd(final String adID, final AdListener adListener){
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.float_view,mViewGroup);
        mfloatAdView = view.findViewById(R.id.floatAdView);
        mfloatAdView.setAdListener(new TmListener() {
            @Override
            public void onReceiveAd() {
                adListener.onLoaded(AdType.FLOATTYPE);
            }

            @Override
            public void onFailedToReceiveAd() {
                adListener.onLoadFailed(AdType.FLOATTYPE);
            }

            @Override
            public void onLoadFailed() {
                adListener.onLoadFailed(AdType.FLOATTYPE);
            }

            @Override
            public void onCloseClick() {
                adListener.onCloseClick(AdType.FLOATTYPE);
            }

            @Override
            public void onAdClick() {
                adListener.onAdClick(AdType.FLOATTYPE);
                mfloatAdView.loadAd(Integer.valueOf(adID));
            }

            @Override
            public void onAdExposure() {
                adListener.onAdExposure(AdType.FLOATTYPE);
            }
        });
        mfloatAdView.loadAd(Integer.valueOf(adID));
    }

    @Override
    public void destoryAd() {
        if (mSmallBannerView != null){
            mSmallBannerView.destroy();
        }
        if (mInterstialAd != null){
            mInterstialAd.destroy();
        }
        if (mSmallNewsFeedView != null){
            mSmallNewsFeedView.destroy();
        }
        if (mBigNewsFeedView != null){
            mBigNewsFeedView.destroy();
        }
        if (mfloatAdView != null){
            mfloatAdView.destroy();
        }
        if (mSplashView != null){
            mSplashView.destroy();
        }
    }


    @Override
    public void initAdSdk(Application application ,String appId, boolean isDebug) {
        TaSDK.init(application);
    }

    @Override
    public void showAd(AdBean adBean) {
        mContext = adBean.getContext();
        mViewGroup = adBean.getViewGroup();
        switch (adBean.getAdType()){
            case AdType.BANNERTYPE:
                loadSmallBannerAd(adBean.getBannerId(), adBean.getAdListener());
                Log.d("========", "onReceivesmallBannerAd");
                break;
            case AdType.INTERSTIALTYPE:
                loadInterstialAd(adBean.getInterstitialId(), adBean.getAdListener());
                break;
            case AdType.NATIVESTYPE:
                loadSmallNewsFeedAd(adBean.getNativeSId(), adBean.getAdListener());
                break;
            case AdType.NATIVELTYPE:
                loadBigNewsFeedAd(adBean.getNativeLId(),adBean.getAdListener());
                break;
            case AdType.FLOATTYPE:
                loadfloatAd(adBean.getFloatId(), adBean.getAdListener());
                break;
            case SPLASHTYPE:
                loadSplashView(adBean.getSplashId(), adBean.getAdListener(),adBean.getTargetClass());
                break;

        }
    }

    @Override
    public void disPlayInterstitialAd() {

    }
}
