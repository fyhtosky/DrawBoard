package com.example.liusheng.painboard.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class PaletteView extends FrameLayout {
    private Paint mPaint;   // 当前画笔
    private Paint whitePaint;   // 当前画笔
    private Path mPath;     // 当前线条路径
    private float mLastX;   // 上一个x点
    private float mLastY;   // 上一个y点
    private Bitmap mBufferBitmap;   // 缓冲位图
    private Canvas mBufferCanvas;   // 缓冲画布

    public Bitmap buffer2Bitmap;

    private DrawView tempView;    // 轮廓显示view

    private static final int MAX_CACHE_STEP = 20;   // 最大撤销步数

    private List<DrawingInfo> mDrawingList;     // 当前存在的线条路径集合
    private List<DrawingInfo> mRemovedList;     // 已经撤销的线条路径集合

    private Xfermode mClearMode;    // 图像混合模式(项目里使用clear模式实现橡皮擦)

    // 画笔参数
    private float mDrawSize = 20;
    private float mEraserSize = 40;
    private int mColor = Color.BLACK;

    private boolean mCanEraser;     // 是否能用橡皮

    private Callback mCallback;     // 回调

    private Mode mMode = Mode.DRAW;     // 绘制模式

    // 抽象类 画笔信息
    public abstract static class DrawingInfo {
        Paint paint;
        Paint whitePaint;
        Mode mode;

        abstract void draw(Canvas canvas);
    }

    public static class PathDrawingInfo extends DrawingInfo {
        Path path;

        @Override
        void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
            if (mode == Mode.DRAW){
                canvas.drawPath(path, whitePaint);
            }
        }
    }


    public interface Callback {
        void onUndoRedoStatusChanged();
    }

    public enum Mode {
        DRAW,
        ERASER,
        RECTANGULAR,
        CIRCULAR,
        LINE,
        DASHLINE
    }


    public PaletteView(Context context) {
        this(context, null);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    // 初始化画笔
    private void init() {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG); // 画笔抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mDrawSize);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG); // 画笔抗锯齿
        whitePaint.setStyle(Paint.Style.STROKE);
        whitePaint.setFilterBitmap(true);
        whitePaint.setStrokeJoin(Paint.Join.ROUND);
        whitePaint.setStrokeCap(Paint.Cap.ROUND);
        whitePaint.setStrokeWidth(5);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);
        whitePaint.setDither(true);
        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    // 初始化缓存画布
    private void initBuffer() {
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
        buffer2Bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    }

    // 获取画笔/橡皮模式
    public Mode getMode() {
        return mMode;
    }

    // 设置画笔/橡皮模式
    public void setMode(Mode mode) {
        if (mMode != mode) {
            mMode = mode;
            if (mMode == Mode.ERASER) {
                mPaint.setXfermode(mClearMode);
                mPaint.setStrokeWidth(mEraserSize);
            } else {
                mPaint.setXfermode(null);
                mPaint.setStrokeWidth(mDrawSize);
            }
        }
    }

    public void setEraserSize(float size) {
        mEraserSize = size;
    }

    public void setPenRawSize(float size) {
        mDrawSize = size;
        mPaint.setStrokeWidth(mDrawSize);
    }

    public void setPenColor(int color) {
        mColor = color;
        mPaint.setColor(color);
    }

    public void setPenAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    // 重绘
    private void reDraw() {
        if (mDrawingList != null) {
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (DrawingInfo drawingInfo : mDrawingList) {
                drawingInfo.draw(mBufferCanvas);
            }
            invalidate();
        }
    }

    public boolean canRedo() {
        return mRemovedList != null && mRemovedList.size() > 0;
    }

    public boolean canUndo() {
        return mDrawingList != null && mDrawingList.size() > 0;
    }

    //恢复
    public void redo() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0) {
            DrawingInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }

    }

    // 撤销
    public void undo() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
        if (size > 0) {
            DrawingInfo info = mDrawingList.remove(size - 1);
            if (mRemovedList == null) {
                mRemovedList = new ArrayList<>(MAX_CACHE_STEP);
            }
            if (size == 1) {
                mCanEraser = false;
            }
            mRemovedList.add(info);
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }

    }


    public void clear() {
        if (mBufferBitmap != null) {
            if (mDrawingList != null) {
                mDrawingList.clear();
            }
            if (mRemovedList != null) {
                mRemovedList.clear();
            }
            mCanEraser = false;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            invalidate();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public Bitmap buildBitmap() {
        Bitmap bm = getDrawingCache();
        Bitmap result = Bitmap.createBitmap(bm);
        destroyDrawingCache();
        return result;
    }

    private void saveDrawingPath() {
        if (mDrawingList == null) {
            mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
        } else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        info.whitePaint = whitePaint;
        info.mode = mMode;
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.onUndoRedoStatusChanged();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (buffer2Bitmap != null) {
            canvas.drawBitmap(buffer2Bitmap, 0, 0, null);

        }
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                if (mPath == null) {
                    mPath = new Path();
                }
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:

                if (tempView != null) {
                    this.removeView(tempView);
                    tempView = null;
                }
                if (mBufferBitmap == null) {
                    initBuffer();
                }
                if (mMode == Mode.ERASER && !mCanEraser) {
                    break;
                }

                if (mMode == Mode.RECTANGULAR) {
                    actionMoveRect(x, y);
                } else if (mMode == Mode.CIRCULAR) {
                    actionMoveCircle(x, y);
                } else if (mMode == Mode.LINE) {
                    actionMoveLine(x, y);
                } else if (mMode == Mode.DASHLINE) {
                    actionMoveDashLine(x, y);
                } else {
                    //荧光画笔

                    mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                    mBufferCanvas.drawPath(mPath, mPaint);
//                    mBufferCanvas.drawPath(mPath, fluorPaint);
                    invalidate();
                    mLastX = x;
                    mLastY = y;
                }

                break;
            case MotionEvent.ACTION_UP:

                if (tempView != null) {
                    this.removeView(tempView);
                    tempView = null;
                }

                if (mMode == Mode.RECTANGULAR) {
                    actionUpRect(x, y);
                }
                if (mMode == Mode.CIRCULAR) {
                    actionUpCircle(x, y);
                }
                if (mMode == Mode.LINE) {
                    actionUpLine(x, y);
                }
                if (mMode == Mode.DASHLINE) {
                    actionUpDashLine(x, y);
                }

                if (mBufferBitmap == null) {
                    initBuffer();
                }
                if (mMode != Mode.DRAW) {
                    mBufferCanvas.drawPath(mPath, mPaint);

                }
                invalidate();


                if (mMode != Mode.ERASER || mCanEraser) {
                    saveDrawingPath();
                }
                mPath.reset();
                mPaint.setPathEffect(null);
                break;
        }
        return true;
    }


    private void actionMoveRect(float x, float y) {
        tempView = new DrawView(getContext(), (int) mDrawSize, mColor, DrawView.DRAWMODE.RECT);
        addTempView(x, y);
    }

    private void actionMoveCircle(float x, float y) {
        tempView = new DrawView(getContext(), (int) mDrawSize, mColor, DrawView.DRAWMODE.CIRCLE);
        addTempView(x, y);
    }

    private void actionMoveLine(float x, float y) {
        if ((x >= mLastX && y >= mLastY) || (x <= mLastX && y <= mLastY)) {
            tempView = new DrawView(getContext(), (int) mDrawSize, mColor, DrawView.DRAWMODE.LINE1);
        } else {
            tempView = new DrawView(getContext(), (int) mDrawSize, mColor, DrawView.DRAWMODE.LINE2);
        }
        addTempView(x, y);
    }

    private void actionMoveDashLine(float x, float y) {
        if ((x >= mLastX && y >= mLastY) || (x <= mLastX && y <= mLastY)) {
            tempView = new DrawView(getContext(), (int) mDrawSize, mColor, DrawView.DRAWMODE.DASHLINE1);
        } else {
            tempView = new DrawView(getContext(), (int) mDrawSize, mColor, DrawView.DRAWMODE.DASHLINE2);
        }
        addTempView(x, y);
    }


    private void addTempView(float x, float y) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                Math.abs((int) (x - mLastX)) + (int) mDrawSize,
                Math.abs((int) (y - mLastY)) + (int) mDrawSize
        );
        float leftMg;
        float topMg;
        if ((x - mLastX >= 0)) {
            leftMg = mLastX;
        } else {
            leftMg = x;
        }
        if ((y - mLastY) >= 0) {
            topMg = mLastY;
        } else {
            topMg = y;
        }
        layoutParams.leftMargin = (int) leftMg - (int) mDrawSize / 2;
        layoutParams.topMargin = (int) topMg - (int) mDrawSize / 2;
        this.addView(tempView, layoutParams);
    }


    private void actionUpRect(float x, float y) {
        mPath.lineTo(mLastX, y);
        mPath.lineTo(x, y);
        mPath.lineTo(x, mLastY);
        mPath.lineTo(mLastX, mLastY);
    }

    private void actionUpCircle(float x, float y) {
        RectF rectF = new RectF(mLastX, mLastY, x, y);
        mPath.addOval(rectF, Path.Direction.CW);
    }

    private void actionUpLine(float x, float y) {
        mPath.lineTo(x, y);
    }

    private void actionUpDashLine(float x, float y) {
        PathEffect effect = new DashPathEffect(new float[]{20, 20}, 0);
        mPaint.setPathEffect(effect);
        mPath.lineTo(x, y);
    }
}
