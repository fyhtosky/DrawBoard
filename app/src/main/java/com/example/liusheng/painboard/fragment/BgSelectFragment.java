package com.example.liusheng.painboard.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.liusheng.painboard.Adapter.LinearHorizontalAdapter;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.View.SpaceItemDecoration;
import com.example.liusheng.painboard.event.BgFragmentEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 背景选择
 * Created by chenzhen on 2019/3/21.
 */

public class BgSelectFragment extends BaseFragment {


    public static final String TAG = BgSelectFragment.class.getSimpleName();

    public static BgSelectFragment newInstance(String tag) {
        BgSelectFragment fragment = new BgSelectFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int generateLayoutId() {
        return R.layout.fragment_bg_select;
    }

    @Override
    protected void init() {

        RecyclerView recyclerView = rootView.findViewById(R.id.hlv_bg_selector);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 10;
                outRect.top = 10;
            }
        });
        LinearHorizontalAdapter adapter = new LinearHorizontalAdapter(getActivity(), getBg(), new LinearHorizontalAdapter.Callback() {
            @Override
            public void onItemImageClick(View view, int position, int resId) {
                EventBus.getDefault().post(new BgFragmentEvent(TAG, position));
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private List<Integer> getBg() {
        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.drawable.a_01_xiangce);
        colorList.add(R.drawable.bg_image_preview_1);
        colorList.add(R.drawable.bg_image_preview_2);
        colorList.add(R.drawable.bg_image_preview_3);
        colorList.add(R.drawable.bg_image_preview_4);
        colorList.add(R.drawable.bg_image_preview_5);
        colorList.add(R.drawable.bg_image_preview_6);
        colorList.add(R.drawable.bg_image_preview_7);
        colorList.add(R.drawable.bg_image_preview_8);
        colorList.add(R.drawable.bg_image_preview_9);
        colorList.add(R.drawable.bg_image_preview_10);
        colorList.add(R.drawable.bg_image_preview_11);
        colorList.add(R.drawable.bg_image_preview_12);
        colorList.add(R.drawable.bg_image_preview_13);
        colorList.add(R.drawable.bg_image_preview_14);
        colorList.add(R.drawable.bg_image_preview_15);
        colorList.add(R.drawable.bg_image_preview_16);
        colorList.add(R.drawable.bg_image_preview_17);
        colorList.add(R.drawable.bg_image_preview_18);
        colorList.add(R.drawable.bg_image_preview_19);
        colorList.add(R.drawable.bg_image_preview_20);
        colorList.add(R.drawable.bg_image_preview_21);
        colorList.add(R.drawable.bg_image_preview_22);
        colorList.add(R.drawable.bg_image_preview_23);
        colorList.add(R.drawable.bg_image_preview_24);
        colorList.add(R.drawable.bg_image_preview_25);
        colorList.add(R.drawable.bg_image_preview_26);
        colorList.add(R.drawable.bg_image_preview_27);
        colorList.add(R.drawable.bg_image_preview_28);
        colorList.add(R.drawable.bg_image_preview_29);
        colorList.add(R.drawable.bg_image_preview_30);
        return colorList;
    }
}
