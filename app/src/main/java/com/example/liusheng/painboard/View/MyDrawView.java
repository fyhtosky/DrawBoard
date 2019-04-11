package com.example.liusheng.painboard.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.example.liusheng.painboard.Activity.DrawingActivity;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.BitmapUtils;
import com.example.liusheng.painboard.Tools.StorageInSDCard;
import com.example.liusheng.painboard.bean.PaintData;
import com.example.liusheng.painboard.draw.ColorUtils;
import com.example.liusheng.painboard.draw.DrawAttribute;
import com.example.liusheng.painboard.event.OnUndoEnabledListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyDrawView extends View {

    static final String TAG = "MyDrawView -- ";


    private Bitmap backgroundBitmap = null;
    private Bitmap paintBitmap = null;
    private Canvas paintCanvas = null;
    public DrawAttribute.DrawStatus drawStatus;
    private Context context;
    private Path strokePath, mPath, mEraserPath;
    public Paint fluorPaint, strokePaint, normalPaint, eraserPaint, bitmapPaint, mPixelPaint, mPaint;
    private float mLastX;
    private float mLastY;
    RectF rect = new RectF();
    float downX, downY, x, y;

    int width = 600;
    int height = 800;

    List<PaintData> mUndoPaintDatas;
    List<PaintData> mRedoPaintDatas;

    int[] pixels;

    PaintFlagsDrawFilter drawFilter;

    Random random;

    PathEffect[] effects;


    private OnUndoEnabledListener onUndoEnabledListener;

    public void setOnUndoEnabledListener(OnUndoEnabledListener listener) {
        this.onUndoEnabledListener = listener;
    }


    public MyDrawView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        this.drawStatus = DrawAttribute.DrawStatus.NORMAL;
        initDraw();
    }

    private void initDraw() {

        //setLayerType(LAYER_TYPE_SOFTWARE, null);

        random = new Random();
        mUndoPaintDatas = new ArrayList<>();
        mRedoPaintDatas = new ArrayList<>();

        strokePath = new Path();
        mPath = new Path();
        mEraserPath = new Path();

        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);

        //荧光笔
        fluorPaint = new Paint();
        fluorPaint.setAntiAlias(true);
        fluorPaint.setDither(true);
        fluorPaint.setStyle(Paint.Style.STROKE);
        fluorPaint.setColor(Color.WHITE);
        fluorPaint.setStrokeWidth(6);
        fluorPaint.setStrokeCap(Paint.Cap.ROUND);
        fluorPaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setDither(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeWidth(18);

        //strokePaint.setMaskFilter(new BlurMaskFilter(5f, BlurMaskFilter.Blur.SOLID));


        //图片画笔
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setDither(true);
        bitmapPaint.setStyle(Paint.Style.STROKE);
        bitmapPaint.setColor(Color.WHITE);
        bitmapPaint.setStrokeWidth(6);
        bitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        bitmapPaint.setStrokeJoin(Paint.Join.ROUND);

        //普通画笔
        normalPaint = new Paint();
        normalPaint.setAntiAlias(true);
        normalPaint.setDither(true);
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setStrokeWidth(8);
        normalPaint.setStrokeCap(Paint.Cap.ROUND);
        normalPaint.setStrokeJoin(Paint.Join.ROUND);
        normalPaint.setColor(Color.RED);


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(Color.RED);


        //像素笔
        mPixelPaint = new Paint();
        mPixelPaint.setAntiAlias(true);
        mPixelPaint.setDither(true);
        mPixelPaint.setStyle(Paint.Style.FILL);
        mPixelPaint.setStrokeWidth(8);
        mPixelPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPixelPaint.setStrokeJoin(Paint.Join.MITER);


        effects = new PathEffect[1];

        //马克笔
        Path p4 = new Path();
        p4.addRect(0, 0, 10, 30, Path.Direction.CCW);
        effects[0] = new PathDashPathEffect(p4, 2, 0, PathDashPathEffect.Style.TRANSLATE);


        //橡皮擦
        eraserPaint = new Paint();
        eraserPaint.setAlpha(0);
        //这个属性是设置paint为橡皮擦重中之重
        //这是重点
        //下面这句代码是橡皮擦设置的重点
        eraserPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        //上面这句代码是橡皮擦设置的重点（重要的事是不是一定要说三遍）
        eraserPaint.setAntiAlias(true);
        eraserPaint.setDither(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setStrokeWidth(12);


        post(new Runnable() {
            @Override
            public void run() {
                paintBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_4444);
                backgroundBitmap = paintBitmap.copy(Bitmap.Config.ARGB_4444, true);
                paintCanvas = new Canvas(paintBitmap);
                paintCanvas.drawARGB(0, 255, 255, 255);
                paintCanvas.setDrawFilter(drawFilter);
                width = paintBitmap.getWidth();
                height = paintBitmap.getHeight();
                pixels = new int[width * height];

            }
        });


    }


    boolean needBackgroundDraw = true;  //更换背景

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(drawFilter);
        if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }
        if (paintBitmap != null && !paintBitmap.isRecycled()) {
            canvas.drawBitmap(paintBitmap, 0, 0, null);
        }
    }


    private void drawRect(float x, float y) {
        int zoneWidth = random.nextInt(5);
        int zoneHeight = random.nextInt(20);
//        RectF rectF = new RectF(x, y, x + zoneWidth, y + zoneHeight);

        paintCanvas.drawLine(x, y, x + zoneWidth, y + zoneHeight, mPaint);
        invalidate();
    }


    RectF pixelRectF = new RectF();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (paintCanvas == null) {
            return true;
        }
        boolean result = super.onTouchEvent(event);
        x = (int) event.getX();
        y = (int) event.getY();
        //Log.e(TAG, "onTouchEvent");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            result = true;
            downX = x;
            downY = y;
            rect.set(downX, downY, downX, downY);

            int randomColor = ColorUtils.randomColor();

            strokePaint.setColor(randomColor);
            // 普通画笔
            normalPaint.setColor(randomColor);

            //笔
            mPaint.setColor(randomColor);

            if (drawStatus == DrawAttribute.DrawStatus.MARK) {
                // 马克笔
                mPaint.setAlpha(255);
                mPaint.setPathEffect(null);
                mPaint.setShader(null);
                mPaint.setPathEffect(effects[0]);

            } else if (drawStatus == DrawAttribute.DrawStatus.PIXEL) {
                // 像素笔
                mPixelPaint.setStrokeMiter(1f);
                mPixelPaint.setColor(randomColor);

            } else if (drawStatus == DrawAttribute.DrawStatus.TAN) {
                // 炭笔
                mPaint.setPathEffect(null);
                int size = (int) mPaint.getStrokeWidth();
                Bitmap bitmap = BitmapUtils.decodeBitmapFromResource(getContext(), R.drawable.pen, size, size);
                Bitmap patternBMP = drawBitmap(bitmap, randomColor);
                BitmapShader shader = new BitmapShader(patternBMP, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                mPaint.setAlpha(200);
                mPaint.setColor(0xFFFFFFFF);
                mPaint.setShader(shader);
            }

            mLastX = x;
            mLastY = y;
            if (strokePath == null) {
                strokePath = new Path();
            }
            mPath.reset();
            mPath.moveTo(x, y);

            strokePath.reset();
            strokePath.moveTo(x, y);

            mEraserPath.reset();
            mEraserPath.moveTo(x, y);
        }
        // 移动
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.e(TAG, "onTouchEvent ACTION_MOVE");
            rect.set(downX, downY, x, y);
            if (drawStatus == DrawAttribute.DrawStatus.CIRCLE || drawStatus == DrawAttribute.DrawStatus.JUXING || drawStatus == DrawAttribute.DrawStatus.XIAN) {
                DrawingActivity.trajectoryView.setVisibility(View.VISIBLE);
                DrawingActivity.trajectoryView.downX = downX;
                DrawingActivity.trajectoryView.downY = downY;
                DrawingActivity.trajectoryView.x = x;
                DrawingActivity.trajectoryView.y = y;
                DrawingActivity.trajectoryView.mPaint = normalPaint;
                DrawingActivity.trajectoryView.mode = drawStatus;
                DrawingActivity.trajectoryView.invalidate();
            } else if (drawStatus == DrawAttribute.DrawStatus.FLUORE) {
                //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                /**荧光画笔*/

//                float offest = strokePaint.getStrokeWidth() / 3;
//                strokePaint.setShadowLayer(360,offest,offest,strokePaint.getColor());

                strokePath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                paintCanvas.drawPath(strokePath, strokePaint);
                paintCanvas.drawPath(strokePath, fluorPaint);
                invalidate();
            } else if (drawStatus == DrawAttribute.DrawStatus.NORMAL) {
                /**普通画笔*/
                strokePath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                paintCanvas.drawPath(strokePath, normalPaint);
                invalidate();
            } else if (drawStatus == DrawAttribute.DrawStatus.BITMAP) {
                /**图片画笔*/
                strokePath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                paintCanvas.drawPath(strokePath, bitmapPaint);
                invalidate();
            } else if (drawStatus == DrawAttribute.DrawStatus.MARK) {
                /**马克笔*/
                strokePath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                paintCanvas.drawPath(strokePath, mPaint);
                invalidate();
            } else if (drawStatus == DrawAttribute.DrawStatus.PIXEL) {
                /**像素画笔*/
                //strokePath.lineTo(x, y);
                pixelRectF.setEmpty();
                float size = mPixelPaint.getStrokeWidth();
                float right = x + size;
                float bottom = y + size;
                pixelRectF.set(mLastX, mLastY, right, bottom);
                paintCanvas.drawRect(pixelRectF, mPixelPaint);
                invalidate();
                mLastX = right;
                mLastY = bottom;
            } else if (drawStatus == DrawAttribute.DrawStatus.TAN) {
                /**炭笔*/
                strokePath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                paintCanvas.drawPath(strokePath, mPaint);
                invalidate();
            } else if (drawStatus == DrawAttribute.DrawStatus.ERASER) {
                /**橡皮擦*/
                mEraserPath.lineTo(x, y);
                paintCanvas.drawPath(mEraserPath, eraserPaint);
                invalidate();
            } else if (drawStatus == DrawAttribute.DrawStatus.JUXING) {
                /** 绘制 矩形*/
                paintCanvas.drawRect(rect, normalPaint);
                invalidate();
            } else if (drawStatus == DrawAttribute.DrawStatus.CIRCLE) {
                /** 绘制 圆形*/
                paintCanvas.drawOval(new RectF(rect.left, rect.top, rect.right, rect.bottom), normalPaint);
                invalidate();
            } else if (drawStatus == DrawAttribute.DrawStatus.XIAN) {
                /** 绘制 线条*/
                paintCanvas.drawLine(downX, downY, x, y, normalPaint);
                invalidate();
            }
            mLastX = x;
            mLastY = y;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (drawStatus == DrawAttribute.DrawStatus.CIRCLE || drawStatus == DrawAttribute.DrawStatus.JUXING
                    || drawStatus == DrawAttribute.DrawStatus.XIAN) {
                DrawingActivity.trajectoryView.setVisibility(View.GONE);
            }
            if (drawStatus == DrawAttribute.DrawStatus.NORMAL) {
                // 普通画笔
                // 添加记录
                mUndoPaintDatas.add(new PaintData(new Path(strokePath), new Paint(normalPaint)));

            } else if (drawStatus == DrawAttribute.DrawStatus.FLUORE) {
                // 银光画笔
                mUndoPaintDatas.add(new PaintData(new Path(strokePath), new Paint(strokePaint), new Paint(fluorPaint)));

            } else if (drawStatus == DrawAttribute.DrawStatus.MARK) {
                // 马克画笔
                mUndoPaintDatas.add(new PaintData(new Path(strokePath), new Paint(mPaint)));

            } else if (drawStatus == DrawAttribute.DrawStatus.PIXEL) {
                // 像素画笔
                mUndoPaintDatas.add(new PaintData(new Path(strokePath), new Paint(mPixelPaint)));

            } else if (drawStatus == DrawAttribute.DrawStatus.TAN) {
                // 炭笔
                mUndoPaintDatas.add(new PaintData(new Path(strokePath), new Paint(mPaint)));

            } else if (drawStatus == DrawAttribute.DrawStatus.BITMAP) {
                /**图片画笔*/
                mUndoPaintDatas.add(new PaintData(new Path(strokePath), new Paint(bitmapPaint)));

            } else if (drawStatus == DrawAttribute.DrawStatus.JUXING) {

                // 矩形
                paintCanvas.drawRect(rect, normalPaint);
                // 添加记录
                Path rectPath = new Path();
                rectPath.addRect(rect, Path.Direction.CCW);
                mUndoPaintDatas.add(new PaintData(rectPath, new Paint(normalPaint)));

            } else if (drawStatus == DrawAttribute.DrawStatus.CIRCLE) {

                // 圆形
                paintCanvas.drawOval(rect, normalPaint);

                // 添加记录
                Path ovalPath = new Path();
                ovalPath.addOval(rect, Path.Direction.CCW);
                mUndoPaintDatas.add(new PaintData(ovalPath, new Paint(normalPaint)));

            } else if (drawStatus == DrawAttribute.DrawStatus.XIAN) {
                // 直线
                paintCanvas.drawLine(downX, downY, x, y, normalPaint);

                Path linePath = new Path();
                linePath.moveTo(downX, downY);
                linePath.lineTo(x, y);
                mUndoPaintDatas.add(new PaintData(linePath, new Paint(normalPaint)));
            } else if (drawStatus == DrawAttribute.DrawStatus.ERASER) {
                //橡皮擦
                mUndoPaintDatas.add(new PaintData(new Path(mEraserPath), new Paint(eraserPaint)));
            }

            if (onUndoEnabledListener != null) {
                onUndoEnabledListener.onUndoEnabled(true);
            }

            mEraserPath.reset();
            mPath.reset();
            strokePath.reset();
        }

        return result;
    }

    public void setPaintBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        try {
            bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
            paintBitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            invalidate();
        } catch (Exception e) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    public void setBackgroundBitmap(Bitmap bitmap, boolean isFromSystem) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
            backgroundBitmap.recycle();
            backgroundBitmap = null;
        }
        backgroundBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        needBackgroundDraw = true;
        invalidate();
    }

    public void setBrushBitmap(DrawAttribute.DrawStatus drawStatus) {
        this.drawStatus = drawStatus;
    }


    public void setPaintSize(float paintSize) {
        fluorPaint.setStrokeWidth(paintSize * 0.5f);
        strokePaint.setStrokeWidth(paintSize);
        normalPaint.setStrokeWidth(paintSize);
        eraserPaint.setStrokeWidth(paintSize);
        bitmapPaint.setStrokeWidth(paintSize);
        mPaint.setStrokeWidth(paintSize);

        mPixelPaint.setStrokeWidth(paintSize);

    }

    /**
     * 设置笔刷
     *
     * @param resId
     */
    public void setPattern(@DrawableRes int resId) {
        //set pattern
        setBrushBitmap(DrawAttribute.DrawStatus.BITMAP);
        invalidate();
        int patternID = getResources().getIdentifier("pattern2", "drawable", getContext().getPackageName());
        //Bitmap patternBMP = BitmapFactory.decodeResource(getResources(), patternID);

        Bitmap patternBMP = BitmapFactory.decodeResource(getResources(), resId);
        BitmapShader patternBMPshader = new BitmapShader(patternBMP, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        bitmapPaint.setColor(0xFFFFFFFF);
        bitmapPaint.setShader(patternBMPshader);
    }

    public void cleanPaintBitmap() {
        paintCanvas.drawColor(0xffffffff, Mode.DST_OUT);
        recycleList();
        invalidate();
        if (onUndoEnabledListener != null) {
            onUndoEnabledListener.onAllDisabled(); //都不可用
        }
    }


    /***
     * 撤销
     * @return
     */
    public void undo() {
        reDraw(mUndoPaintDatas);
        if (onUndoEnabledListener != null) {
            onUndoEnabledListener.onUndoEnabled(mUndoPaintDatas.isEmpty() ? false : true);
            onUndoEnabledListener.onRedoEnabled(mRedoPaintDatas.isEmpty() ? false : true);
        }
    }


    /***
     * 反撤销
     * @return
     */
    public void redo() {
        reDraw(mRedoPaintDatas);
        if (onUndoEnabledListener != null) {
            onUndoEnabledListener.onUndoEnabled(mUndoPaintDatas.isEmpty() ? false : true);
            onUndoEnabledListener.onRedoEnabled(mRedoPaintDatas.isEmpty() ? false : true);
        }
    }

    /**
     * 重绘
     *
     * @param dataList
     * @return
     */
    private void reDraw(List<PaintData> dataList) {
        int size = dataList.size();
        //Log.e(TAG, "reDraw size" + size);
        if (size > 0) {
            PaintData paint = dataList.remove(size - 1);
            if (dataList == mUndoPaintDatas) {
                mRedoPaintDatas.add(paint);
            } else {
                mUndoPaintDatas.add(paint);
            }
            //Log.e(TAG, "reDraw " + paint.toString());
            //清空缓存画板
            paintCanvas.drawColor(0xffffff, Mode.CLEAR);
            for (PaintData paintData : mUndoPaintDatas) {
                //重新绘制每个path
                paintData.draw(paintCanvas);
            }
            invalidate();
        }
    }


    public Uri saveBitmap(boolean share) {
        try {
            Bitmap bitmap = createViewBitmap(this);
            if (bitmap != null) {
                return StorageInSDCard.saveBitmapInExternalStorage(bitmap, context, share);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 将View转为Bitmap
     *
     * @param view
     * @return
     */
    Bitmap createViewBitmap(View view) {
        if (view == null) {
            return null;
        }
        setDrawingCacheEnabled(false);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap tmp = view.getDrawingCache();
        return tmp;
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

    public void freeBitmaps() {
        paintCanvas = null;
        if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
            backgroundBitmap.recycle();
            backgroundBitmap = null;
        }
        if (paintBitmap != null && !paintBitmap.isRecycled()) {
            paintBitmap.recycle();
            paintBitmap = null;
        }
    }

    public boolean canExit() {
        return mUndoPaintDatas.isEmpty();
    }

    void release() {
        freeBitmaps();
        recycleList();
    }

    void recycleList() {
        mUndoPaintDatas.clear();
        mRedoPaintDatas.clear();
    }

    @Override
    protected void onDetachedFromWindow() {
        release();
        super.onDetachedFromWindow();
    }
}
