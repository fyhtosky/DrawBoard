package com.lafonapps.common;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lafonapps.adadapter.AdListener;
import com.lafonapps.adadapter.AdType;
import com.lafonapps.adadapter.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;



public abstract class BaseSplashAdActivity extends Activity {

    private static final String TAG = BaseSplashAdActivity.class.getCanonicalName();
    private static final int REQUEST_PERMISSIONS_CODE = 100;

    protected int requestTimeOut = 5;

    private LinearLayout container;
    private ImageView im_bg;

    /**
     * 判断是否可以立刻跳转应用主页面。
     */
    private boolean shouldDismiss = false;

    //超时则直接跳转主界面
    private CountDownTimer requestTimer = new CountDownTimer(requestTimeOut * 1000, 1000) {
        @Override
        public void onTick(long l) {
            requestTimeOut--;
            Log.d(TAG, "Request countdown = " + requestTimeOut);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "requestTimer finish");
            CommonUtils.startOtherActivity(BaseSplashAdActivity.this,getTargetActivity());
        }
    };

    private void loadAndDisplay() {
        AdManager.getInstance().setTargetClass(getTargetActivity());
        if (!CommonUtils.isNetworkConnected(this)) {
            CommonUtils.startOtherActivity(this,getTargetActivity());
        } else {
            //显示开屏广告并跳转
            AdManager.getInstance().showAd(CommonConfig.sharedCommonConfig.splashAdName, BaseSplashAdActivity.this, container, AdType.SPLASHTYPE, new AdListener() {
                @Override
                public void onTimeOut(int adType) {
                    Log.i(TAG, "onTimeOut");
                }

                @Override
                public void onLoaded(int adType) {
                    Log.i(TAG, "onLoaded");
                    //facebook  没有处理该回调
                    if (!CommonConfig.sharedCommonConfig.splashAdName.equals("facebook")) {
                        requestTimer.cancel();
                    }
                }

                @Override
                public void onLoadFailed(int adType) {
                    Log.i(TAG, "onLoadFailed");
                    requestTimer.cancel();
                }

                @Override
                public void onCloseClick(int adType) {
                    Log.i(TAG, "onCloseClick");
                }

                @Override
                public void onDismiss(int adType) {
                    Log.i(TAG, "onDismiss");
                    //facebook  没有处理该回调
                    if (!CommonConfig.sharedCommonConfig.splashAdName.equals("facebook")) {
                        requestTimer.cancel();
                    }
                    dismissSplashAdIfShould();
                }

                @Override
                public void onAdClick(int adType) {
                    Log.i(TAG, "onLoaded");
                }

                @Override
                public void onAdExposure(int adType) {
                    Log.i(TAG, "onAdExposure");
                    //facebook  没有处理该回调
                    if (!CommonConfig.sharedCommonConfig.splashAdName.equals("facebook")) {
                        requestTimer.cancel();
                    }
                    showSplash();
                }
            });

            //facebook特殊处理
            if (!CommonConfig.sharedCommonConfig.splashAdName.equals("facebook")) {
                requestTimer.start();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        /**
         * 这里包含对于点击闪屏广告以后、然后返回闪屏广告页面立刻跳转应用主页面的处理。
         */
        if (shouldDismiss) {
            CommonUtils.startOtherActivity(BaseSplashAdActivity.this,getTargetActivity());
        }
        shouldDismiss = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * 这里包含对于点击闪屏广告以后、然后返回闪屏广告页面立刻跳转应用主页面的处理。
         */
        shouldDismiss = false;
    }

    /**
     * 结束开屏页面，跳转主页面。
     */
    protected void dismissSplashAdIfShould() {
        if (shouldDismiss) {
            CommonUtils.startOtherActivity(BaseSplashAdActivity.this,getTargetActivity());
        } else {
            shouldDismiss = true;
        }
    }

    private void initView() {
        container = (LinearLayout) findViewById(R.id.ll_splash_ad);
        im_bg = (ImageView) findViewById(R.id.im_bg);
        im_bg.setImageResource(getSplashImage());
    }

    private void showSplash() {
        im_bg.setVisibility(View.GONE);
    }

    public abstract int getSplashImage();

    public abstract Class getTargetActivity();

    /**
     * 判断应用是否已经获得SDK运行必须的READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE两个权限。
     * @return
     */
    private boolean hasNecessaryPMSGranted() {
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return true;
            }
        }
        return false;
    }


    private List<String> mNeedRequestPMSList = new ArrayList<>();
    /**
     * 申请SDK运行需要的权限
     * 注意：READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE 两个权限是必须权限，没有这两个权限SDK无法正常获得广告。
     * WRITE_CALENDAR、ACCESS_FINE_LOCATION 是两个可选权限；没有不影响SDK获取广告；但是如果应用申请到该权限，会显著提升应用的广告收益。
     */
    private void checkAndRequestPermissions() {
        /**
         * READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE 两个权限是必须权限，没有这两个权限SDK无法正常获得广告。
         */
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            mNeedRequestPMSList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //其它权限可以继续添加
        if (0 == mNeedRequestPMSList.size()) {
            /**
             * 权限都已经有了，那么直接调用SDK请求广告。
             */
            loadAndDisplay();
        } else {
            /**
             * 有权限需要申请，主动申请。
             */
            String[] temp = new String[mNeedRequestPMSList.size()];
            mNeedRequestPMSList.toArray(temp);
            ActivityCompat.requestPermissions(this, temp, REQUEST_PERMISSIONS_CODE);
        }
    }

    /**
     * 处理权限申请的结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            /**
             *处理SDK申请权限的结果。
             */
            case REQUEST_PERMISSIONS_CODE:
                if (hasNecessaryPMSGranted()) {
                    /**
                     * 应用已经获得SDK运行必须的READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE两个权限，直接请求广告。
                     */
                    loadAndDisplay();
                } else {
                    /**
                     * 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
                     */
                    CommonUtils.startOtherActivity(BaseSplashAdActivity.this,getTargetActivity());

                    Toast.makeText(this, "应用缺少SDK运行必须的READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE两个权限！请点击\"应用权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 捕获back键，在展示广告期间按back键，不跳过广告
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //
        requestTimer.cancel();
        Log.i(TAG, "onDestroy");
    }

}
