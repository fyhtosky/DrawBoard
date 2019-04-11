package com.example.liusheng.painboard.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.liusheng.painboard.Adapter.LearnTemplateAdapter;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.ItemDecoration;
import com.example.liusheng.painboard.bean.LearnGroupBean;
import com.example.liusheng.painboard.bean.LearnItemBean;
import com.example.liusheng.painboard.constant.LearnMaterialConstant;

import java.util.ArrayList;
import java.util.List;

public class LearnTemplatePickActivity extends MyActivity implements TabLayout.OnTabSelectedListener {


    private static final String TAG = "LearnPickActivity";

    public static final String LEARN_TEMPLATE_KEY = "learn_template_key";
    public static final String LEARN_GUIDE_KEY = "learn_guide_key";

    LearnTemplateAdapter mAdapter;

    RecyclerView mRecyclerView;
    TabLayout mTabLayout;

    List<LearnItemBean> listData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_template_pick);

        showBannerAd();

        transparentStatusBar();
        init();
    }

    private void init() {
        mTabLayout = findViewById(R.id.tab_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new ItemDecoration());

        List<LearnGroupBean> materials = LearnMaterialConstant.getClassifyMaterials();
        if (materials.size() <= 0) {
            return;
        }
        for (LearnGroupBean groupBean : materials) {
            mTabLayout.addTab(mTabLayout.newTab().setText(groupBean.getGroupName()));
        }
        mTabLayout.addOnTabSelectedListener(this);
        listData.clear();
        listData.addAll(materials.get(0).getItemList());
        mAdapter = new LearnTemplateAdapter(this, listData, new LearnTemplateAdapter.Callback() {
            @Override
            public void onItemImageClick(View view, int backgroundId, int guideId) {
                Intent intent = new Intent(LearnTemplatePickActivity.this, LearnActivity.class);
                intent.putExtra(LEARN_TEMPLATE_KEY, backgroundId);
                intent.putExtra(LEARN_GUIDE_KEY, guideId);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }


    public void onLearnTemplateClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.learn_pick_banner_view);
        }
        return bannerViewContainer;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        try {
            int position = tab.getPosition();
            List<LearnGroupBean> classifyMaterials = LearnMaterialConstant.getClassifyMaterials();
            int size = classifyMaterials.size();
            if (0 <= position && position < size) {
                listData.clear();
                listData.addAll(classifyMaterials.get(position).getItemList());
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
