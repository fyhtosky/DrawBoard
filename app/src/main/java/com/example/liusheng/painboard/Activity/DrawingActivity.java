package com.example.liusheng.painboard.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.View.CustomView;
import com.example.liusheng.painboard.View.MyDrawView;
import com.example.liusheng.painboard.View.TrajectoryView;
import com.example.liusheng.painboard.View.VerticalSeekBar;
import com.example.liusheng.painboard.constant.AdConstant;
import com.example.liusheng.painboard.draw.ColorUtils;
import com.example.liusheng.painboard.draw.DrawAttribute;
import com.example.liusheng.painboard.event.BgFragmentEvent;
import com.example.liusheng.painboard.event.BrushFragmentEvent;
import com.example.liusheng.painboard.event.ColorFragmentEvent;
import com.example.liusheng.painboard.event.FragmentEvent;
import com.example.liusheng.painboard.event.OnUndoEnabledListener;
import com.example.liusheng.painboard.event.ShapeFragmentEvent;
import com.example.liusheng.painboard.fragment.BaseFragment;
import com.example.liusheng.painboard.fragment.BgSelectFragment;
import com.example.liusheng.painboard.fragment.BlankFragment;
import com.example.liusheng.painboard.fragment.BrashSelectFragment;
import com.example.liusheng.painboard.fragment.ColorSelectFragment;
import com.example.liusheng.painboard.fragment.ShapeSelectFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.example.liusheng.painboard.BuildConfig;

/***
 *
 * 画画
 */


