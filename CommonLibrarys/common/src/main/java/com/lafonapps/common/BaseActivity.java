package com.lafonapps.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import com.lafonapps.adadapter.AdListener;
import com.lafonapps.adadapter.AdType;
import com.lafonapps.adadapter.utils.AppStatusDetector;
import com.lafonapps.adadapter.utils.NotificationCenter;
import com.lafonapps.common.rate.AppRater;
import com.lafonapps.common.update.VersionAutoUpdater;
import com.lafonapps.common.util.Common;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public abstract class BaseActivity extends AppCompatActivity implements AdListener{

    private static final String TAG = BaseActivity.class.getName();
    private static int counter;
    private static boolean promptToRateOnAppLaunched; //是否已经在应用启动后提示过评论了
    protected String tag = getClass().getCanonicalName();

    private Observer applicationWillEnterForegroundNotificationObserver = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            if (Common.getCurrentActivity() == BaseActivity.this) {
                if (CommonConfig.sharedCommonConfig.shouldShowInterstitialAd) {
                    showInterstitalAd();
                } else  {
                    promptToRateAppWhenApplicationEnterForeground();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(tag, "onCreate");

        NotificationCenter.defaultCenter().addObserver
                (AppStatusDetector.APPLICATION_WILL_ENTER_FOREGROUND_NOTIFICATION,
                        applicationWillEnterForegroundNotificationObserver);

        VersionAutoUpdater.autoUpdateOnce(this);

        //promptToRateAppOnAppLaunched(); //启动应用后提示评论

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(tag, "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //预加载全屏广告
//        loadInterstitalAd();
//        showBannerAd();
        Log.d(tag, "onResume");
        //待当前Activity界面完成布局后再展示全屏广告，避免无法展示
//        findViewById(android.R.id.content).post(new Runnable() {
//            @Override
//            public void run() {
//                incrementAdCounter();
//            }
//        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.d(tag, "onAttachedToWindow");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Log.d(tag, "onDetachedFromWindow");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(tag, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(tag, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(tag, "onStop");
    }

    @Override
    protected void onDestroy() {
        Log.d(tag, "onDestroy");
        NotificationCenter.defaultCenter().removeObserver
                (AppStatusDetector.APPLICATION_WILL_ENTER_FOREGROUND_NOTIFICATION,
                        applicationWillEnterForegroundNotificationObserver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //拦截返回键，加入以下逻辑：1.没有评论过的提示评论；2.防止连续点击返回键误退出应用，加入"再次点击返回键退出"提示
        if (shouldPromptToRateAppAtLastActivityOnBackPressed()) {
            boolean handed = AppRater.defaultAppRater.handBackEventToPromtRate(this);
            if (handed) {
                return;
            }
        }
        super.onBackPressed();
    }

    /**
     * 显示banner广告
     */
    protected void showBannerAd() {
        AdManager.getInstance().showAd(CommonConfig.sharedCommonConfig.bannerAdName, BaseActivity.this, getBannerView(), AdType.BANNERTYPE, this);
    }
    public abstract ViewGroup getBannerView();
    /**
     * 显示悬浮广告
     */
    protected void showFloatAd() {
        AdManager.getInstance().showAd(CommonConfig.sharedCommonConfig.floatAdName, BaseActivity.this, getFloatView(), AdType.FLOATTYPE, this);
    }
    public abstract ViewGroup getFloatView();
    /**
     * 显示信息流大图广告
     */
    protected void showNativeLAd() {
        AdManager.getInstance().showAd(CommonConfig.sharedCommonConfig.nativeLAdName, BaseActivity.this, getNativeLView(), AdType.NATIVELTYPE, this);
    }
    public abstract ViewGroup getNativeLView();
    /**
     * 显示信息流组图广告
     */
    protected void showNativeMAd() {
        AdManager.getInstance().showAd(CommonConfig.sharedCommonConfig.nativeMAdName, BaseActivity.this, getNativeMView(), AdType.NATIVEMTYPE, this);
    }
    public abstract ViewGroup getNativeMView();
    /**
     * 显示信息流小图广告
     */
    protected void showNativeSAd() {
        AdManager.getInstance().showAd(CommonConfig.sharedCommonConfig.nativeSAdName, BaseActivity.this, getNativeSView(), AdType.NATIVESTYPE, this);
    }
    public abstract ViewGroup getNativeSView();
    /**
     * 显示视屏广告
     */
    protected void showVedioAd() {
        AdManager.getInstance().showAd(CommonConfig.sharedCommonConfig.vedioAdName, BaseActivity.this, getVedioView(), AdType.VIDEOTYPE, this);
    }
    public abstract ViewGroup getVedioView();
    /**
     * 显示插屏广告
     */
    protected void showInterstitalAd() {
        AdManager.getInstance().disPlayInterstitial(CommonConfig.sharedCommonConfig.interstitialAdName);
    }
    public abstract ViewGroup getInterstitalView();
    /**
     * 加载插屏广告
     */
    protected void loadInterstitalAd() {
        AdManager.getInstance().showAd(CommonConfig.sharedCommonConfig.interstitialAdName, BaseActivity.this, getInterstitalView(), AdType.INTERSTIALTYPE, this);
    }


    //获取当前界面的广告平台
    public abstract String[] getAdType();

    //针对获取的广告平台，分别释放资源
    public void destoryAd() {
        ArrayList<String> adNameList=new ArrayList<String>();

        for (int i=0 ; i<getAdType().length ; i++) {
            adNameList.add(getAdType()[i]);
        }
        HashSet<String> adNameSetList=new HashSet<String>();
        adNameSetList.addAll(adNameList);
        for (String s : adNameSetList) {
            AdManager.getInstance().destoryAd(s);
        }
    }




    /**
     * 按下Back键时是否提示评论。仅当当前对象是最后一个Activity的时候，并且没有评论过，才会提示评论
     * 子类可以重写此方法。
     *
     * @return 如果没有评论过，是否提示评论。值由CommonConfig.sharedCommonConfig.shouldPromptToRateAppAtLastActivityOnBackPressed决定
     */
    protected boolean shouldPromptToRateAppAtLastActivityOnBackPressed() {
        return CommonConfig.sharedCommonConfig.shouldPromptToRateAppAtLastActivityOnBackPressed;
    }

    /**
     * 在应用启动后提示评论。如果已经评论过，或者shouldPromptToRateAppOnAppLaunched()方法返回false，则不提示
     */
//    public void promptToRateAppOnAppLaunched() {
//        if (shouldPromptToRateAppOnAppLaunched() && AppRater.defaultAppRater.shouldRate(this) && !promptToRateOnAppLaunched) {
//            AppRater.defaultAppRater.promptToRateWithCustomDialog(this);
//            promptToRateOnAppLaunched = true;
//        }
//    }

    /**
     * 启动应用后，是否提示用户评论。如果用户已评论过，则什么都不做
     * 子类可以重写此方法。
     *
     * @return 如果没有评论过，是否提示评论。值由CommonConfig.sharedCommonConfig.shouldPromptToRateAppOnAppLaunched
     */
//    protected boolean shouldPromptToRateAppOnAppLaunched() {
//        int appLaunchedCountAtCurrentVersion = Preferences.getSharedPreference().getCountOfAppLaunchedFromVersion();
//        if (CommonConfig.sharedCommonConfig.minAppLaunchCountToPromptToRateAppOnAppLaunched > 0 && CommonConfig.sharedCommonConfig.minAppLaunchCountToPromptToRateAppOnAppLaunched <= appLaunchedCountAtCurrentVersion) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * 应用从后台返回到前台，提示用户评论。评论评论的概率是1/3。如果用户已评论过，promptToRateAppWhenApplicationEnterForeground()方法返回false，则不提示。
     */
    private void promptToRateAppWhenApplicationEnterForeground() {
        if (shouldPromptToRateAppWhenApplicationEnterForeground() && AppRater.defaultAppRater.shouldRate(this)) {
            AppRater.defaultAppRater.promptToRateWithCustomDialog(this);
        }
    }

    /**
     * 从后台返回前台是否提示评论。如已经评论过，则不提示。默认值由CommonConfig.sharedCommonConfig.probabilityValueForPromptToRateAppWhenApplicationEnterForeground决定。如果返回0，则不提示，返回1则每次都提示，返回n则概率为1/n
     *
     * @return 如果没有评论过，是否提示评论。
     */
    protected boolean shouldPromptToRateAppWhenApplicationEnterForeground() {
        boolean shouldPrompt = false;
        int probability = CommonConfig.sharedCommonConfig.probabilityValueForPromptToRateAppWhenApplicationEnterForeground;
        if (probability > 0) {
            Random random = new Random();
            int randomInt = random.nextInt(probability) + 1;
            Log.d(TAG, "promptToRateAppWhenApplicationEnterForeground: randomInt = " + randomInt);
            if (randomInt == probability) { //1/n的弹出概率
                shouldPrompt = true;
            }
        }
        return shouldPrompt;
    }

//
//    /**
//     * 增加即将弹出广告的计数次数，达到一定次数就会弹出广告，然后重置为0
//     */
//    protected void incrementAdCounter() {
//        counter++;
//
//        int numberOfTimesToPresentInterstitial = Preferences.getSharedPreference().getNumberOfTimesToPresentInterstitial();
//        Log.d(TAG, "presentedTimes = " + counter + ", numberOfTimesToPresentInterstitial = " + numberOfTimesToPresentInterstitial);
//        if (counter >= numberOfTimesToPresentInterstitial && shouldAutoPresentInterstitialAd()) {
//            //显示插屏
//            showInterstitalAd();
//        }
//    }

    /* 是否在页面跳转次数累计到一定次数后自动弹出全屏广告。默认是true */
    protected boolean shouldAutoPresentInterstitialAd() {
        return true;
    }

    @Override
    public void onTimeOut(int adType) {

    }

    @Override
    public void onLoaded(int adType) {

    }

    @Override
    public void onLoadFailed(int adType) {
        Log.d(TAG, "onLoadFailed: "+ adType);
        if (adType == AdType.BANNERTYPE){
            ViewGroup contener = getBannerView();
            if (contener != null){
                contener.removeAllViews();
            }
            AdManager.getInstance().showAd(CommonConfig.sharedCommonConfig.standbyBannerAdName, this, contener, adType, new AdListener() {
                @Override
                public void onTimeOut(int adType) {

                }

                @Override
                public void onLoaded(int adType) {

                }

                @Override
                public void onLoadFailed(int adType) {

                }

                @Override
                public void onCloseClick(int adType) {

                }

                @Override
                public void onDismiss(int adType) {

                }

                @Override
                public void onAdClick(int adType) {

                }

                @Override
                public void onAdExposure(int adType) {

                }
            });
        }

    }

    @Override
    public void onCloseClick(int adType) {

    }

    @Override
    public void onDismiss(int adType) {
    }

    @Override
    public void onAdClick(int adType) {

    }

    @Override
    public void onAdExposure(int adType) {

    }
}
