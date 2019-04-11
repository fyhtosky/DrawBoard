package com.example.liusheng.painboard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.liusheng.painboard.BuildConfig;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.constant.AdConstant;
public class MainActivity extends MyActivity {


    ImageView mBg;
    ViewGroup floatViewContainer;
    ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        transparentStatusBar();
        //steepMode();
        initView();
    }

    private void initView() {
        mBg = findViewById(R.id.iv_bg);
        container = findViewById(R.id.fl_container);
        Glide.with(getApplicationContext()).load(R.drawable.sy_bg).into(mBg);

        showBannerAd();
        dispatchFloatAd();
    }

    private void dispatchFloatAd() {

        RequestOptions options = new RequestOptions().error(R.drawable.ic_float_ad_error);
        if ("huawei".equalsIgnoreCase(BuildConfig.FLAVOR)) {
            //华为oppo手动广告
            final ImageView floatImageView = findViewById(R.id.float_image_view);
            floatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AdShowActivity.class);
                    intent.putExtra(AdConstant.KEY_AD_URL, AdConstant.HW_FLOAT_AD_URL);
                    startActivity(intent);
                }
            });
            Glide.with(getApplicationContext()).load(AdConstant.HW_FLOAT_AD_IMAGE_URL).apply(options).into(floatImageView);
        } else if ("vivo".equalsIgnoreCase(BuildConfig.FLAVOR)) {
            //vivo手动广告
            final ImageView floatImageView = findViewById(R.id.float_image_view);
            floatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AdShowActivity.class);
                    intent.putExtra(AdConstant.KEY_AD_URL, AdConstant.VIVO_FLOAT_AD_URL);
                    startActivity(intent);
                }
            });
            Glide.with(getApplicationContext()).load(AdConstant.VIVO_FLOAT_AD_IMAGE_URL).apply(options).into(floatImageView);
        }else if ("oppo".equalsIgnoreCase(BuildConfig.FLAVOR)) {
            final ImageView floatImageView = findViewById(R.id.float_image_view);
            floatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AdShowActivity.class);
                    intent.putExtra(AdConstant.KEY_AD_URL, AdConstant.OPPO_FLOAT_AD_URL);
                    startActivity(intent);
                }
            });
            Glide.with(getApplicationContext()).load(AdConstant.OPPO_FLOAT_AD_IMAGE_URL).apply(options).into(floatImageView);
        } else if ("xiaomi".equalsIgnoreCase(BuildConfig.FLAVOR)) {
            final ImageView floatImageView = findViewById(R.id.float_image_view);
            floatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AdShowActivity.class);
                    intent.putExtra(AdConstant.KEY_AD_URL, AdConstant.MI_FLOAT_AD_URL);
                    startActivity(intent);
                }
            });
            Glide.with(getApplicationContext()).load(AdConstant.MI_FLOAT_AD_IMAGE_URL).apply(options).into(floatImageView);
        }  else {
            findViewById(R.id.float_image_view).setVisibility(View.GONE);
            showFloatAd();
        }

        findViewById(R.id.float_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeAllViews();
                container.setVisibility(View.GONE);
            }
        });
    }

    public void onMainClick(View view) {
        switch (view.getId()) {

            case R.id.iv_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.ll_coloring_layout:
                startActivity(new Intent(this, ColoringTemplateActivity.class));
                break;

            case R.id.ll_drawing_layout:
                startActivity(new Intent(this, DrawingActivity.class));
                break;

            case R.id.ll_works_layout:
                startActivity(new Intent(this, WorksActivity.class));
                break;


            case R.id.ll_learn_layout:
                startActivity(new Intent(MainActivity.this, LearnTemplatePickActivity.class));
                break;

            case R.id.ll_dynamic_layout:
                startActivity(new Intent(MainActivity.this, DynamicActivity.class));
                break;

            case R.id.ll_magic_layout:
                startActivity(new Intent(this, MagicActivity.class));
                break;

        }
    }

    @Override
    public ViewGroup getFloatView() {
        if (floatViewContainer == null) {
            floatViewContainer = findViewById(R.id.float_view);
        }
        return floatViewContainer;
    }

    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.main_banner_view);
        }
        return bannerViewContainer;
    }

    // 定义一个变量，来标识是否退出
    private long lastTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long span = System.currentTimeMillis() - lastTime;
            if (span <= 2000) {
                finish();
                return true;
            } else {
                lastTime = System.currentTimeMillis();
                toast("再次点击，退出应用");
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
