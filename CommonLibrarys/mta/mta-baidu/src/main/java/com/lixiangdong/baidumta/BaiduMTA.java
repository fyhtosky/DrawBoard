package com.lixiangdong.baidumta;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mobstat.StatService;

/**
 * Created by chenjie on 2018/3/20.
 * 用于快速集成百度移动统计代码
 */

public class BaiduMTA implements Application.ActivityLifecycleCallbacks {

    private final static String TAG = BaiduMTA.class.getCanonicalName();
    private boolean statServiceStarted = false;
    private Application application;

    public BaiduMTA(Application application) {
        this.application = application;
        StatService.autoTrace(application, true, true);
    }

    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(new BaiduMTA(application));
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.d(TAG, "onActivityCreated:" + activity);
        if (!statServiceStarted) {
            statServiceStarted = true;
            StatService.setDebugOn(isApkDebugable());
//            StatService.setAppKey("ce187b22ac");
            StatService.start(activity);

            String testDeviceId = StatService.getTestDeviceId(activity);
            Log.d(TAG, "testDeviceId:" + testDeviceId);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        StatService.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        StatService.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private boolean isApkDebugable() {
        try {
            ApplicationInfo info = application.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
