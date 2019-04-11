package com.example.liusheng.painboard.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.ScenesChooseCallback;

/**
 * Created by liusheng on 2017/10/30.
 */

public class ScenesChooseView extends LinearLayout {

    ScenesChooseCallback cb;

    public void setScenesChooseCallback(ScenesChooseCallback callback) {
        this.cb = callback;
    }

    public ScenesChooseView(Context context) {
        super(context);
        init(context);
    }

    public ScenesChooseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_scenes, this);
    }

}
