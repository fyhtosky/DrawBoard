package com.example.liusheng.painboard.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewConfiguration;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.BitmapUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by chenzhen on 2019/3/25.
 */

public class MagicSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    static final String TAG = "DynamicSurfaceView";


    public static final int[] RANDOM_DRAWABLE = {
            R.drawable.p_shader_20, R.drawable.p_shader_2, R.drawable.p_shader_23,
            R.drawable.p_shader_25, R.drawable.p_shader_26, R.drawable.p_shader_27,
            R.drawable.p_shader_28, R.drawable.p_shader_14,
    };

    public static Integer[] BITMAP_SHADERS = {
            R.drawable.p_shader_0, R.drawable.p_shader_1, R.drawable.p_shader_2,
            R.drawable.p_shader_3, R.drawable.p_shader_4, R.drawable.p_shader_5,
            R.drawable.p_shader_6, R.drawable.p_shader_7, R.drawable.p_shader_8,
            R.drawable.p_shader_9, R.drawable.p_shader_10, R.drawable.p_shader_11,
            R.drawable.p_shader_12, R.drawable.p_shader_13, R.drawable.p_shader_14,
            R.drawable.p_shader_15, R.drawable.p_shader_16, R.drawable.p_shader_17,
            R.drawable.p_shader_18, R.drawable.p_shader_19, R.drawable.p_shader_20,
            R.drawable.p_shader_21, R.drawable.p_shader_22, R.drawable.p_shader_23,
            R.drawable.p_shader_24, R.drawable.p_shader_25, R.drawable.p_shader_26,
            R.drawable.p_shader_27, R.drawable.p_shader_28
    };

    Context mContext;
    List<Dynamic> dynamicList;
    List<Bitmap> randomBitmaps = new ArrayList<>();
    Paint drawPaint;
    int drawWidth = 20;//画笔宽度
    int drawColor = Color.RED;//画笔宽度


    int slop = 20;


    Random random;

    Bitmap initBitmap;


    DynamicType dynamicType = DynamicType.MULTI_RANDOM;

    SurfaceHolder mHolder;


    public MagicSurfaceView(Context context) {
        this(context, null);
    }

    public MagicSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    private void init() {


        random = new Random();

        //dynamicList = new CopyOnWriteArrayList<>();//处理并发

        dynamicList = new CopyOnWriteArrayList<>();

        slop = ViewConfiguration.get(mContext).getScaledTouchSlop();

        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setStrokeWidth(drawWidth);
        drawPaint.setStyle(Paint.Style.STROKE);

        mHolder = getHolder();
        mHolder.addCallback(this);


        initBitmap = BitmapUtils.decodeBitmapFromResource(mContext, R.drawable.star, drawWidth, drawWidth);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);

        generateRandomBitmaps();
    }


    private void generateRandomBitmaps() {
        randomBitmaps.clear();
        int length = RANDOM_DRAWABLE.length;
        while (length > 0) {
            length--;
            Bitmap bitmap = BitmapUtils.decodeBitmapFromResource(mContext, RANDOM_DRAWABLE[length], drawWidth, drawWidth);
            randomBitmaps.add(drawBitmap(bitmap, randomColor()));
        }

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            onStart();
        } catch (Exception e) {
            Log.e(TAG, "surfaceCreated " + e.getMessage());
        } finally {
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
                Log.e(TAG, "surfaceCreated finally");
            }
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onStop();
    }

    public void setRandomBitmaps(List<Bitmap> bitmaps) {

        randomBitmaps.clear();
        randomBitmaps.addAll(bitmaps);
    }

    public void setBitmap(Bitmap bitmap) {
        this.initBitmap = bitmap;
    }

    public void setDynamicType(DynamicType type) {
        this.dynamicType = type;
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

    private Bitmap getRandomBitmap() {
        int range = randomBitmaps.size();
        if (range == 0) {
            return null;
        }
        return randomBitmaps.get(random.nextInt(range - 1));
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

    float startX, startY, endX, endY;

    int shaderResId = R.drawable.shader_normal4;

    public void setShaderResId(@DrawableRes int shaderId) {
        this.shaderResId = shaderId;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (dynamicType == DynamicType.SIMPLE) {
                    Bitmap simpleBitmap = BitmapUtils.decodeBitmapFromResource(mContext, shaderResId, drawWidth, drawWidth);
                    if (simpleBitmap != null) {
                        setBitmap(simpleBitmap);
                    }
                } else if (dynamicType == DynamicType.SIMPLE_COLOR_RANDOM) {
                    Bitmap bitmap = BitmapUtils.decodeBitmapFromResource(mContext, shaderResId, drawWidth, drawWidth);
                    if (bitmap != null) {
                        Bitmap simpleColorBitmap = drawBitmap(bitmap, randomColor());
                        if (simpleColorBitmap != null) {
                            setBitmap(drawBitmap(simpleColorBitmap, randomColor()));
                        }
                    }
                } else if (dynamicType == DynamicType.SIMPLE_RANDOM) {
                    Bitmap simpleRandomBitmap = getRandomBitmap();
                    if (simpleRandomBitmap != null) {
                        setBitmap(simpleRandomBitmap);
                    }
                } else if (dynamicType == DynamicType.MULTI_RANDOM) {
                    generateRandomBitmaps();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (dynamicType == DynamicType.SIMPLE) {
                    if (calculateDistance(x, y, 30)) {
                        Dynamic dynamic = new DynamicBitmap(initBitmap);
                        dynamic.config(x, y);
                        dynamicList.add(dynamic);
                    }
                } else if (dynamicType == DynamicType.MULTI_RANDOM) {
                    if (calculateDistance(x, y, 30)) {
                        Bitmap randomBitmap = getRandomBitmap();
                        Dynamic dynamic = new DynamicBitmap(randomBitmap != null ? randomBitmap : initBitmap);
                        dynamic.config(x, y);
                        dynamicList.add(dynamic);
                    }

                } else if (dynamicType == DynamicType.SIMPLE_RANDOM) {
                    if (calculateDistance(x, y, 30)) {
                        Dynamic dynamic = new DynamicBitmap(initBitmap);
                        dynamic.config(x, y);
                        dynamicList.add(dynamic);
                    }

                } else if (dynamicType == DynamicType.SIMPLE_COLOR_RANDOM) {
                    if (calculateDistance(x, y, 30)) {
                        Dynamic dynamic = new DynamicBitmap(initBitmap);
                        dynamic.config(x, y);
                        dynamicList.add(dynamic);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startX = x;
                startY = y;
                break;

        }
        return true;
    }


    DrawDynamic drawDynamic;

    void onStart() {
        if (drawDynamic == null) {
            drawDynamic = new DrawDynamic();
        }
        running = true;
        drawDynamic.start();
    }

    void onStop() {
        running = false;
    }


    boolean running = true;

    private class DrawDynamic extends Thread {
        @Override
        public void run() {
            while (running) {
                try {
                    toDraw();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private boolean calculateDistance(float x, float y, int d) {
        endX = x;
        endY = y;
        //计算手指滑动距离
        double distance = Math.sqrt(Math.pow(Math.abs(endX - startX), 2) + Math.pow(Math.abs(endY - startY), 2));
        return distance > d;
    }


    private void toDraw() {
        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            doDraw(canvas);
        } catch (Exception e) {
            Log.e(TAG, "toDraw " + e.getMessage());
        } finally {
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
                Log.e(TAG, "toDraw finally");
            }
        }
    }


    public void clear() {
        dynamicList.clear();
    }


    int LIMIT_SIZE = 400;

    private void doDraw(Canvas canvas) {

        //绘制bitmap
        int lastSize = dynamicList.size();
        if (lastSize > LIMIT_SIZE) {
            dynamicList.removeAll(dynamicList.subList(0, lastSize));
        }
        for (Dynamic dynamic : dynamicList) {
            dynamic.draw(canvas);
        }
        int size = dynamicList.size();
        if (size > 0) {
            Dynamic dynamic = dynamicList.get(size - 1);
            dynamic.draw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        onStop();
        super.onDetachedFromWindow();
    }

    public void pause() {
        onStop();
        toDraw();
    }

    public void play() {
        onPlay();
    }

    private void onPlay() {
        drawDynamic = new DrawDynamic();
        running = true;
        drawDynamic.start();
    }
}
