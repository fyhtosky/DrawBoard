package com.example.liusheng.painboard.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.StorageInSDCard;
import com.example.liusheng.painboard.View.CustomView;
import com.example.liusheng.painboard.View.FillImageView;
import com.example.liusheng.painboard.constant.Constant;
import com.example.liusheng.painboard.event.OnUndoEnabledListener;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/****
 * 涂色
 */
public class ColoringActivity extends MyActivity {


    static final String TAG = ColoringActivity.class.getSimpleName();

    static final int MSG_START = 0;
    static final int MSG_ERROR = 1;
    static final int MSG_END = 2;

    FillImageView mFillView;
    RecyclerView mRecyclerView;

    ImageView ivUndo, ivRedo;

    String imagePath, imageName;
    RequestOptions mOptions;
    ColorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_coloring);

        showBannerAd();

        initView();
        initData();
    }

    private void initView() {
        mFillView = findViewById(R.id.color_fill_view);
        mRecyclerView = findViewById(R.id.recycler_view);
        ivUndo = findViewById(R.id.iv_undo);
        ivRedo = findViewById(R.id.iv_redo);

        ivUndo.setEnabled(false);
        ivRedo.setEnabled(false);


        mFillView.setOnUndoEnabledListener(new OnUndoEnabledListener() {
            @Override
            public void onUndoEnabled(boolean enabled) {
                ivUndo.setEnabled(enabled);
            }

            @Override
            public void onRedoEnabled(boolean enabled) {
                ivRedo.setEnabled(enabled);
            }

            @Override
            public void onAllDisabled() {
                ivUndo.setEnabled(false);
                ivRedo.setEnabled(false);
            }
        });

        SharedPreferences sp = getSharedPreferences("start", MODE_PRIVATE);

        firstStart = sp.getBoolean("ShowGuideView", true);

        if (firstStart) {
            // 第一次 启动 显示遮罩
            mFillView.post(new Runnable() {
                @Override
                public void run() {
                    showHelpDialog(ColoringActivity.this);
                }
            });
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("ShowGuideView", false);
            editor.apply();
            firstStart = false;
        }


    }


    boolean firstStart = false;

    private void initData() {

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        imageName = intent.getStringExtra(Constant.KEY_IMAGE_NAME);
        imagePath = intent.getStringExtra(Constant.KEY_IMAGE_PATH);
        if (imagePath == null) {
            return;
        }
        mOptions = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
        mFillView.setUpdatePath(imagePath);
        mFillView.setRandomColor();
        mFillView.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(ColoringActivity.this.getApplicationContext()).asBitmap().apply(mOptions).load(imagePath).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        //此处主要解决图片过大 某些内存较小手机 涂色过程中导致OOM问题
                        //对图片惊醒裁剪
                        mFillView.setBitmap(optimizeBitmap(resource), true);
                    }
                });
            }
        });

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(10, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(10, 10, 10, 10);
            }
        });
        mAdapter = new ColorAdapter(getColors());
        mRecyclerView.setAdapter(mAdapter);
    }


    private void showHelpDialog(Activity activity) {


        if (activity == null) {
            return;
        }
        final Dialog helpDialog = new Dialog(activity, R.style.Dialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.coloring_help_dialog_layout, null);
        helpDialog.setContentView(contentView);

        contentView.findViewById(R.id.bt_help_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog.dismiss();
            }
        });

        helpDialog.show();
    }


    /**
     * 此处主要解决图片过大 某些内存较小手机 涂色过程中导致OOM问题
     * 对图片惊醒裁剪
     * 大图片优化
     *
     * @param resource 需要优化的大图
     */
    private Bitmap optimizeBitmap(Bitmap resource) {
        if (resource == null || resource.isRecycled()) {
            return null;
        }
        //图片宽高
        float bw = resource.getWidth();
        float bh = resource.getHeight();
        //视图宽高
        float vw = mFillView.getWidth();
        float vh = mFillView.getHeight();
        //处理 图片宽高>=视图宽高
        if (bw >= vw || bh >= vh) {
            try {
                float radio = bw / (vw / 1.5f); //以图片宽和视图宽计算作为缩放比例 缩放为视图一半
                if (radio == 1 || radio == 0) {
                    radio = 1.5f;
                }
                float dstWidth = bw / radio;
                float dstHeight = bh / radio;
                Bitmap bitmap = Bitmap.createScaledBitmap(resource, (int) dstWidth, (int) dstHeight, false);
                if (resource != null && !resource.isRecycled()) {
                    resource.recycle();
                }
                Log.e(TAG, "optimizeBitmap " + radio);
                return bitmap;
            } catch (Exception e) {
                return resource;
            }
        } else {
            return resource;
        }
    }


    public void onColoringClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:
                //返回
                checkClose();
                break;

            case R.id.iv_undo:
                // 撤销
                mFillView.undo();
                break;
            case R.id.iv_redo:
                //反撤销
                mFillView.redo();
                break;
            case R.id.iv_reset:
                //重置
                showResetDialog(this, new OnDialogListener() {
                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onOk() {
                        mFillView.reset(optimizeBitmap(getBitmapFromAssets()));
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
        if (mFillView.hasReset() ) {
            finish();
        } else {
            showCloseDialog(this);
        }
    }

    @Override
    public void onBackPressed() {
        checkClose();
    }

    /***
     * 保存图片
     */
    Disposable mDisposable;
    Bitmap updateBitmap, saveBitmap;
    Uri shareUri;

    private void save() {
        updateBitmap = mFillView.getUpdateBitmap();
        saveBitmap = mFillView.getSaveBitmap();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(MSG_START);
                boolean result = StorageInSDCard.updateBitmap(updateBitmap, imagePath);
                Uri uri = StorageInSDCard.saveBitmapInExternalStorage(saveBitmap, ColoringActivity.this, false);
                if (result && uri != null) {
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
                            EventBus.getDefault().post(new Object());
                            showSaveDialog(ColoringActivity.this, shareUri);
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
    protected void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }

    Bitmap getBitmapFromAssets() {
        try {
            InputStream inputStream = getAssets().open(Constant.TEMPLATE_ASSETS_DIR_NAME + File.separator + imageName);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private List<String> getColors() {
        return Arrays.asList(
                "#FFC0CB", "#DC143C", "#DB7093", "#FF69B4", "#FF1493", "#C71585", "#DA70D6", "#D8BFD8",
                "#DDA0DD", "#EE82EE", "#FF00FF", "#FF00FF", "#8B008B", "#800080", "#BA55D3", "#9400D3", "#9932CC", "#4B0082", "#8A2BE2",
                "#9370DB", "#7B68EE", "#6A5ACD", "#483D8B", "#0000FF", "#0000CD", "#191970", "#00008B", "#000080",
                "#4169E1", "#6495ED", "#778899", "#708090", "#1E90FF", "#4682B4", "#87CEFA", "#87CEEB", "#00BFFF"
                , "#5F9EA0", "#AFEEEE", "#00FFFF", "#00FFFF", "#00CED1", "#2F4F4F", "#008B8B",
                "#008080", "#48D1CC", "#20B2AA", "#40E0D0", "#7FFFAA", "#00FA9A", "#00FF7F", "#3CB371", "#2E8B57", "#F0FFF0",
                "#90EE90", "#98FB98", "#8FBC8F", "#32CD32", "#00FF00", "#228B22", "#008000", "#006400", "#7FFF00", "#7CFC00", "#ADFF2F",
                "#FFFF00", "#808000", "#BDB76B", "#FFFACD", "#EEE8AA", "#F0E68C",
                "#FFD700", "#DAA520", "#F5DEB3", "#FFE4B5", "#FFA500", "#FFEFD5", "#FFEBCD", "#FFDEAD",
                "#FAEBD7", "#D2B48C", "#DEB887", "#FFE4C4", "#FF8C00", "#FAF0E6", "#CD853F", "#FFDAB9", "#F4A460", "#D2691E", "#8B4513",
                "#A0522D", "#FFA07A", "#FF7F50", "#FF4500", "#E9967A", "#FF6347", "#FFE4E1", "#FA8072", "#F08080",
                "#BC8F8F", "#CD5C5C", "#FF0000", "#A52A2A", "#B22222", "#8B0000", "#800000", "#DCDCDC", "#D3D3D3",
                "#C0C0C0", "#A9A9A9", "#808080", "#696969", "#000000", "#000000"
        );

    }

    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.coloring_banner_view);
        }
        return bannerViewContainer;
    }

    public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {


        int currentIndex = 0;
        List<String> colorList;

        public ColorAdapter(List<String> colorList) {
            this.colorList = colorList;
        }

        @Override
        public ColorAdapter.ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_selector_view, null);
            return new ColorAdapter.ColorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ColorAdapter.ColorViewHolder holder, final int position) {
            if (position == 0) {
                Glide.with(ColoringActivity.this.getApplicationContext()).asBitmap().load(R.drawable.caise_icon).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.customView.setImage(resource);
                    }
                });
            } else {
                String color = colorList.get(position);
                holder.customView.setColor(Color.parseColor(color));
            }
            if (currentIndex == position) {
                holder.customView.setSelected(true);
            } else {
                holder.customView.setSelected(false);
            }
            holder.customView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentIndex == position) {
                        return;
                    }
                    if (mFillView != null) {
                        if (position == 0) {
                            mFillView.setRandomColor();
                        } else {
                            mFillView.setColor(Color.parseColor(colorList.get(position)));
                        }
                        int lastIndex = currentIndex;
                        currentIndex = position;
                        notifyItemChanged(currentIndex);
                        if (lastIndex != -1) {
                            notifyItemChanged(lastIndex);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return colorList != null ? colorList.size() : 0;
        }

        class ColorViewHolder extends RecyclerView.ViewHolder {

            CustomView customView;

            public ColorViewHolder(View itemView) {
                super(itemView);

                customView = itemView.findViewById(R.id.color_imageView);
                customView.getBackground().setAlpha(0);
            }
        }
    }


}
