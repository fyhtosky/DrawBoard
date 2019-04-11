package com.lafonapps.common.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.lafonapps.adadapter.utils.AppStatusDetector;
import com.lafonapps.common.AdManager;
import com.lafonapps.common.CommonConfig;
import com.lixiangdong.baidumta.BaiduMTA;
import com.tendcloud.tenddata.TCAgent;

/**
 * Created by chenjie on 2017/8/10.
 */

public class Common {


    private static final String TAG = Common.class.getCanonicalName();

    private static Application sharedApplication;
    private static AppStatusDetector appStatusDetector = new AppStatusDetector();


    public static void initialize(Application application) {
        sharedApplication = application;
        //广告初始化
        AdManager.getInstance().initAdSdk(application);

        Log.d(TAG, "isApkDebugable:" + isApkDebugable());

//        Preferences.getSharedPreference().appLaunched(); //标记应用启动了
//        Preferences.getSharedPreference().getAppFirstLaunchDate(); //记录应用第一次启动的日期
//        Preferences.getSharedPreference().getVersionFirstLaunchDate(); //记录或更新应用当前版本第一次启动的日期

        application.registerActivityLifecycleCallbacks(appStatusDetector);

        BaiduMTA.init(application);  //GP版本注释掉

        //talkingdata 埋点初始化
        TCAgent.LOG_ON=true;
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(application, CommonConfig.sharedCommonConfig.talkingDataKey, CommonConfig.sharedCommonConfig.market);  //更换对应app的talkingdata ID
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        TCAgent.setReportUncaughtExceptions(true);

//        AppsFlyer.init(application);
//        Facebook.init(application);
        //友盟追踪
//        ApplicationInfo info = sharedApplication.getApplicationInfo();
//        String channelId = "Unknown";
//        if (info.metaData != null) {
//            channelId = info.metaData.getString("UMENG_CHANNEL_VALUE", "Unknown");
//        }
//        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(application,
//                CommonConfig.sharedCommonConfig.umengAppKey, channelId);
//        MobclickAgent.startWithConfigure(config);
//
//        if (isApkDebugable()) {
//            MobclickAgent.setDebugMode(true);
//        }
    }

    public static Application getSharedApplication() {
        return sharedApplication;
    }

    public static AppStatusDetector getAppStatusDetector() {
        return appStatusDetector;
    }

    public static boolean isApkDebugable() {
        try {
            ApplicationInfo info = sharedApplication.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getAppDisplayName() {
        ApplicationInfo applicationInfo = sharedApplication.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : sharedApplication.getString(stringId);
    }

    //获取当前版本名称
    public static String getAppVersionName() {
        String versionName = "";
        try {
            Context context = Common.getSharedApplication();
            String packageName = context.getPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    //获取当前版本号
    public static int getAppVersionCode() {
        int versionCode = 0;
        try {
            Context context = Common.getSharedApplication();
            String packageName = context.getPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static Activity getCurrentActivity() {
        return appStatusDetector.currentActivity();
    }

}
