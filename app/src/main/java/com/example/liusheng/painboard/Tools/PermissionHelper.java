package com.example.liusheng.painboard.Tools;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by chenzhen on 2019/2/15.
 */

public class PermissionHelper {

    static final String TAG = PermissionHelper.class.getSimpleName();


    public static final String TIPS_READ_STORAGE_PERMISSION = "Read to external storage permission is needed to run this application";
    public static final String TIPS_WRITE_STORAGE_PERMISSION = "Write to external storage permission is needed to run this application";
    public static final String TIPS_READ_PHONE_STATE_PERMISSION = "Read phone state permission is needed to run this application";


    public static final int READ_STORAGE_PERMISSION_CODE = 1009;
    public static final int WRITE_STORAGE_PERMISSION_CODE = 1010;
    private static final int READ_PHONE_STATE_PERMISSION_CODE = 1011;

    private PermissionHelper() {
    }


    /**
     * 判断有写入SD卡权限
     * @param activity
     * @return
     */
    public static boolean hasWriteStoragePermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 判断有读取SD卡权限
     * @param activity
     * @return
     */
    public static boolean hasReadStoragePermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 判断有读取手机状态权限
     * @param activity
     * @return
     */
    public static boolean hasReadPhoneStatePermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 请求写入SD卡权限
     * @param activity
     */
    public static void requestWriteStoragePermission(Activity activity) {
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (shouldShowRationale) {
            Toast.makeText(activity, TIPS_WRITE_STORAGE_PERMISSION, Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_CODE);
        }
    }


    /**
     * 请求读取SD卡权限
     * @param activity
     */
    public static void requestReadStoragePermission(Activity activity) {
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (shouldShowRationale) {
            Toast.makeText(activity, TIPS_READ_STORAGE_PERMISSION, Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_CODE);
        }
    }

    /**
     * 请求读取手机状态
     * @param activity
     */
    public static void requestReadPhoneStatePermission(Activity activity) {
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE);
        if (shouldShowRationale) {
            Toast.makeText(activity, TIPS_READ_STORAGE_PERMISSION, Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSION_CODE);
        }
    }

    public static void launchPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

}
