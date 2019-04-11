package com.example.liusheng.painboard.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.ShareUtils;
import com.example.liusheng.painboard.constant.Constant;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * 画本图片预览
 */

public class ImagePreviewActivity extends MyActivity {

    PhotoView mPhotoView;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        showBannerAd();

        transparentStatusBar();
        initViews();
    }

    private void initViews() {
        mPhotoView = findViewById(R.id.photo_view);
        Intent intent = getIntent();
        if (intent == null){
            return;
        }
        path = intent.getStringExtra(Constant.KEY_PREVIEW_IAMGE_PATH);
        if (path == null){
            return;
        }
        Glide.with(getApplicationContext()).load(path).into(mPhotoView);
    }

    public void onPreviewClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_share:
                ShareUtils.share(this,path);
                break;
        }
    }

    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.preview_banner_view);
        }
        return bannerViewContainer;
    }
}
