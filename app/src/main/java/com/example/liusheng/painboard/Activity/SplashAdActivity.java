package com.example.liusheng.painboard.Activity;

import com.example.liusheng.painboard.R;
import com.lafonapps.common.BaseSplashAdActivity;

public class SplashAdActivity extends BaseSplashAdActivity{

    @Override
    public int getSplashImage() {
        return R.drawable.launch_image;
    }

    @Override
    public Class getTargetActivity() {
        return MainActivity.class;
    }
}
