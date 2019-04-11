package com.example.liusheng.painboard.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.constant.AdConstant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yuwei on 2018/8/28.
 */

public class HWSplashActivity extends Activity implements View.OnClickListener {

    static final long MILLIS_IN_FUTURE = 3 * 1000;//维持时间
    static final int REQUEST_PERMISSIONS_CODE = 100;
    static final int REQUEST_CODE = 101;

    ImageView ivSplash;
    Button fbtCountDownTime;
    CountDownTimer mTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw_splash_ad);
        initViews();
        initCountDownTimer();
        checkAndRequestPermissions();
    }

    private void initViews() {
        ivSplash = findViewById(R.id.iv_splash);
        fbtCountDownTime = findViewById(R.id.fbt_count_down_time);

        ivSplash.setOnClickListener(this);
        fbtCountDownTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_splash:
                cancelCountDownTIme();
                Intent intent = new Intent(HWSplashActivity.this, AdShowActivity.class);
                intent.putExtra(AdConstant.KEY_AD_URL, AdConstant.HW_SPLASH_AD_URL);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.fbt_count_down_time:
                navigateToMainActivity();
                break;
        }
    }


    private void initSplashImage() {
        int count = new Random().nextInt(3);
        switch (count) {
            case 0:
                Glide.with(HWSplashActivity.this).load(R.drawable.bg1).into(ivSplash);
                break;
            case 1:
                Glide.with(HWSplashActivity.this).load(R.drawable.bg2).into(ivSplash);
                break;
            case 2:
                Glide.with(HWSplashActivity.this).load(R.drawable.bg3).into(ivSplash);
                break;
        }

    }

    private void initCountDownTimer() {
        mTimer = new CountDownTimer(MILLIS_IN_FUTURE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!HWSplashActivity.this.isFinishing()) {
                    long remainTime = millisUntilFinished / 1000;
                    fbtCountDownTime.setText(remainTime + " 跳过");
                }
            }
            @Override
            public void onFinish() {
                startActivity(new Intent(HWSplashActivity.this, MainActivity.class));
                finish();

            }
        };
        mTimer.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            navigateToMainActivity();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCountDownTIme();
    }

    public void navigateToMainActivity() {
        cancelCountDownTIme();
        startActivity(new Intent(HWSplashActivity.this, MainActivity.class));
        finish();
    }

    public void cancelCountDownTIme() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
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
            initSplashImage();
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
                    initSplashImage();
                } else {
                    /**
                     * 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
                     */
                    navigateToMainActivity();
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
}
