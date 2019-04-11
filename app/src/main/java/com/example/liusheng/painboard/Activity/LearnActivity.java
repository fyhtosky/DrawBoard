package com.example.liusheng.painboard.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import com.bumptech.glide.Glide;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.View.ColorFillView;
import com.example.liusheng.painboard.draw.ColorUtils;
import com.example.liusheng.painboard.draw.DrawAttribute;
import com.example.liusheng.painboard.event.ColorFragmentEvent;
import com.example.liusheng.painboard.event.FragmentEvent;
import com.example.liusheng.painboard.event.OnUndoEnabledListener;
import com.example.liusheng.painboard.event.ShapeFragmentEvent;
import com.example.liusheng.painboard.fragment.BaseFragment;
import com.example.liusheng.painboard.fragment.BlankFragment;
import com.example.liusheng.painboard.fragment.ColorSelectFragment;
import com.example.liusheng.painboard.fragment.ShapeSelectFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;


/**
 * 学画画
 */

public class LearnActivity extends MyActivity implements TabLayout.OnTabSelectedListener {


    static final String TAG = "LearnActivity";


    ColorFillView fillView;
    ImageView bg, ivArrow, ivGuide;
    SeekBar seekBar;
    View vSize;

    TabLayout mBottomTabLayout;
    ImageView btnUndo, btnRedo;
    LinearLayout guideContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (savedInstanceState == null) {
            BaseFragment colorSelectFragment = ColorSelectFragment.newInstance(ColorSelectFragment.class.getSimpleName());
            fragments.add(colorSelectFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.fl_learn_fragment_container, fragments.get(0), ColorSelectFragment.TAG).commitAllowingStateLoss();
        }
        setContentView(R.layout.activity_learn);

        EventBus.getDefault().register(this);

        showBannerAd();

