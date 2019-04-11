package com.example.liusheng.painboard.fragment;

import android.os.Bundle;

import com.example.liusheng.painboard.R;

/**
 * 橡皮擦
 * Created by chenzhen on 2019/3/21.
 */

public class BlankFragment extends BaseFragment {


    public static final String TAG = BlankFragment.class.getSimpleName();

    public static BlankFragment newInstance(String tag) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int generateLayoutId() {
        return R.layout.fragment_blank;
    }

    @Override
    protected void init() {

    }
}
