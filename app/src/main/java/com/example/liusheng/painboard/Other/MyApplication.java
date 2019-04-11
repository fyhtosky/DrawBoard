package com.example.liusheng.painboard.Other;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.baidu.mobstat.StatService;
import com.example.liusheng.painboard.BuildConfig;
import com.example.liusheng.painboard.R;
import com.lafonapps.common.CommonConfig;
import com.lafonapps.common.feedback.activity.FeedbackInputActivity;
import com.lafonapps.common.util.Common;

public class MyApplication extends Application {


    private static final String TAG = MyApplication.class.getCanonicalName();

    private static MyApplication sharedApplication;

    public static MyApplication getSharedApplication() {
        return sharedApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedApplication = this;

        CommonConfig.sharedCommonConfig.isDebug = BuildConfig.DEBUG;

        CommonConfig.sharedCommonConfig.bannerAdName = BuildConfig.bannerAdName;
        CommonConfig.sharedCommonConfig.standbyBannerAdName = BuildConfig.standbyBannerAdName;  //
        CommonConfig.sharedCommonConfig.floatAdName = BuildConfig.floatAdName;
        CommonConfig.sharedCommonConfig.nativeLAdName = BuildConfig.nativeLAdName;
        CommonConfig.sharedCommonConfig.nativeSAdName = BuildConfig.nativeSAdName;
        CommonConfig.sharedCommonConfig.nativeMAdName = BuildConfig.nativeMAdName;
        CommonConfig.sharedCommonConfig.vedioAdName = BuildConfig.vedioAdName;
        CommonConfig.sharedCommonConfig.splashAdName = BuildConfig.splashAdName;
        CommonConfig.sharedCommonConfig.interstitialAdName = BuildConfig.interstitialAdName;


//        TuiAAdsManager.initAd(this);
//        Toast.makeText(this, "ahhah", Toast.LENGTH_SHORT).show();
//        TuiAAdsCommonConfig.sharedInstance.bannerAdId = 462;
//        TuiAAdsCommonConfig.sharedInstance.smallBannerAdId = 458;
//        TuiAAdsCommonConfig.sharedInstance.interstialAdId = 459;
//        TuiAAdsCommonConfig.sharedInstance.newsFeed180AdId = 461;
//        TuiAAdsCommonConfig.sharedInstance.newsFeed420AdId = 460;
        //TuiAAdsCommonConfig.sharedInstance.floatAdId = 194476;
        //TuiAAdsCommonConfig.sharedInstance.splashAdId = 194475;

        CommonConfig.sharedCommonConfig.floatAdUnitID4tuia = "194476";
        CommonConfig.sharedCommonConfig.splashAdUnitID4Adtuia = "194475";


        /**
         *  Admob广告配置
         */
        CommonConfig.sharedCommonConfig.appID4Admob = "ca-app-pub-7028363992110677~7886988953"; //广告应用ID
        CommonConfig.sharedCommonConfig.bannerAdUnitID4Admob = "ca-app-pub-7028363992110677/6951636640"; //横幅广告ID
//        CommonConfig.sharedCommonConfig.nativeAdUnitID4Admob = "ca-app-pub-8698484584626435/2229416369"; //小型原生广告ID
//        CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Admob = "ca-app-pub-8698484584626435/1284926399"; //中型原生广告ID
//        CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Admob = "ca-app-pub-8698484584626435/8459960407"; //大型原生广告ID
//        CommonConfig.sharedCommonConfig.splashAdUnitID4Admob = "ca-app-pub-8698484584626435/6470159684"; //用作开屏广告的原生广告ID
        CommonConfig.sharedCommonConfig.interstitialAdUnitID4Admob = "ca-app-pub-7028363992110677/7226968817"; //全屏广告ID

        /***
         * 小米广告配置
         * appid：2882303761517629751
         * 横幅广告id：6bf7ef2dab15caac6b6a95dbe464f4a9
         * 开屏：479ec8fe2d8549168b2afa09a0675608
         * 系统开屏：9d5680de7675dbf3ee37a3d109ba00da
         */
        CommonConfig.sharedCommonConfig.appID4XiaoMi = "2882303761517629751"; //广告应用ID
        CommonConfig.sharedCommonConfig.bannerAdUnitID4XiaoMi = "6bf7ef2dab15caac6b6a95dbe464f4a9"; //横幅广告ID
        CommonConfig.sharedCommonConfig.splashAdUnitID4XiaoMi = "479ec8fe2d8549168b2afa09a0675608"; //开屏广告ID
        CommonConfig.sharedCommonConfig.nativeAdUnitID4XiaoMi = "7d6cb2eaa61a5ee7ad41193a12cc4095"; //小型信息流广告ID
//        CommonConfig.sharedCommonConfig.nativeAdUnitID132H4XiaoMi = "9aa660fd8bdbb6bee98461745916eb32"; //信息流组图广告ID
        CommonConfig.sharedCommonConfig.nativeAdUnitID250H4XiaoMi = "bb3c1d734b532781aba2f7a7ee405790"; //大型信息流广告ID
        CommonConfig.sharedCommonConfig.interstitialAdUnitID4XiaoMi = "2af24377e146411697b59a78932f3ce1"; //全屏广告ID


        /**
         * OPPO广告配置
         */
//        CommonConfig.sharedCommonConfig.appID4OPPO = "100";
//        CommonConfig.sharedCommonConfig.bannerAdUnitID4OPPO = "328";
//        CommonConfig.sharedCommonConfig.nativeAdUnitID4OPPO = "";
//        CommonConfig.sharedCommonConfig.nativeAdUnitID132H4OPPO = "";
//        CommonConfig.sharedCommonConfig.nativeAdUnitID250H4OPPO = "332";
//        CommonConfig.sharedCommonConfig.splashAdUnitID4OPPO = "331";
//        CommonConfig.sharedCommonConfig.interstitialAdUnitID4OPPO = "329";

        /**
         *  腾讯广告配置
         */
        CommonConfig.sharedCommonConfig.appID4Tencent = "1106546503";
        CommonConfig.sharedCommonConfig.bannerAdUnitID4Tencent = "4020526837083616";
        CommonConfig.sharedCommonConfig.nativeAdUnitID4Tencent = "";
        CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Tencent = "";
        CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Tencent = "";
        CommonConfig.sharedCommonConfig.splashAdUnitID4Tencent = "7060526837786617";
        CommonConfig.sharedCommonConfig.interstitialAdUnitID4Tencent = "";

        if ("xiaomi".equals(BuildConfig.FLAVOR)) {

            CommonConfig.sharedCommonConfig.appID4Tencent = "1106546503";
            CommonConfig.sharedCommonConfig.bannerAdUnitID4Tencent = "1030453727083887";
            CommonConfig.sharedCommonConfig.nativeAdUnitID4Tencent = "";
            CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Tencent = "";
            CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Tencent = "";
            CommonConfig.sharedCommonConfig.splashAdUnitID4Tencent = "5030854707180940";
            CommonConfig.sharedCommonConfig.interstitialAdUnitID4Tencent = "";

        } else if ("vivo".equals(BuildConfig.FLAVOR)) {

            CommonConfig.sharedCommonConfig.appID4Tencent = "1106546503";
            CommonConfig.sharedCommonConfig.bannerAdUnitID4Tencent = "1070753707082866";
            CommonConfig.sharedCommonConfig.nativeAdUnitID4Tencent = "";
            CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Tencent = "";
            CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Tencent = "";
            CommonConfig.sharedCommonConfig.splashAdUnitID4Tencent = "2050359717289809";
            CommonConfig.sharedCommonConfig.interstitialAdUnitID4Tencent = "";

        } else if ("oppo".equals(BuildConfig.FLAVOR)) {

            CommonConfig.sharedCommonConfig.appID4Tencent = "1106546503";
            CommonConfig.sharedCommonConfig.bannerAdUnitID4Tencent = "4020526837083616";
            CommonConfig.sharedCommonConfig.nativeAdUnitID4Tencent = "";
            CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Tencent = "";
            CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Tencent = "";
            CommonConfig.sharedCommonConfig.splashAdUnitID4Tencent = "7060526837786617";
            CommonConfig.sharedCommonConfig.interstitialAdUnitID4Tencent = "";

        }



        /* 测试设备ID */
        CommonConfig.sharedCommonConfig.testDevices = new String[]{
                "2C7051C179D611A65CB34AED3255F136",
                "9E8A18C2A04EA50F41F258354D86601F",
                "7D08A034F6946BED1E0EF80F61A71124",
                "1FB61E9F3F955A3DEF1F1DCA2CD2C510",
                "226FF93D678B6499DF2DAA0AE56802F1",
                "181F363C876857CE4F79750F6A10D3AA"
        };

        /** 是否展示插屏广告 */
        /** 点击横幅广告下载应用或打开网址之前是否提示用户。true为先提醒用户，再次点击会下载应用或打开网址。false为不提示用户，直接下载应用或打开网址 */
        CommonConfig.sharedCommonConfig.shouldComfirmBeforeDownloadAppOnBannerViewClick = BuildConfig.showComfirmBeforeDownloadAppOnBannerViewClick; //在app的build.gradle中进行差异化配置
        /** 点击开屏广告下载应用或打开网址之前是否提示用户。true为先提醒用户，再次点击会下载应用或打开网址。false为不提示用户，直接下载应用或打开网址 */
        CommonConfig.sharedCommonConfig.shouldComfirmBeforeDownloadAppOnSplashAdClick = BuildConfig.showComfirmBeforeDownloadAppOnSplashAdClick; //在app的build.gradle中进行差异化配置
        /** 当前包所对应的应用商店 */
        CommonConfig.sharedCommonConfig.market = BuildConfig.FLAVOR; //对应app的build.gradle中的productFlavor名称

        /* 友盟统计key */
        CommonConfig.sharedCommonConfig.umengAppKey = "59a6927b6e27a45fe200031f";

        CommonConfig.sharedCommonConfig.market = BuildConfig.FLAVOR;


//        CommonConfig.sharedCommonConfig.leanCloudAppID = "WM2ILPFB8a3JC99HjM9dLkRS-gzGzoHsz";
//        CommonConfig.sharedCommonConfig.leanCloudAppKey = "44kQRXeWc5TI9NeoOUYjeFej";


        Common.initialize(this);

        savePenMessage();
//
//        //LeanCloud基本配置
//        // 正式版使用
//        FeedbackOperation.Configuration(this, "WM2ILPFB8a3JC99HjM9dLkRS-gzGzoHsz", "44kQRXeWc5TI9NeoOUYjeFej");
//        // 开发环境下调试使用
////        FeedbackOperation.Configuration(this, "RVx4wbF7FLW3KOa573USo91b-gzGzoHsz", "pBrW9gcwOxrJ7jE58fXBNyyH");
//        JumpContactOperation.SetQQ("3505789356");
//        JumpContactOperation.SetEmail("wmxyfacebook@gmail.com");

        //反馈
        initFeedBack();

    }

    private void initFeedBack() {
        //  后台意见反馈
        FeedbackInputActivity.FEEDBACK_URL = "http://121.40.61.21:8080/Statistics_branch/transit/addData";
        FeedbackInputActivity.APP_NAME = getString(R.string.app_name);
        //StatService是来自百度统计api，设备唯一标识
        FeedbackInputActivity.DEVICE_ID = StatService.getTestDeviceId(this);
        //talkingDataKey 配置(需根据应用分别配置)
        CommonConfig.sharedCommonConfig.talkingDataKey = "";

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void savePenMessage() {
        SharedPreferences preferences = getSharedPreferences("PenMessage", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("PenSize", 0);
        editor.putInt("PenColor", 0);
        editor.apply();
    }


}
