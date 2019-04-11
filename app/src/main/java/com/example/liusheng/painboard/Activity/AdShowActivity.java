package com.example.liusheng.painboard.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.PermissionHelper;
import com.example.liusheng.painboard.constant.AdConstant;

/**
 * Created by yuwei on 2018/9/12.
 */

public class AdShowActivity extends Activity implements View.OnClickListener {

    ImageView ivClose;
    WebView wbCustomAd;
    String device_id;
    String webUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_show);
        initView();
        initDeviceId();
        initIntent();
        setWebView();
    }

    private void initView() {
        ivClose = findViewById(R.id.iv_close);
        wbCustomAd = findViewById(R.id.wb_custom_ad);

        ivClose.setOnClickListener(this);
        wbCustomAd.setOnClickListener(this);
    }

    @SuppressLint("MissingPermission")
    private void initDeviceId() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (PermissionHelper.hasReadPhoneStatePermission(this)) {
            device_id = tm.getDeviceId();
        } else {
            device_id = "";
        }
    }

    //画画板
    //开屏广告链接：https://engine.lvehaisen.com/index/activity?appKey=446S1mhP3vvyJ4jpxMkQxjHLwWBq&adslotId=267205
    //悬浮广告链接：https://engine.lvehaisen.com/index/activity?appKey=446S1mhP3vvyJ4jpxMkQxjHLwWBq&adslotId=267195
    private void initIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        webUrl = getIntent().getStringExtra(AdConstant.KEY_AD_URL);
        if (webUrl == null) {
            return;
        }
        webUrl = webUrl + "&device_id=" + device_id;
    }

    private void setWebView() {
        if (webUrl == null) {
            return;
        }
        wbCustomAd.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wbCustomAd.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        wbCustomAd.getSettings().setJavaScriptEnabled(true);
        wbCustomAd.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        wbCustomAd.loadUrl(webUrl);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                if (wbCustomAd.canGoBack()) {
                    wbCustomAd.goBack();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && wbCustomAd.canGoBack()) {
            wbCustomAd.goBack();
            return true;
        } else {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
}
