package com.example.liusheng.painboard.View;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.BitmapUtils;
import com.example.liusheng.painboard.Tools.StorageInSDCard;
import com.example.liusheng.painboard.particlesystem.ParticleSystem;
import com.example.liusheng.painboard.particlesystem.modifiers.AlphaModifier;
import com.example.liusheng.painboard.particlesystem.modifiers.ScaleModifier;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 粒子画画
 * Created by chenzhen on 2019/3/6.
 */

public class MagicView extends View {


    static final String TAG = "MagicView";


    Activity activity;

    int magicType = MagicType.FIREWORK;

    ValueAnimator mAnimator;


    List<ParticleSystem> particleSystemList;


    int magicResId = R.drawable.firework_2;

    Bitmap drawBitmap;


    public MagicView(Context context) {
        this(context, null);
    }

    public MagicView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity) context;

        drawBitmap = BitmapUtils.decodeBitmapFromResource(context, magicResId, 30, 30);
        particleSystemList = new CopyOnWriteArrayList<>();
    }


    private void startAnimator(Interpolator interpolator, long animnationTime) {
        mAnimator = ValueAnimator.ofInt(0, 200, 400, 600, 800, 1000);
        mAnimator.setDuration(animnationTime);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //clear();
            }
        });
        mAnimator.setInterpolator(interpolator);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                //Log.e(TAG, "magicType " + magicType);

