package com.lafonapps.common;

import android.view.Gravity;

import java.util.List;


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

public class CommonConfig {

    public static final CommonConfig sharedCommonConfig = new CommonConfig();

    public boolean isDebug = false;

    //广告平台名
    public String bannerAdName = "";
    public String standbyBannerAdName = "";
    public String floatAdName = "";
    public String nativeLAdName = "";
    public String nativeMAdName = "";
    public String nativeSAdName = "";
    public String splashAdName = "";
    public String interstitialAdName = "";
    public String vedioAdName = "";

    /**
     * Admob广告配置
     */
    public String appID4Admob = "ca-app-pub-8698484584626435~1504595858";
    public String bannerAdUnitID4Admob = "ca-app-pub-8698484584626435/7634889932";
    public String nativeAdUnitID4Admob = "ca-app-pub-8698484584626435/1142645536";
    public String nativeAdUnitID132H4Admob = "ca-app-pub-8698484584626435/1284926399";
    public String nativeAdUnitID250H4Admob = "ca-app-pub-8698484584626435/9762359826";
    public String splashAdUnitID4Admob = "ca-app-pub-8698484584626435/6301405192";
    public String interstitialAdUnitID4Admob = "ca-app-pub-8698484584626435/6470159684";
    public String videoAdUnitID4Admob = "ca-app-pub-8698484584626435/6470159684";
    public String floatAdUnitID4Admob = "";
    /**
     * 测试设备ID
     */
    public String[] testDevices = {"be3fd841-ddc1-4107-ad55-8bf461dddc1c"};

    /**
     * 小米广告配置
     */
    public String appID4XiaoMi = "2882303761517411490";  //2882303761517621548  2882303761517411490
    public String bannerAdUnitID4XiaoMi = "802e356f1726f9ff39c69308bfd6f06a"; //07077ebba2bb1439dcc16a1becb24837  802e356f1726f9ff39c69308bfd6f06a
    public String nativeAdUnitID4XiaoMi = "0c220d9bf7029e71461f247485696d07";
    public String nativeAdUnitID132H4XiaoMi = "b38f454156852941f3883c736c79e7e1";
    public String nativeAdUnitID250H4XiaoMi = "2cae1a1f63f60185630f78a1d63923b0";
    public String splashAdUnitID4XiaoMi = "b373ee903da0c6fc9c9da202df95a500";
    public String interstitialAdUnitID4XiaoMi = "67b05e7cc9533510d4b8d9d4d78d0ae9";
    public String floatAdUnitID4XiaoMi = "a6fa00f86c8d849bd9eccade17e3eba5";
    public String videoAdUnitID4XiaoMi = "20c070adf42787a99f8146881a640306";
    /**
     * 悬浮广告位置
     */
    public int gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT; //可自由组合设定

    /**
     * OPPO广告配置
     */
    public String appID4OPPO = "3578431";
    public String bannerAdUnitID4OPPO = "2787";
    public String nativeAdUnitID4OPPO = "332";
    public String nativeAdUnitID132H4OPPO = "3347";
    public String nativeAdUnitID250H4OPPO = "3347";
    public String splashAdUnitID4OPPO = "2789";
    public String interstitialAdUnitID4OPPO = "2788";
    public String videoAdUnitID4OPPO = "3347";
    public String floatAdUnitID4OPPO = "";

    /**
     * tencent广告配置
     */
    public String appID4Tencent = "1101152570";
    public String bannerAdUnitID4Tencent = "9079537218417626401";
    public String nativeAdUnitID4Tencent = "7030020348049331";
    public String nativeAdUnitID132H4Tencent = "7030020348049331";
    public String nativeAdUnitID250H4Tencent = "7030020348049331";
    public String splashAdUnitID4Tencent = "8863364436303842593";
    public String interstitialAdUnitID4Tencent = "8575134060152130849";
    public String videoAdUnitID4Tencent = "8575134060152130849";
    public String floatAdUnitID4Tencent = "";

