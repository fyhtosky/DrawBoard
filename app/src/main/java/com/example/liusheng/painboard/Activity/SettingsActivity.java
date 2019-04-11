package com.example.liusheng.painboard.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.liusheng.painboard.R;
import com.lafonapps.common.feedback.activity.FeedbackInputActivity;
import com.lafonapps.common.feedback.activity.WenJuanActivity;

/***
 * 设置
 */

public class SettingsActivity extends MyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        showBannerAd();

        transparentStatusBar();
    }

    public void onSettingsClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:
                finish();
                break;

            case R.id.score:
                //showRateDialog();
                gotoRate();
                break;
            case R.id.yijian:

                startActivity(new Intent(this, FeedbackInputActivity.class));

                break;
            case R.id.questionnaire:
                questionnaire();
                break;

            case R.id.qq:
                navigateToQQ(QQ_NUMBER);
                break;
            default:

                break;
        }
    }

    static final String QQ_NUMBER = "3213640836";
    private void navigateToQQ(String qqNum) {
        try {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            toast("当前手机未安装'手机QQ'聊天工具");
        }

    }

    private void questionnaire() {
        WenJuanActivity.present(this, "https://www.wenjuan.in/s/YJZjayd/");
    }

    //评分
    private void gotoRate() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "尚未安装应用市场，无法评分", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.settings_banner_view);
        }
        return bannerViewContainer;
    }
}