//                if (magicType == MagicType.AUTO) {
//
//                    auto(x, y);
//
//                } else
                if (magicType == MagicType.FIREWORK) {

                    Bitmap sourceBitmap = BitmapUtils.decodeBitmapFromResource(getContext(), magicResId, 30, 30);
                    Bitmap bitmap = drawBitmap(sourceBitmap, randomColor());
                    if (bitmap != null) {
                        drawBitmap = bitmap;
                    }
                    ParticleSystem ps = new ParticleSystem(activity, 500, drawBitmap, 1000, R.id.fl_particle_container);
                    ps.setSpeedRange(0.1f, 0.25f).emit(x, y, 30);

                    particleSystemList.add(ps);

                } else if (magicType == MagicType.DUST) {

                    Bitmap sourceBitmap = BitmapUtils.decodeBitmapFromResource(getContext(), magicResId, 30, 30);
                    Bitmap bitmap = drawBitmap(sourceBitmap, randomColor());
                    if (bitmap != null) {
                        drawBitmap = bitmap;
                    }
                    ParticleSystem ps = new ParticleSystem(activity, 500, drawBitmap, 20000, R.id.fl_particle_container);
                    ps.setSpeedByComponentsRange(-0.025f, 0.025f, -0.06f, -0.08f)
                            .setAcceleration(0.00001f, 30)
                            .setInitialRotationRange(0, 360)
                            .addModifier(new AlphaModifier(255, 0, 1000, 20000))
                            .addModifier(new ScaleModifier(0.5f, 2f, 0, 10000))
                            .emit(x, y, 30);

                    particleSystemList.add(ps);


                } else if (magicType == MagicType.START) {

                    Bitmap sourceBitmap = BitmapUtils.decodeBitmapFromResource(getContext(), magicResId, 30, 30);
                    Bitmap bitmap = drawBitmap(sourceBitmap, randomColor());
                    if (bitmap != null) {
                        drawBitmap = bitmap;
                    }
                    ParticleSystem ps = new ParticleSystem(activity, 100, drawBitmap, 5000, R.id.fl_particle_container);
                    ps.setSpeedByComponentsRange(-0.1f, 0.1f, -0.1f, 0.02f)
                            .setAcceleration(0.000003f, 90)
                            .setInitialRotationRange(0, 360)
                            .setRotationSpeed(120)
                            .setFadeOut(2000)
                            .addModifier(new ScaleModifier(0f, 1.5f, 0, 1500))
                            .emit(x, y, 100);

                    particleSystemList.add(ps);

                } else if (magicType == MagicType.FLOWER) {

                    Bitmap sourceBitmap = BitmapUtils.decodeBitmapFromResource(getContext(), magicResId, 30, 30);
                    Bitmap bitmap = drawBitmap(sourceBitmap, randomColor());
                    if (bitmap != null) {
                        drawBitmap = bitmap;
                    }
                    ParticleSystem ps = new ParticleSystem(activity, 80, drawBitmap, 10000, R.id.fl_particle_container);
                    ps.setSpeedModuleAndAngleRange(0f, 0.1f, 180, 180)
                            .setRotationSpeed(144)
                            .setAcceleration(0.000017f, 90)
                            .emit(0, 0, 8);

                    particleSystemList.add(ps);

                } else {
                    auto(x, y);
                }

                break;

            case MotionEvent.ACTION_MOVE:
                int indexMove = particleSystemList.size();
                if (indexMove > 0) {
                    particleSystemList.get(indexMove - 1).updateEmitPoint(x, y);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//                int indexUp = particleSystemList.size();
//                if (indexUp > 0) {
//                    particleSystemList.get(indexUp - 1).stopEmitting();
//                }
                break;
        }

        return true;
    }

    public void clear() {
        if (particleSystemList != null) {
            for (ParticleSystem particleSystem : particleSystemList) {
                particleSystem.stopEmitting();
                particleSystem.cancel();
            }
            particleSystemList.clear();
        }
    }

    public boolean canExit(){
        return particleSystemList.isEmpty();
    }


    /**
     * 默认
     *
     * @param x
     * @param y
     */
    private void auto(int x, int y) {
        Bitmap sourceBitmap = BitmapUtils.decodeBitmapFromResource(getContext(), magicResId, 30, 30);
        Bitmap bitmap = drawBitmap(sourceBitmap, randomColor());
        if (bitmap != null) {
            drawBitmap = bitmap;
        }
        ParticleSystem ps = new ParticleSystem(activity, 300, drawBitmap, 800, R.id.fl_particle_container);
        ps.setScaleRange(0.7f, 1.3f);
        ps.setSpeedRange(0.05f, 0.1f);
        ps.setRotationSpeedRange(90, 180);
        ps.setFadeOut(2000, new AccelerateInterpolator());
        ps.emit(x, y, 40);
        particleSystemList.add(ps);
    }


    public void setMagicType(int type) {
        this.magicType = type;
    }

    public void setMagicResId(@DrawableRes int drawableResId) {
        this.magicResId = drawableResId;
        Bitmap sourceBitmap = BitmapUtils.decodeBitmapFromResource(getContext(), drawableResId, 30, 30);
        Bitmap bitmap = drawBitmap(sourceBitmap, randomColor());
        if (bitmap != null) {
            drawBitmap = bitmap;
        }
    }


    /**
     * 返回一个随机颜色
     *
     * @return
     */
    private int randomColor() {
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return color;
    }

    /**
     * 图片 上色
     *
     * @param sourceBitmap
     * @param color
     * @return
     */
    private Bitmap drawBitmap(Bitmap sourceBitmap, int color) {
        Bitmap bitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        Paint paintUnder = new Paint();
        paintUnder.setColor(color);
        canvas.drawPaint(paintUnder);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(sourceBitmap, 0, 0, paint);
        return bitmap;
    }


    public Uri saveBitmap(boolean share) {
        try {
            Bitmap bitmap = drawBitmap();
            if (bitmap != null) {
                return StorageInSDCard.saveBitmapInExternalStorage(bitmap, getContext(), share);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }


    private Bitmap drawBitmap() {
        int w = getWidth();
        int h = getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(Color.BLACK);
        drawPs(canvas);
        return bmp;
    }

    private void drawPs(Canvas canvas) {
        for (ParticleSystem particleSystem : particleSystemList) {
            View view = particleSystem.getDrawingView();
            if (view != null) {
                view.draw(canvas);
            }
        }
    }


}