    /**
     * tuia广告配置
     */
    public String bannerAdUnitID4Adtuia = "458";
    public String nativeAdUnitID180H4tuia = "461";
    public String nativeAdUnitID420H4tuia = "460";
    public String splashAdUnitID4Adtuia = "466";
    public String interstitialAdUnitID4Adtuia = "459";
    public String floatAdUnitID4tuia = "463";
    public String videoAdUnitID4tuia = "463";

    /**
     * Facebook广告配置
     */
    public String appID4Facebook = "387546555060621";
    /**
     * 普通横幅广告
     */
    public String bannerAdUnitID4Facebook = "387546555060621_387547748393835";
    /**
     * 原生横幅广告
     */
    public String nativeBannerAdUnitID4Facebook = "387546555060621_404071570074786";

    public String nativeAdUnitID4Facebook = "387546555060621_387547748393835";
    public String nativeAdUnitID132H4Facebook = "";
    public String nativeAdUnitID250H4Facebook = "";
    public String splashAdUnitID4Facebook = "387546555060621_387547041727239";
    public String interstitialAdUnitID4Facebook = "387546555060621_387547648393845";
    public String floatAdUnitID4Facebook = "";
    public String videoAdUnitID4Facebook = "";

    /**
     * 点击开屏广告下载应用或打开网址之前是否提示用户。true为先提醒用户，再次点击会下载应用或打开网址。false为不提示用户，直接下载应用或打开网址
     */
    public boolean shouldComfirmBeforeDownloadAppOnSplashAdClick = false;
    /**
     * 点击横幅广告下载应用或打开网址之前是否提示用户。true为先提醒用户，再次点击会下载应用或打开网址。false为不提示用户，直接下载应用或打开网址
     */
    public boolean shouldComfirmBeforeDownloadAppOnBannerViewClick = false;
    /**
     * 点击其它广告下载应用或打开网址之前是否提示用户。true为先提醒用户，再次点击会下载应用或打开网址。false为不提示用户，直接下载应用或打开网址
     */
    public boolean shouldComfirmBeforeDownloadAppOnOtherAdViewClick = false;

    //是否显示开屏广告
    public boolean isShowSplash = true;
    //是否显示插屏广告
    public boolean isShowOtherAd = true;

    /** 是否显示插屏广告 */
    public boolean shouldShowInterstitialAd = false;

    /**
     * 反馈邮箱
     */
    public String feedbackEmailAddress = "moreapps.service@gmail.com";

    /** 预设的用户反馈选项，多选。设置为null则使用预置的通用选项，设置为空List则没有选项 */
    public List<String> presetSuggestions ;

    /**
     * 界面切换多少次后弹出全屏广告
     */
    public int numberOfTimesToPresentInterstitial = 10;

    /**
     * 友盟AppKey
     */
    public String umengAppKey = "";

    /** 当前包所对应的应用商店 */
    public String market = "";

    /** 按下Back键时，当前Activity是最后一个Activity的时候，是否提示评论。如果评论过，则不提示 */
    public boolean shouldPromptToRateAppAtLastActivityOnBackPressed = true;
    /** 按下Back键时，如果弹出提示评论窗，是否必须要点击按钮才能推出应用程序  */
    public boolean forcePromptToRateForBackEvent = false;
    /**  至少启动应用多少次后提示用户评论。如果评论过，则不提示。0表示不提示，1表示第一启动就提示，n则表示第n次启动提示 */
    public int minAppLaunchCountToPromptToRateAppOnAppLaunched = 0;
    /**  从后台返回前台提示评论的几率。如已经评论过，则不提示。如果返回0，则不提示，1表示每次都提示，n则提示概率为1/n */
    public int probabilityValueForPromptToRateAppWhenApplicationEnterForeground = 0;
    /** 是否使用激进式提示评论用语。激进式效果比较好，但风险较高，目前针对Google Play使用 */
    public boolean useRadicalismRateText = false;

    /** talkingdata ID */
    public String talkingDataKey = "";

}
