package com.example.liusheng.painboard.Activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.View.DynamicSurfaceView;
import com.example.liusheng.painboard.View.DynamicType;

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
 * 动态画画
 */

public class DynamicActivity extends MyActivity {

    static final int MSG_START = 0;
    static final int MSG_END = 1;
    static final int MSG_ERROR = 2;


    DynamicSurfaceView dynamicView;

    List<Integer> shaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dynamic);

        showBannerAd();

        dynamicView = findViewById(R.id.dynamic_view);
        init();
    }

    private void init() {
        dynamicView.setDynamicType(DynamicType.MULTI_RANDOM);

        shaders = Arrays.asList(DynamicSurfaceView.BITMAP_SHADERS);

        RecyclerView recyclerView = findViewById(R.id.hlv_shader_selector);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        MaterialAdapter adapter = new MaterialAdapter(shaders);
        recyclerView.setAdapter(adapter);
    }

    public void onDynamicClick(View view) {
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
                        dynamicView.clear();
                    }
                });
                break;

            case R.id.iv_save:
                //保存
                save();
                break;
        }
    }


    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.dynamic_banner_view);
        }
        return bannerViewContainer;
    }

    void checkClose() {
        if (dynamicView.canExit()) {
            finish();
        } else {
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
                Uri uri = dynamicView.saveBitmap(false);
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
                                showSaveDialog(DynamicActivity.this, shareUri);
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

    boolean isDestroy = false;

    @Override
    protected void onDestroy() {
        isDestroy = true;
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }


    private class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

        List<Integer> materialList;
        int currentIndex = 0;

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
            Glide.with(DynamicActivity.this.getApplicationContext()).asBitmap().load(resId).into(holder.material);

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
                    if (position == 0){
                        dynamicView.setDynamicType(DynamicType.MULTI_RANDOM);
                    }else {
                        dynamicView.setDynamicType(DynamicType.SIMPLE_COLOR_RANDOM);
                        dynamicView.setShaderResId(shaders.get(position));
                    }
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
