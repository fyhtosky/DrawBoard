package com.example.liusheng.painboard.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.draw.DrawAttribute;
import com.example.liusheng.painboard.event.ShapeFragmentEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 图形选择
 * Created by chenzhen on 2019/3/21.
 */

public class ShapeSelectFragment extends BaseFragment implements View.OnClickListener {


    public static final String TAG = ShapeSelectFragment.class.getSimpleName();

    public static ShapeSelectFragment newInstance(String tag) {
        ShapeSelectFragment fragment = new ShapeSelectFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    ImageView ivRect, ivCircle, ivLine,ivTrangel;

    @Override
    protected int generateLayoutId() {
        return R.layout.fragment_shape_select;
    }

    @Override
    protected void init() {

        ivRect = rootView.findViewById(R.id.iv_rect);
        ivCircle = rootView.findViewById(R.id.iv_circle);
        ivLine = rootView.findViewById(R.id.iv_line);
        ivTrangel = rootView.findViewById(R.id.iv_trangle);

        ivRect.setOnClickListener(this);
        ivCircle.setOnClickListener(this);
        ivLine.setOnClickListener(this);
        ivTrangel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        reset();
        v.setBackgroundColor(Color.RED);
        switch (v.getId()) {
            //矩形
            case R.id.iv_rect:
                EventBus.getDefault().post(new ShapeFragmentEvent(TAG, DrawAttribute.DrawStatus.JUXING));
                break;

            //圆形
            case R.id.iv_circle:
                EventBus.getDefault().post(new ShapeFragmentEvent(TAG, DrawAttribute.DrawStatus.CIRCLE));
                break;
            //直线
            case R.id.iv_line:
                EventBus.getDefault().post(new ShapeFragmentEvent(TAG, DrawAttribute.DrawStatus.XIAN));
                break;

            //直线
            case R.id.iv_trangle:
                EventBus.getDefault().post(new ShapeFragmentEvent(TAG, DrawAttribute.DrawStatus.TRIANGLE));
                break;

        }
    }

    void reset() {
        if (ivRect == null || ivCircle == null || ivLine == null || ivTrangel == null) {
            return;
        }
        ivRect.setBackgroundColor(Color.BLACK);
        ivCircle.setBackgroundColor(Color.BLACK);
        ivLine.setBackgroundColor(Color.BLACK);
        ivTrangel.setBackgroundColor(Color.BLACK);
    }


    @Override
    public void doOthers() {
        super.doOthers();
        reset();
    }
}
