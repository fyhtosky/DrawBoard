package com.lafonapps.adadapter.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.lafonapps.adadapter.R;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/08/10
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class CommonUtils {
    //
    public static void startOtherActivity(Activity activity, Class targetClass) {
        Intent intent = new Intent(activity, targetClass);
        activity.startActivity(intent);
        activity.finish();
        //可以添加跳转动画
        activity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public static String getAppDisplayName(Application application) {
        ApplicationInfo applicationInfo = application.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : application.getString(stringId);
    }
    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //获取当前版本名称
    public static String getAppVersionName(Application application) {
        String versionName = "";
        try {
            Context context = application;
            String packageName = context.getPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
