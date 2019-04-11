package com.example.liusheng.painboard.Activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.View.MagicView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 粒子画画
 */

public class MagicActivity extends MyActivity implements TabLayout.OnTabSelectedListener {


    static final int MSG_START = 0;
    static final int MSG_END = 1;
    static final int MSG_ERROR = 2;

    MagicView magicView;
    RecyclerView mRecyclerView;
    TabLayout mBottomTabLayout;
    List<Integer> shaders = new ArrayList<>();
    List<List<Integer>> shaderList = new ArrayList<>();

    MaterialAdapter mAdapter;

    static final Integer[] MATERIAL_FIREWORK = {
            R.drawable.p_shader_3,
            R.drawable.p_shader_25, R.drawable.p_shader_26, R.drawable.p_shader_6,
            R.drawable.p_shader_27, R.drawable.p_shader_28, R.drawable.p_shader_8,
    };

    static final Integer[] MATERIAL_DUST = {
            R.drawable.p_shader_5, R.drawable.p_shader_19, R.drawable.p_shader_2,
            R.drawable.p_shader_25, R.drawable.p_shader_26, R.drawable.p_shader_27,
            R.drawable.p_shader_28, R.drawable.p_shader_25, R.drawable.p_shader_1,
            R.drawable.p_shader_4,
    };

    static final Integer[] MATERIAL_STAR = {
            R.drawable.p_shader_25, R.drawable.p_shader_26, R.drawable.p_shader_12,
            R.drawable.p_shader_27, R.drawable.p_shader_28, R.drawable.p_shader_14,
    };

    static final Integer[] MATERIAL_FLOWER = {
            R.drawable.p_shader_14, R.drawable.p_shader_2, R.drawable.p_shader_15,
            R.drawable.p_shader_16, R.drawable.p_shader_7, R.drawable.p_shader_13
    };

    static final String[] MAGIC_TYPE_TITLE = {
            "烟花", "烟雾", "星星", "飞花"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_magic);

        showBannerAd();
        init();
    }

    private void init() {
        magicView = findViewById(R.id.magic_view);
        mRecyclerView = findViewById(R.id.materials_recycler_view);
        mBottomTabLayout = findViewById(R.id.magic_bottom_tab_layout);


        shaderList.clear();
        shaderList.add(Arrays.asList(MATERIAL_FIREWORK));
        shaderList.add(Arrays.asList(MATERIAL_DUST));
        shaderList.add(Arrays.asList(MATERIAL_STAR));
        shaderList.add(Arrays.asList(MATERIAL_FLOWER));

        shaders.clear();
        shaders.addAll(shaderList.get(0));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new MaterialAdapter(shaders);
        mRecyclerView.setAdapter(mAdapter);
        for (String title : MAGIC_TYPE_TITLE) {
            mBottomTabLayout.addTab(mBottomTabLayout.newTab().setText(title));
        }
        setTabLine(mBottomTabLayout, 15, 15);
        mBottomTabLayout.addOnTabSelectedListener(this);
    }

    public void onMagicClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:
                //返回
                checkClose();
                break;

            case R.id.iv_undo:
                // 撤销
                break;
            case R.id.iv_redo:
                //反撤销

                break;
            case R.id.iv_reset:
                //重置
                showResetDialog(this, "确定要清空画板吗？", new OnDialogListener() {
                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onOk() {
                        magicView.clear();
                    }
                });
                break;

            case R.id.iv_save:
                //保存
                save();
                break;

        }
    }

    void checkClose() {
        if (magicView.canExit()){
            finish();
        }else {
            showCloseDialog(this);
        }
    }

    @Override
    public void onBackPressed() {
        checkClose();
    }

    Disposable mDisposable;
    Uri shareUri;

    private void save() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(MSG_START);
                Uri uri = magicView.saveBitmap(false);
                if (uri != null) {
                    shareUri = uri;
                    e.onNext(MSG_END);
                } else {
                    e.onNext(MSG_ERROR);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                if (integer != null) {
                    int id = integer.intValue();
                    switch (id) {
                        case MSG_START:

                            break;

                        case MSG_END:
                            if (!isDestroy) {
                                showSaveDialog(MagicActivity.this, shareUri);
                            }
                            break;

                        case MSG_ERROR:
                            toast("保存失败");
                            break;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.magic_banner_view);
        }
        return bannerViewContainer;
    }

    boolean isDestroy = false;

    @Override
    protected void onDestroy() {
        isDestroy = true;
        shaders.clear();
        shaderList.clear();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int magicType = tab.getPosition();
        magicView.setMagicType(magicType);
        shaders.clear();
        shaders.addAll(shaderList.get(magicType));
        if (mAdapter != null) {
            mAdapter.setCurrentIndex(-1);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    private class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

        List<Integer> materialList;
        int currentIndex = -1;

        public void setCurrentIndex(int index) {
            currentIndex = index;
        }

        public MaterialAdapter(List<Integer> list) {
            this.materialList = list;
        }

        @Override
        public MaterialAdapter.MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_item_layout, parent, false);
            return new MaterialAdapter.MaterialViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MaterialAdapter.MaterialViewHolder holder, final int position) {
            final int resId = materialList.get(position);
            Glide.with(MagicActivity.this.getApplicationContext()).asBitmap().load(resId).into(holder.material);
//            if (currentIndex == position) {
//                holder.material.setSelected(true);
//            } else {
//                holder.material.setSelected(false);
//            }
            if (currentIndex == position) {
                holder.material.setBackgroundColor(Color.RED);
            } else {
                holder.material.setBackgroundColor(Color.BLACK);
            }
            holder.material.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    magicView.setMagicResId(resId);
                    int lastIndex = currentIndex;
                    currentIndex = position;
                    notifyItemChanged(currentIndex);
                    if (lastIndex != -1) {
                        notifyItemChanged(lastIndex);
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return this.materialList != null ? materialList.size() : 0;
        }

        class MaterialViewHolder extends RecyclerView.ViewHolder {

            ImageView material;

            public MaterialViewHolder(View itemView) {
                super(itemView);
                material = itemView.findViewById(R.id.iv_material);
            }
        }
    }
}