        initView();
        initData();

    }


    private void initView() {
        ivArrow = findViewById(R.id.iv_arrow);
        ivGuide = findViewById(R.id.iv_guide);
        fillView = findViewById(R.id.cfv_draw_view);
        bg = findViewById(R.id.iv_bg_draw_view);
        seekBar = findViewById(R.id.seek_bar);
        vSize = findViewById(R.id.v_draw_paint_size);
        mBottomTabLayout = findViewById(R.id.bottom_tab_layout);
        guideContainer = findViewById(R.id.hsv_image_guide_container);


        btnUndo = findViewById(R.id.btn_undo);
        btnRedo = findViewById(R.id.btn_redo);

        btnUndo.setEnabled(false);
        btnRedo.setEnabled(false);


    }

    private void initData() {


        int size = seekBar.getProgress() + ColorFillView.SMALL_PAINT_SIZE;
        updateSize(size);

        Intent intent = getIntent();
        int backgroundId = intent.getIntExtra(LearnTemplatePickActivity.LEARN_TEMPLATE_KEY, R.drawable.dw_bg_material_cat);
        int guideId = intent.getIntExtra(LearnTemplatePickActivity.LEARN_GUIDE_KEY, R.drawable.dw_step_material_cat);
        Glide.with(this.getApplicationContext()).load(backgroundId).into(bg);
        Glide.with(this.getApplicationContext()).load(guideId).into(ivGuide);


        fillView.post(new Runnable() {
            @Override
            public void run() {
                fillView.setBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
            }
        });

        fillView.setOnUndoEnabledListener(new OnUndoEnabledListener() {
            @Override
            public void onUndoEnabled(boolean enabled) {
                btnUndo.setEnabled(enabled);
            }

            @Override
            public void onRedoEnabled(boolean enabled) {
                btnRedo.setEnabled(enabled);
            }

            @Override
            public void onAllDisabled() {
                btnUndo.setEnabled(false);
                btnRedo.setEnabled(false);
            }
        });

        setTabLayout();

        setFragment();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = progress + ColorFillView.SMALL_PAINT_SIZE;
                updateSize(size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateSize(int size) {
        ViewGroup.LayoutParams layoutParams = vSize.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        vSize.setLayoutParams(layoutParams);
        fillView.setPaintSize(size);
    }


    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.learn_banner_view);
        }
        return bannerViewContainer;
    }

    /**
     * fragment 切换 回调
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFragmentSelectEvent(FragmentEvent event) {
        if (event == null || fillView == null) {
            return;
        }
        if (event instanceof ColorFragmentEvent) {
            // 颜色选择
            ColorFragmentEvent colorFragmentEvent = (ColorFragmentEvent) event;
            int selectIndex = colorFragmentEvent.getSelectIndex();
            if (selectIndex == 0) {
                fillView.setRandomColor(true);
            } else {
                fillView.setRandomColor(false);
                fillView.setColor(ColorUtils.color);
            }
        } else if (event instanceof ShapeFragmentEvent) {
            //形状
            ShapeFragmentEvent shapeFragmentEvent = (ShapeFragmentEvent) event;
            fillView.setDrawType(shapeFragmentEvent.getDrawStatus());
        }
    }


    List<BaseFragment> fragments = new ArrayList<>();

    private void setFragment() {
        fragments.add(BlankFragment.newInstance(BlankFragment.TAG));
        fragments.add(ShapeSelectFragment.newInstance(ShapeSelectFragment.TAG));
        //fragments.add(BgSelectFragment.newInstance(BgSelectFragment.TAG));
    }

    private void setTabLayout() {
        mBottomTabLayout.addTab(mBottomTabLayout.newTab());
        mBottomTabLayout.addTab(mBottomTabLayout.newTab());
        mBottomTabLayout.addTab(mBottomTabLayout.newTab());
        //mBottomTabLayout.addTab(mBottomTabLayout.newTab());

        int count = mBottomTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            mBottomTabLayout.getTabAt(i).setCustomView(getTabView(i));
        }

        mBottomTabLayout.addOnTabSelectedListener(this);

        setTabLine(mBottomTabLayout, 30, 30);

    }


    public void onLearnClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.close:
                checkClose();
                break;

            case R.id.btn_empty:
                showResetDialog(this,"确定要清空画板吗", new OnDialogListener() {
                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onOk() {
                        fillView.clearScreen();
                    }
                });
                break;

            case R.id.btn_save:
                Uri shareUri = fillView.saveBitmap(false);
                showSaveDialog(this, shareUri);
                break;

            case R.id.btn_undo:
                fillView.undo();
                break;

            case R.id.btn_redo:
                fillView.redo();
                break;

            case R.id.iv_arrow:
                hideGuideView();
                break;
        }
    }


    void checkClose() {
        if (fillView.canExit()){
            finish();
        }else {
            showCloseDialog(this);
        }
    }

    @Override
    public void onBackPressed() {
        checkClose();
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
            transaction.hide(from).add(R.id.fl_learn_fragment_container, to, to.getClass().getSimpleName()).commitAllowingStateLoss();
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

    private void checkEraser(int position) {
        if (position == 1) {
            fillView.setDrawType(DrawAttribute.DrawStatus.ERASER);
        } else {
            fillView.setDrawType(DrawAttribute.DrawStatus.NORMAL);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        fromPosition = tab.getPosition();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**
     * tablayou item 自定义视图
     *
     * @param position
     * @return
     */
    /**
     * tabLayout 图标
     */
    static final int[] tabIcons = {
            R.drawable.tap_ys,
            R.drawable.tap_xp,
            R.drawable.tap_xz,
//            R.drawable.tap_bg,
    };

    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_view, null);
        ImageView icon = view.findViewById(R.id.iv_tab_icon);
        icon.setImageResource(tabIcons[position]);
        return view;
    }


    boolean isOpen = true;

    public void hideGuideView() {
        if (isOpen) {
            ivArrow.setRotation(90);
            guideContainer.animate().translationX(ivGuide.getWidth()).setDuration(200).start();
            isOpen = false;
        } else {
            ivArrow.setRotation(-90);
            guideContainer.animate().translationX(0).setDuration(200).start();
            isOpen = true;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
