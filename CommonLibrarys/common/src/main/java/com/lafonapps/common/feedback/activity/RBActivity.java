package com.lafonapps.common.feedback.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lafonapps.common.R;
import com.lafonapps.common.util.StatusBarUtil;

/**
 * Created by xiongzhifan on 2018/2/2.
 */

public class RBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setWindowStatusBarColor(this);
        super.onCreate(savedInstanceState);
    }

    public int getStatusBarColor() {
        return R.color.color_clear;
    }

    private void setWindowStatusBarColor(Activity activity) {
        try {
            StatusBarUtil.setTranslucent(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
