package com.lafonapps.xiaomiad;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lafonapps.adadapter.AdBean;
import com.lafonapps.adadapter.AdListener;
import com.lafonapps.adadapter.AdsAdapter;
import com.lafonapps.adadapter.utils.CommonUtils;
import com.miui.zeus.mimo.sdk.MimoSdk;
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.FloatAd;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.ad.IVideoAdWorker;
import com.miui.zeus.mimo.sdk.api.IMimoSdkListener;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.miui.zeus.mimo.sdk.listener.MimoVideoListener;
import com.xiaomi.ad.common.pojo.AdType;

import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import static com.lafonapps.adadapter.utils.CommonUtils.startOtherActivity;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/08/07
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class XiaoMiManager implements AdsAdapter {
    // 以下两个没有的话就按照以下传入
    private String APPKEY_XIAOMI = "fake_app_key";
    private String APPTOKEN_XIAOMI = "fake_app_token";
    private ViewGroup mContainer;
    private IAdWorker mWorker;
    private AdType adType;
    private IVideoAdWorker mVideoAdWorker;
    private static final String TAG = XiaoMiManager.class.getName();
    public static final int NATIVEBANNERTYPE =1;  //
    public static final int NATIVESTYPE =2;
    public static final int NATIVEMTYPE =3;
    public static final int NATIVELTYPE =4;
    public static final int INTERSTIALTYPE =5;
    public static final int FLOATTYPE =6;
    public static final int VIDEOTYPE =7;
    public static final int SPLASHTYPE =8;
    public static final int BANNERTYPE =9;
    private boolean isInterstialAd = false;
    private int retryDelayForFailed;
//    private volatile static XiaoMiManager xiaoMiManager;
//
//    private XiaoMiManager() {
//        xiaoMiManager = new XiaoMiManager();
//    }
//
//    ;
//
//    public static XiaoMiManager getInstance() {
//        if (xiaoMiManager == null) {
//            synchronized (XiaoMiManager.class) {
//                if (xiaoMiManager == null) {
//                    xiaoMiManager = new XiaoMiManager();
//                }
//            }
//        }
//        return xiaoMiManager;
//    }


    @Override
    public void initAdSdk(Application application, String appId, boolean isDebug) {
        if (false) {
            MimoSdk.setDebugOn();
            // 正式上线时候务必关闭stage
            MimoSdk.setStageOn();
        }
        MimoSdk.init(application, appId, APPKEY_XIAOMI, APPTOKEN_XIAOMI, new IMimoSdkListener() {
            @Override
            public void onSdkInitSuccess() {

            }

            @Override
            public void onSdkInitFailed() {

            }
        });
        Log.i(TAG, CommonUtils.getAppDisplayName(application));
    }

    @Override
    public void showAd(final AdBean adBean) {
        String adId = "";
        if (adBean!=null) {
            final Activity context = adBean.getContext();
            final AdListener adListener = adBean.getAdListener();
            final Class targetClass = adBean.getTargetClass();
            int gravity = adBean.getGravity();
            final int adSign = adBean.getAdType() ;
            switch (adSign) {
                //竖屏开屏
                case SPLASHTYPE:
                    mContainer = adBean.getViewGroup();
                    adId = adBean.getSplashId();
                    adType = AdType.AD_SPLASH;
                    break;
                //信息流大图
                case NATIVELTYPE:
                    mContainer = adBean.getViewGroup();
                    adId = adBean.getNativeLId();
                    adType = AdType.AD_STANDARD_NEWSFEED;
                    break;
                //信息流小图
                case NATIVESTYPE:
                    mContainer = adBean.getViewGroup();
                    adId = adBean.getNativeSId();
                    adType = AdType.AD_STANDARD_NEWSFEED;
                    break;
                //信息流组图
                case NATIVEMTYPE:
                    mContainer = adBean.getViewGroup();
                    adId = adBean.getNativeMId();
                    adType = AdType.AD_STANDARD_NEWSFEED;
                    break;
                //横幅广告
                case BANNERTYPE:
                    mContainer = adBean.getViewGroup();
                    adId = adBean.getBannerId();
                    adType = AdType.AD_BANNER;
//                    mContainer = (ViewGroup) LayoutInflater.from(adBean.getContext()).inflate(R.layout.nativemiview, null, false);
//                    RelativeLayout bannerView = mContainer.findViewById(R.id.rl_ad);
//                    ImageView iv_close = mContainer.findViewById(R.id.iv_adclose);
//                    bannerView.addView(adBean.getViewGroup(),ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//                    iv_close.setVisibility(View.GONE);
//                    iv_close.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mContainer.setVisibility(View.GONE);
//                        }
//                    });
                    break;
                //悬浮广告
                case FLOATTYPE:
                    mContainer = (ViewGroup) context.getWindow().getDecorView();
                    adId = adBean.getFloatId();
                    adType = AdType.AD_FLOAT_AD;
                    break;
                //插屏广告
                case INTERSTIALTYPE:
                    mContainer = (ViewGroup) context.getWindow().getDecorView();
                    adId = adBean.getInterstitialId();
                    adType = AdType.AD_INTERSTITIAL;
                    isInterstialAd = true;
                    break;
                //贴片视屏广告
                case VIDEOTYPE:
                    mContainer = adBean.getViewGroup();
                    adId = adBean.getVideoId();
                    adType = AdType.AD_PLASTER_VIDEO;
                    break;
                default:
                    break;
            }
           final String tempId = adId;
            if (adSign == VIDEOTYPE) {
                try {
                    mVideoAdWorker = AdWorkerFactory.getVideoAdWorker(context, adId, adType);
                    mVideoAdWorker.setListener(new MimoVideoListener() {
                        @Override
                        public void onVideoStart() {
                            Log.e(TAG, "Video is start");
                        }

                        @Override
                        public void onVideoPause() {
                            Log.e(TAG, "Video is pause");

                        }

                        @Override
                        public void onVideoComplete() {
                            Log.e(TAG, "Video is complete");
                        }

                        @Override
                        public void onAdPresent() {
                            Log.e(TAG, "ad present in");
                            adListener.onAdExposure(adSign);
                            //

                        }

                        @Override
                        public void onAdClick() {
                            Log.e(TAG, "ad is clicked");
                            adListener.onAdClick(adSign);

                        }

                        @Override
                        public void onAdDismissed() {
                            Log.e(TAG, "ad is dismissed");
                            adListener.onDismiss(adSign);
                        }

                        @Override
                        public void onAdFailed(String s) {
                            Log.e(TAG, "ad load failed");
                            adListener.onLoadFailed(adSign);
                        }

                        @Override
                        public void onAdLoaded(int i) {
                            adListener.onLoaded(adSign);
                            Log.e(TAG, "ad is loaded");
                        }

                        @Override
                        public void onStimulateSuccess() {

                        }
                    });
                    mVideoAdWorker.recycle();
                    if (!mVideoAdWorker.isReady()) {
                        mVideoAdWorker.load();
                    }
                    if (mVideoAdWorker.isReady()) {
                        mContainer.removeAllViews();
                    }
                    new Handler().postDelayed(new Runnable(){

                        public void run() {
                            try {
                                // show 耗时，不要放在主线程
                                mVideoAdWorker.show(mContainer);
                                mVideoAdWorker.play();
                            } catch (Exception e) {
                            }

                        }

                    }, 1000 * 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mWorker = AdWorkerFactory.getAdWorker(context, mContainer, new MimoAdListener() {
                        @Override
                        public void onAdPresent() {
                            // 开屏广告展示
                            //加载关闭
                            com.lafonapps.adadapter.AdType.BANNERCLOSE = true;
                            adListener.onAdExposure(adSign);
                            Log.d(TAG, "onAdPresent");
                        }

                        @Override
                        public void onAdClick() {
                            //用户点击了开屏广告
                            Log.d(TAG, "onAdClick");
                            adListener.onAdClick(adSign);
                            if (adSign == SPLASHTYPE) {
                                startOtherActivity(context, targetClass);
                            }

                        }

                        @Override
                        public void onAdDismissed() {
                            //这个方法被调用时，表示广告消失。
                            Log.d(TAG, "onAdDismissed");
                            adListener.onDismiss(adSign);
                            if (adSign == SPLASHTYPE) {
                                startOtherActivity(context, targetClass);
                            }
                            if (adSign == INTERSTIALTYPE) {
                                try {
//                                    mWorker.load(tempId);
                                    showAd(adBean);
                                    Log.d(TAG, "onAdDismissed  INTERSTIALTYPE");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "onAdDismissed  error");
                                }
                            }
                        }

                        @Override
                        public void onAdFailed(String s) {
                            Log.e(TAG, "ad fail message : " + s);
                            adListener.onLoadFailed(adSign);
                            if (adSign == SPLASHTYPE) {
                                startOtherActivity(context, targetClass);
                            }
                            if (adSign == INTERSTIALTYPE) {
                                retryDelayForFailed += 2000; //延迟时间增加2秒

                                //延迟一段时间后重新加载
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mWorker.load(tempId);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, retryDelayForFailed);
                            }
                        }

                        @Override
                        public void onAdLoaded(int i) {
                            Log.e(TAG, "onAdLoaded");
                            retryDelayForFailed = 0;
                            adListener.onLoaded(adSign);
                        }

                        @Override
                        public void onStimulateSuccess() {

                        }

                    }, adType);
                    if (adSign!=INTERSTIALTYPE) {
                        //悬浮广告位置设定
                        if (adSign == FLOATTYPE) {
                            if (gravity == 0) {
                                gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                            }
                            FloatAd.setGravity(gravity);
                        }
                        if (MimoSdk.isSdkReady()){
                            mWorker.loadAndShow(adId);
                        }

                    } else {
                        if (MimoSdk.isSdkReady()){
                            mWorker.load(adId);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (adSign == SPLASHTYPE) {
                        startOtherActivity(context, targetClass);
                    }
                }
            }
        }
    }

    @Override
    public void disPlayInterstitialAd() {
        try {
            Log.e(TAG, "admessage : " + isInterstialAd+"\t\t"+mWorker.isReady()+"\t\t"+(mWorker!=null));
            if (mWorker!=null&&isInterstialAd&&mWorker.isReady()) {
                mWorker.show();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void destoryAd() {
        if (mWorker!=null) {
            try {
                mWorker.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
