package com.example.liusheng.painboard.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chenzhen on 2019/3/21.
 */

public abstract class BaseFragment extends Fragment {


    private static String TAG = BaseFragment.class.getSimpleName();

    protected String tagInfo;
    protected View rootView;

    public BaseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tagInfo = getArguments().getString(TAG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(generateLayoutId(),container,false);
        init();
        return rootView;
    }

    protected abstract int generateLayoutId();
    protected abstract void init();

    public void doOthers(){

    }
}