public class DrawingActivity extends MyActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "DrawingActivity -- ";

    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle params = new Bundle();


    static final int SMALL_PAINT_SIZE = 1;

    MyDrawView drawView;//画板
    ImageView btn_undo;//撤销
    ImageView btn_redo;//取消撤销

    public static TrajectoryView trajectoryView;

    ImageView btn_save;//保存
    ImageView btn_empty;//清空
    ImageView btn_close;//关闭
    ViewGroup floatViewContainer, container;


    TabLayout mBottomTabLayout;
    FrameLayout mFragmentContainer;

    CustomView drawColorView;
    VerticalSeekBar seekBar;
    View paintSizeView;

    static final int REQUEST_EXTERNAL_STORAGE = 1;
    static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
    };


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        params.putString(FirebaseAnalytics.Param.START_DATE, new Date().toString());
        params.putDouble(FirebaseAnalytics.Param.VALUE, 9.99);
        //隐藏状态栏
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        Window window = getWindow();
        window.setFlags(flag, flag);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        DrawAttribute.screenHeight = dm.heightPixels;
        DrawAttribute.screenWidth = dm.widthPixels;

        if (savedInstanceState == null) {
            BaseFragment brashSelectFragment = BrashSelectFragment.newInstance(BrashSelectFragment.class.getSimpleName());
            fragments.add(brashSelectFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.fl_fragment_container, fragments.get(0), BrashSelectFragment.TAG).commitAllowingStateLoss();
        }
        setContentView(R.layout.activity_drawing);

        EventBus.getDefault().register(this);

        verifyStoragePermissions(this);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setCurrentScreen(this, "主页面", "DrawingActivity");


        findView();//载入所有的按钮实例


        showBannerAd();
        dispatchFloatAd();//广告分发

        setTabLayout();
        setFragment();

        //drawView.setPattern("");
    }


    private void findView() {

        mBottomTabLayout = findViewById(R.id.bottom_tab_layout);
        mFragmentContainer = findViewById(R.id.fl_fragment_container);

        container = findViewById(R.id.fl_container);

        //画板整体布局
        drawView = findViewById(R.id.draw_view);

        btn_undo = findViewById(R.id.btn_undo);
        btn_redo = findViewById(R.id.btn_redo);

        btn_undo.setEnabled(false);
        btn_redo.setEnabled(false);

        btn_save = findViewById(R.id.btn_save);
        btn_empty = findViewById(R.id.btn_empty);

        btn_close = findViewById(R.id.close);

        trajectoryView = findViewById(R.id.trajectoryView);

        //画笔颜色大小
        drawColorView = findViewById(R.id.color_view);
        seekBar = findViewById(R.id.verticalSeekBar);

        paintSizeView = findViewById(R.id.v_draw_paint_size);

        drawColorView.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.caise_icon));

        //随机色
        ColorUtils.colorRandom = true;

        btn_undo.setOnClickListener(this);
        btn_redo.setOnClickListener(this);
        btn_empty.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        drawView.drawStatus = DrawAttribute.DrawStatus.NORMAL;
        drawView.setOnUndoEnabledListener(new OnUndoEnabledListener() {
            @Override
            public void onUndoEnabled(boolean enabled) {
                btn_undo.setEnabled(enabled);
            }

            @Override
            public void onRedoEnabled(boolean enabled) {
                btn_redo.setEnabled(enabled);
            }

            @Override
            public void onAllDisabled() {
                btn_undo.setEnabled(false);
                btn_redo.setEnabled(false);
            }
        });
        seekBar.setOrientation(0);
        seekBar.setUnSelectColor(Color.parseColor("#AAAAAA"));
        seekBar.setSelectColor(Color.parseColor("#EC427C"));
        seekBar.setThumb(R.drawable.ic_vsb_thumb);
        seekBar.setThumbSize(12,12);
        seekBar.setMaxProgress(60);
        seekBar.setProgress(15);
        seekBar.setOnSlideChangeListener(new VerticalSeekBar.SlideChangeListener() {
            @Override
            public void onStart(VerticalSeekBar slideView, int progress) {
                paintSizeView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(VerticalSeekBar verticalSeekBar, int progress) {
                if (progress <= 0) {
                    progress = 0;
                }
                if (progress > verticalSeekBar.getMaxProgress()) {
                    progress = verticalSeekBar.getMaxProgress();
                }
                int size = progress + SMALL_PAINT_SIZE;
                drawView.setPaintSize(size);
                ViewGroup.LayoutParams layoutParams = paintSizeView.getLayoutParams();
                layoutParams.width = size;
                layoutParams.height = size;
                paintSizeView.setLayoutParams(layoutParams);
            }

            @Override
            public void onStop(VerticalSeekBar slideView, int progress) {
                paintSizeView.setVisibility(View.GONE);
            }
        });

    }

    private void dispatchFloatAd() {

        RequestOptions options = new RequestOptions().error(R.drawable.ic_float_ad_error);
        if ("huawei".equalsIgnoreCase(BuildConfig.FLAVOR)) {
            //华为oppo手动广告
            final ImageView floatImageView = findViewById(R.id.float_image_view);
            floatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DrawingActivity.this, AdShowActivity.class);
                    intent.putExtra(AdConstant.KEY_AD_URL, AdConstant.HW_FLOAT_AD_URL);
                    startActivity(intent);
                }
            });
            Glide.with(getApplicationContext()).load(AdConstant.HW_FLOAT_AD_IMAGE_URL).apply(options).into(floatImageView);
        } else if ("vivo".equalsIgnoreCase(BuildConfig.FLAVOR)) {
            //vivo手动广告
            final ImageView floatImageView = findViewById(R.id.float_image_view);
            floatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DrawingActivity.this, AdShowActivity.class);
                    intent.putExtra(AdConstant.KEY_AD_URL, AdConstant.VIVO_FLOAT_AD_URL);
                    startActivity(intent);
                }
            });
            Glide.with(getApplicationContext()).load(AdConstant.VIVO_FLOAT_AD_IMAGE_URL).apply(options).into(floatImageView);
        }else if ("oppo".equalsIgnoreCase(BuildConfig.FLAVOR)) {
            final ImageView floatImageView = findViewById(R.id.float_image_view);
            floatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DrawingActivity.this, AdShowActivity.class);
                    intent.putExtra(AdConstant.KEY_AD_URL, AdConstant.OPPO_FLOAT_AD_URL);
                    startActivity(intent);
                }
            });
            Glide.with(getApplicationContext()).load(AdConstant.OPPO_FLOAT_AD_IMAGE_URL).apply(options).into(floatImageView);
        } else if ("xiaomi".equalsIgnoreCase(BuildConfig.FLAVOR)) {
            final ImageView floatImageView = findViewById(R.id.float_image_view);
            floatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DrawingActivity.this, AdShowActivity.class);
                    intent.putExtra(AdConstant.KEY_AD_URL, AdConstant.MI_FLOAT_AD_URL);
                    startActivity(intent);
                }
            });
            Glide.with(getApplicationContext()).load(AdConstant.MI_FLOAT_AD_IMAGE_URL).apply(options).into(floatImageView);
        }  else {
            findViewById(R.id.float_image_view).setVisibility(View.GONE);
            showFloatAd();
        }

        findViewById(R.id.float_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeAllViews();
                container.setVisibility(View.GONE);
            }
        });
    }


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * fragment 切换 回调
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFragmentSelectEvent(FragmentEvent event) {
        if (event == null || drawView == null) {
            return;
        }
        if (event instanceof BrushFragmentEvent) {
            //笔刷
            BrushFragmentEvent brushFragmentEvent = (BrushFragmentEvent) event;
            DrawAttribute.DrawStatus drawStatus = brushFragmentEvent.getDrawStatus();
            drawView.setBrushBitmap(drawStatus);
            if (drawStatus == DrawAttribute.DrawStatus.BITMAP) {
                drawView.setPattern(brushFragmentEvent.getResId());
            }

        } else if (event instanceof ColorFragmentEvent) {
            // 颜色选择
            ColorFragmentEvent colorFragmentEvent = (ColorFragmentEvent) event;
            int selectIndex = colorFragmentEvent.getSelectIndex();
            if (selectIndex == 0) {
                drawColorView.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.caise_icon));
            } else {
                drawColorView.setColor(ColorUtils.color);
            }

        } else if (event instanceof ShapeFragmentEvent) {
            //形状
            ShapeFragmentEvent shapeFragmentEvent = (ShapeFragmentEvent) event;
            drawView.setBrushBitmap(shapeFragmentEvent.getDrawStatus());

        } else if (event instanceof BgFragmentEvent) {
            //背景图
            BgFragmentEvent bgFragmentEvent = (BgFragmentEvent) event;
            int index = bgFragmentEvent.getSelectIndex();
            if (index == 0) {
                showPickBgDialog(DrawingActivity.this);
            } else {
                drawView.setBackgroundBitmap(DrawAttribute.getImageFromAssetsFile(DrawingActivity.this,
                        "bg_" + "image_" + index + ".jpeg", true), true);
            }
        }

    }


    /**
     * tabLayout 图标
     */
    static final int[] tabIcons = {
            R.drawable.tap_hb,
            R.drawable.tap_ys,
            R.drawable.tap_xp,
            R.drawable.tap_xz,
            R.drawable.tap_bg

    };

    private void setTabLayout() {

        mBottomTabLayout.addTab(mBottomTabLayout.newTab());
        mBottomTabLayout.addTab(mBottomTabLayout.newTab());
        mBottomTabLayout.addTab(mBottomTabLayout.newTab());
        mBottomTabLayout.addTab(mBottomTabLayout.newTab());
        mBottomTabLayout.addTab(mBottomTabLayout.newTab());

        int count = mBottomTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            mBottomTabLayout.getTabAt(i).setCustomView(getTabView(i));
        }

        mBottomTabLayout.addOnTabSelectedListener(this);

        setTabLine(mBottomTabLayout, 15, 15);
    }

    List<BaseFragment> fragments = new ArrayList<>();

    private void setFragment() {
        fragments.add(ColorSelectFragment.newInstance(ColorSelectFragment.TAG));
        fragments.add(BlankFragment.newInstance(BlankFragment.TAG));
        fragments.add(ShapeSelectFragment.newInstance(ShapeSelectFragment.TAG));
        fragments.add(BgSelectFragment.newInstance(BgSelectFragment.TAG));
    }


    /**
     * fragment 切换
     *
     * @param from
     * @param to
     */
    void switchFragment(BaseFragment from, BaseFragment to) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (!to.isAdded()) {
            transaction.hide(from).add(R.id.fl_fragment_container, to, to.getClass().getSimpleName()).commitAllowingStateLoss();
        } else {
            transaction.hide(from).show(to).commitAllowingStateLoss();
        }
    }

    int fromPosition = 0;

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        BaseFragment from = fragments.get(fromPosition);
        BaseFragment to = fragments.get(position);
        to.doOthers();
        switchFragment(from, to);
        checkEraser(position);
    }


    /**
     * 判断是否是橡皮擦模式
     *
     * @param position
     */
    private void checkEraser(int position) {
        if (position == 2) {
            drawView.setBrushBitmap(DrawAttribute.DrawStatus.ERASER);
        }
    }

    void checkClose() {
        if (drawView.canExit()){
            finish();
        }else {
            showCloseDialog(this);
        }
    }

    @Override
    public void onBackPressed() {
        checkClose();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        fromPosition = tab.getPosition();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    LocalMedia media = selectList.get(0);
                    String path;
                    if (media.isCompressed()) {
                        path = media.getCompressPath();
                    } else if (media.isCut()) {
                        path = media.getCutPath();
                    } else {
                        path = media.getPath();
                    }

                    //Bitmap bitmap = BitmapFactory.decodeFile(path);
                    Glide.with(getApplicationContext()).asBitmap().load(path).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (resource != null) {
                                drawView.setBackgroundBitmap(resource, false);
                            }
                        }
                    });
                    //Log.v("qqqq", String.valueOf(bitmap.getByteCount()));
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close) {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
            checkClose();
        } else if (id == R.id.btn_empty) {
            showClearDialog(this);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

        } else if (id == R.id.btn_save) {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
            Uri shariUri = drawView.saveBitmap(false);
            if (shariUri == null) {
                toast("保存失败");
                return;
            }
            showSaveDialog(this, shariUri);
        } else if (id == R.id.btn_undo) {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
            drawView.undo();
        } else if (id == R.id.btn_redo) {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
            drawView.redo();
        }
    }

    /**
     * 选择背景图片对话框
     */
    private void showPickBgDialog(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        final Dialog pickDialog = new Dialog(activity, R.style.Dialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.drawing_pick_bg_dialog_layout, null);
        pickDialog.setContentView(contentView);

        contentView.findViewById(R.id.tv_pick_dialog_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDialog.dismiss();
                //相册
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
                openAlbum();
            }
        });
        contentView.findViewById(R.id.tv_pick_dialog_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDialog.dismiss();
                //相机
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

                openCamera();

            }
        });
        contentView.findViewById(R.id.bt_pick_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDialog.dismiss();
            }
        });

        pickDialog.show();
    }


    /**
     * 打开相册
     */
    private void openAlbum() {
        PictureSelector.create(DrawingActivity.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .theme(R.style.picture_default_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(false)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true可不填
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,
                .enableCrop(true)// 是否裁剪 true or false
                .withAspectRatio(3, 4)
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .compress(true)// 是否压缩 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }


    /**
     * 打开相机
     */
    private void openCamera() {
        PictureSelector.create(DrawingActivity.this)
                .openCamera(PictureMimeType.ofImage())
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(true)// 是否裁剪 true or false
                .withAspectRatio(3, 4)
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .compress(true)// 是否压缩 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }


    /**
     * 擦出手绘对话框
     */
    private void showClearDialog(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        final Dialog cleanDialog = new Dialog(activity, R.style.Dialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.drawing_clean_dialog_layout, null);
        cleanDialog.setContentView(contentView);

        contentView.findViewById(R.id.bt_clean_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.bt_clean_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanDialog.dismiss();
                drawView.cleanPaintBitmap();
            }
        });


        cleanDialog.show();
    }


    /**
     * tablayou item 自定义视图
     *
     * @param position
     * @return
     */
    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_view, null);
        ImageView icon = view.findViewById(R.id.iv_tab_icon);
        icon.setImageResource(tabIcons[position]);
        return view;
    }

    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.drawing_banner_view);
        }
        return bannerViewContainer;
    }

    @Override
    public ViewGroup getFloatView() {
        if (floatViewContainer == null) {
            floatViewContainer = findViewById(R.id.float_view);
        }
        return floatViewContainer;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
