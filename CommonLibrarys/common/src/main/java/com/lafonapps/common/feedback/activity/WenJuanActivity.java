package com.lafonapps.common.feedback.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lafonapps.common.R;

public class WenJuanActivity extends RBActivity implements View.OnClickListener {

    public interface WenjuanSuccessPresent {
        public void onSuccessfulPresent(WenJuanActivity obj);
    }

    private static final String WENJUAN_URL_KEY = "WENJUAN_URL_KEY";
    private static final String Version_Suffix = ":isQuestionnaire";

    private WebView webView;
    private ProgressBar progressBar;

    private String WEN_JUAN_URL = "";

    private static WenjuanSuccessPresent mPresent;

    public static void setOnSuccessPresent(WenjuanSuccessPresent present) {
        mPresent = present;
    }

    public static void present(@Nullable Context context, @Nullable String url) {
        Intent intent = new Intent(context, WenJuanActivity.class);
        intent.putExtra(WENJUAN_URL_KEY, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wenjuan);

        WEN_JUAN_URL = getIntent().getStringExtra(WENJUAN_URL_KEY);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        webView = (WebView) findViewById(R.id.webview);

        webView.loadUrl(WEN_JUAN_URL);
        webView.setWebViewClient(new MyWebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

    }

    public static boolean isCurrentVersionQuestionnaire(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(getAppVersionName(context) + Version_Suffix, false);
    }

    private static String getAppVersionName(Context context) {
        String versionName = "-1";
        try {
            PackageInfo packageInfo = getPackageInfo(context);
            versionName = packageInfo.versionName;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private static PackageInfo getPackageInfo(Context context) throws Exception {
        PackageInfo pi = null;
        PackageManager pm = context.getPackageManager();
        pi = pm.getPackageInfo(context.getPackageName(),
                PackageManager.GET_CONFIGURATIONS);
        return pi;
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("问卷", "shouldOverrideUrlLoading:     " + url);
            if (url.contains("display/dc/?project")) {
                finish();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Log.d("问卷", "shouldInterceptRequest:     " + url);
            if (url.contains("s?z=wenjuan&c=1")) {

                if (!isCurrentVersionQuestionnaire(WenJuanActivity.this)) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(WenJuanActivity.this);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(getAppVersionName(WenJuanActivity.this) + Version_Suffix, true);
                    editor.commit();
                }

                if (mPresent != null) {
                    mPresent.onSuccessfulPresent(WenJuanActivity.this);
                }else {
                    Intent intent = new Intent(WenJuanActivity.this, WenJuanSendSuccessedActivity.class);
                    intent.putExtra("isWenjuan", true);
                    startActivity(intent);
                }

                finish();
            }
            return super.shouldInterceptRequest(view, url);
        }

    }

}
