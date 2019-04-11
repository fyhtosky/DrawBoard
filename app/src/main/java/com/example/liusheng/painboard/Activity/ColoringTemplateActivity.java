package com.example.liusheng.painboard.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.liusheng.painboard.Adapter.TemplateAdapter;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.FileHelper;
import com.example.liusheng.painboard.Tools.ItemDecoration;
import com.example.liusheng.painboard.Tools.PermissionHelper;
import com.example.liusheng.painboard.bean.TemplateBean;
import com.example.liusheng.painboard.constant.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/***
 * 涂色图片选择
 */

public class ColoringTemplateActivity extends MyActivity implements TabLayout.OnTabSelectedListener {


    static final int MSG_START = 0;
    static final int MSG_END = 1;
    static final int MSG_ERROR = 2;

    List<TemplateBean> mData = new ArrayList<>();
    List<TemplateBean> mClassfyData = new ArrayList<>();
    Disposable mDisposable;
    TemplateAdapter mAdapter;
    boolean isDestroyed = false;

    RecyclerView mRecyclerView;
    TabLayout mTabLayout;

    static final String[] CLASSIFY_TITLES = {
            "所有", "人物", "建筑", "动物", "植物"
    };
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coloring_template);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        showBannerAd();

        transparentStatusBar();
        initView();
    }

    void initView() {
        mTabLayout = findViewById(R.id.tab_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new ItemDecoration());
        mAdapter = new TemplateAdapter(this, mClassfyData, new TemplateAdapter.Callback() {
            @Override
            public void onItemImageClick(View view, int position, String imagePath, String imageName) {
                Intent intent = new Intent(ColoringTemplateActivity.this, ColoringActivity.class);
                intent.putExtra(Constant.KEY_IMAGE_PATH, imagePath);
                intent.putExtra(Constant.KEY_IMAGE_NAME, imageName);
                intent.putExtra(Constant.KEY_IMAGE_POSITION, position);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        for (String title : CLASSIFY_TITLES) {
            mTabLayout.addTab(mTabLayout.newTab().setText(title));
        }

        mTabLayout.addOnTabSelectedListener(this);

        checkPermission();

//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (recyclerView.canScrollVertically(1) == false){
//                    //滑动到底部
//                    Log.e(TAG,"滑动到底部");
//
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy < 0){
//                    //向上滑动
//                    Log.e(TAG,"向上滑动");
//                }
//            }
//        });
    }

    void checkPermission() {
        if (PermissionHelper.hasReadStoragePermission(this)) {
            requestData();
        } else {
            PermissionHelper.requestReadStoragePermission(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults != null && grantResults.length >= 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestData();
                } else {
                    Toast.makeText(this, PermissionHelper.TIPS_WRITE_STORAGE_PERMISSION, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void requestData() {
        mData.clear();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(MSG_START);
                FileHelper fileHelper = FileHelper.getInstance(ColoringTemplateActivity.this);
                boolean result = fileHelper.copyAssetsToDst(Constant.TEMPLATE_ASSETS_DIR_NAME, Constant.TEMPLATE_CACHE_DIR_NAME);
                e.onNext(result ? MSG_END : MSG_ERROR);
                File cache = fileHelper.getTemplateCacheDir(Constant.TEMPLATE_CACHE_DIR_NAME);
                if (!cache.exists()) {
                    e.onNext(MSG_ERROR);
                } else {
                    File[] files = cache.listFiles();
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            TemplateBean bean = new TemplateBean(file.getName(), file.getPath());
                            mData.add(bean);
                        }
                        // 按文件名称整理
                        Collections.sort(mData, new Comparator<TemplateBean>() {
                            @Override
                            public int compare(TemplateBean o1, TemplateBean o2) {
                                String s1 = o1.getName();
                                String s2 = o2.getName();
                                return s1.compareTo(s2);
                            }
                        });
                        mClassfyData.addAll(mData);
                        e.onComplete();
                    } else {
                        e.onNext(MSG_ERROR);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(Integer result) {
                if (result != null) {
                    switch (result.intValue()) {

                        case MSG_START:
                            //toast(" MSG_START ");
                            break;

                        case MSG_END:
                            //toast(" MSG_END ");
                            break;

                        case MSG_ERROR:
                            //toast(" MSG_ERROR ");
                            break;

                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                if (mAdapter != null && !isDestroyed) {
                    //toast(" onComplete ");
                    isReady = true;
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemRefresh(Object obj) {
        requestData();
    }


    public void onTemplateClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.coloring_template_banner_view);
        }
        return bannerViewContainer;
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        mData.clear();
        if (mDisposable != null && mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (isReady) {
            classify(tab.getPosition());
            return;
        }
        mTabLayout.getTabAt(0).select();
    }


    private void classify(int position) {
        int length = CLASSIFY_TITLES.length;
        if (position < 0 || position >= length) {
            return;
        }
        mClassfyData.clear();
        if (position == 0) {
            mClassfyData.addAll(mData);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            return;
        }
        String title = CLASSIFY_TITLES[position];
        if (title == null) {
            return;
        }
        if ("建筑".equals(title)) {
            filterByPrefixName("fw", "a_0", "a_1", "a_2", "a_5", "fj", "jz");
        } else if ("动物".equals(title)) {
            filterByPrefixName("dw", "temp_2", "a_3");
        } else if ("植物".equals(title)) {
            filterByPrefixName("zw", "temp_0", "temp_9", "zh_02");
        } else if ("人物".equals(title)) {
            filterByPrefixName("rw", "jz", "temp_6", "xz", "a_4");
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 根据名称前缀执行分类
     *
     * @param prefixNames
     */
    private void filterByPrefixName(String... prefixNames) {
        for (String prefixName : prefixNames) {
            for (TemplateBean bean : mData) {
                if (bean.getName().startsWith(prefixName)) {
                    mClassfyData.add(bean);
                }
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
