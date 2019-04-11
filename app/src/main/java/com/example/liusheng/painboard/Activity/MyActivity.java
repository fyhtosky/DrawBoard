package com.example.liusheng.painboard.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liusheng.painboard.Other.MyApplication;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.ShareUtils;
import com.lafonapps.common.BaseActivity;
import com.lafonapps.common.rate.AppRater;

import java.lang.reflect.Field;

/**
 * Created by menghanghang on 2018/1/27.
 */

public class MyActivity extends BaseActivity {

    protected ViewGroup bannerViewContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public ViewGroup getBannerView() {
        return null;
    }

    @Override
    public ViewGroup getFloatView() {
        return null;
    }

    @Override
    public ViewGroup getNativeLView() {
        return null;
    }

    @Override
    public ViewGroup getNativeMView() {
        return null;
    }

    @Override
    public ViewGroup getNativeSView() {
        return null;
    }

    @Override
    public ViewGroup getVedioView() {
        return null;
    }

    @Override
    public ViewGroup getInterstitalView() {
        return null;
    }

    @Override
    public String[] getAdType() {
        return new String[0];
    }

    /**
     * 网络是否联通
     */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return true;
            }
        }
        return false;
    }

    /***
     * 沉浸式模式
     */
    protected void steepMode() {
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * 透明导航栏&&状态栏
     */
    protected void transparentStatusBarAndNavigationBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 透明导航栏
     */
    protected void transparentNavigationBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    protected void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    protected void toast(String msg) {
        Toast.makeText(MyApplication.getSharedApplication(), msg, Toast.LENGTH_SHORT).show();
    }


    /***
     * 保存对话框
     */
    protected void showSaveDialog(final Activity activity, final Uri shareUri, final OnDialogListener listener) {
        if (activity == null || activity.isFinishing() || shareUri == null) {
            return;
        }
        final Dialog saveDialog = new Dialog(activity, R.style.Dialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.drawing_save_dialog_layout, null);
        saveDialog.setContentView(contentView);
        contentView.findViewById(R.id.iv_save_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭
                saveDialog.dismiss();
            }
        });
        contentView.findViewById(R.id.iv_save_dialog_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2019/2/21 分享
                saveDialog.dismiss();
                // 分享
                ShareUtils.share(activity, shareUri);
            }
        });
        contentView.findViewById(R.id.bt_save_dialog_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDialog.dismiss();
                // 评分
                AppRater.defaultAppRater.goRating(activity);
            }
        });

        contentView.findViewById(R.id.bt_save_dialog_to_work).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 画本
                saveDialog.dismiss();
                startActivity(new Intent(MyActivity.this, WorksActivity.class));
                finish();
            }
        });
        saveDialog.show();
    }

    protected void showSaveDialog(final Activity activity, final Uri shareUri) {
        showSaveDialog(activity, shareUri, null);
    }


    /**
     * 重置对话框
     */
    protected void showResetDialog(Activity activity, String content, final OnDialogListener listener) {
        // TODO: 2019/2/25 重置确认对话框
        if (activity == null) {
            return;
        }
        final Dialog resetDialog = new Dialog(activity, R.style.Dialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.drawing_clean_dialog_layout, null);
        resetDialog.setContentView(contentView);

        TextView title = contentView.findViewById(R.id.tv_clean_dialog_title);
        title.setText(TextUtils.isEmpty(content) ? "确定要重置吗？重置后不可返回并且所有涂色将清除" : content);
        contentView.findViewById(R.id.bt_clean_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDialog.dismiss();
                if (listener != null) {
                    listener.onDismiss();
                }
            }
        });

        contentView.findViewById(R.id.bt_clean_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDialog.dismiss();
                if (listener != null) {
                    listener.onOk();
                }
                //mFillView.reset(optimizeBitmap(getBitmapFromAssets()));
            }
        });


        resetDialog.show();
    }

    /**
     * 重置对话框
     */
    protected void showResetDialog(Activity activity, final OnDialogListener listener) {
        // TODO: 2019/2/25 重置确认对话框
        if (activity == null) {
            return;
        }
        showResetDialog(activity, "确定要重置吗？重置后不可返回并且所有涂色将清除", listener);
    }

    protected void showResetDialog(Activity activity) {
        showResetDialog(activity, null);
    }

    /**
     * 返回 确认对话框
     */
    protected void showCloseDialog(Activity activity) {
        showCloseDialog(activity, null);
    }

    protected void showCloseDialog(Activity activity, final OnDialogListener listener) {
        // TODO: 2019/2/25 返回确认对话框
        if (activity == null) {
            return;
        }
        final Dialog finishDialog = new Dialog(activity, R.style.Dialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.drawing_clean_dialog_layout, null);
        finishDialog.setContentView(contentView);

        TextView title = contentView.findViewById(R.id.tv_clean_dialog_title);
        title.setText("确定要退出吗？");
        contentView.findViewById(R.id.bt_clean_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog.dismiss();
                if (listener != null) {
                    listener.onDismiss();
                }
            }
        });

        contentView.findViewById(R.id.bt_clean_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog.dismiss();
                if (listener != null) {
                    listener.onOk();
                } else {
                    finish();
                }
            }
        });

        finishDialog.show();
    }


    /**
     * 自定义TabLayout的下划线长度.
     *
     * @param tab
     * @param left
     * @param right
     */
    protected void setTabLine(TabLayout tab, int left, int right) {
        try {
            Class<?> tabLayout = tab.getClass();
            Field tabStrip = tabLayout.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
            LinearLayout ll_tab = (LinearLayout) tabStrip.get(tab);
            int marginStart = dp2px(left);
            int marginEnd = dp2px(right);
            for (int i = 0; i < ll_tab.getChildCount(); i++) {
                View child = ll_tab.getChildAt(i);
                child.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                //修改两个tab的间距
                params.setMarginStart(marginStart);
                params.setMarginEnd(marginEnd);
                child.setLayoutParams(params);
                child.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }


    protected interface OnDialogListener {
        void onDismiss();

        void onOk();
    }


}
