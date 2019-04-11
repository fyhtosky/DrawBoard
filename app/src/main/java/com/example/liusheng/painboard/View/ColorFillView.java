package com.example.liusheng.painboard.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.example.liusheng.painboard.Tools.StorageInSDCard;
import com.example.liusheng.painboard.bean.PaintData;
import com.example.liusheng.painboard.draw.DrawAttribute;
import com.example.liusheng.painboard.event.OnUndoEnabledListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;


/**
 * 学画画
 * Created by chenzhen on 2019/2/14.
 */

public class ColorFillView extends View {

    static final String TAG = "ColorFillView ";

    public static final int SMALL_PAINT_SIZE = 5;

    static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    int DrawingColor = DEFAULT_STROKE_COLOR; //绘制颜色


    float slop = 20;
    boolean hasFill = false;

    Context mContext;
    GestureDetector mGestureDetector;
    Stack<Point> mStacks = new Stack<>();
    Path mDrawPath;

    Canvas mBufferCanvas;
    Bitmap mBufferBitmap;
    Paint mPaint, mEraserPaint;

    List<PaintData> mUndoPaintDatas;
    List<PaintData> mRedoPaintDatas;
    RectF rectF;

    boolean randomColor = true;

    private OnUndoEnabledListener onUndoEnabledListener;


    public void setOnUndoEnabledListener(OnUndoEnabledListener listener) {
        this.onUndoEnabledListener = listener;
    }

    public ColorFillView(Context context) {
        this(context, null);
    }

    public ColorFillView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorFillView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }
    void init() {


        mUndoPaintDatas = new ArrayList<>();
        mRedoPaintDatas = new ArrayList<>();

        rectF = new RectF();

        mDrawPath = new Path();

        slop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(mContext, mOnGestureListener);
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeWidth(SMALL_PAINT_SIZE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(DrawingColor);

        mEraserPaint = new Paint();
        mEraserPaint.setDither(true);
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setFilterBitmap(true);
        mEraserPaint.setStrokeWidth(SMALL_PAINT_SIZE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.ROUND);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setAlpha(0x000000);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

//
//        PathEffect effect = new CornerPathEffect(10f);
//        mPaint.setPathEffect(effect);
//        mEraserPaint.setPathEffect(effect);

        setRandomColor(true);//默认随机色

    }

    GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
//            if (hasFill) {
//                Log.e(TAG, "onSingleTapUp ");
//                final int x = (int) event.getX();
//                final int y = (int) event.getY();
//                fillColorToSameArea(x, y);
//            }
            return true;
        }
    };

    float startX, startY;
    float endX, endY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                result = true;
                if (randomColor) {
                    setColor(randomColor());
                }
                startX = event.getX();
                startY = event.getY();
                if (drawType == DrawAttribute.DrawStatus.NORMAL || drawType == DrawAttribute.DrawStatus.ERASER) {
                    mDrawPath.moveTo(startX, startY);
                } else {
                    mDrawPath.reset();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                result = true;
                endX = event.getX();
                endY = event.getY();
                //计算手指滑动距离
                handleMove();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleUp();
                mDrawPath.reset();
                break;
        }
        return result;
    }

    /**
     * 分发手指滑动
     */
    void handleMove(){
        if (drawType == DrawAttribute.DrawStatus.NORMAL || drawType == DrawAttribute.DrawStatus.ERASER) {
            mDrawPath.lineTo(endX, endY);
            drawPath();
            startX = endX;
            startY = endY;
        } else if (drawType == DrawAttribute.DrawStatus.XIAN) {
            /**线条*/
            invalidate();
        } else if (drawType == DrawAttribute.DrawStatus.CIRCLE) {
            /**圆*/
            invalidate();
        } else if (drawType == DrawAttribute.DrawStatus.JUXING) {
            /**矩形*/
            invalidate();
        }else if (drawType == DrawAttribute.DrawStatus.TRIANGLE) {
            /**矩形*/
            invalidate();
        }
    }

    /**
     * 处理手势抬起
     * 添加到撤销记录中
     */
    void handleUp(){
        if (drawType == DrawAttribute.DrawStatus.NORMAL) {
            /**普通*/
            mUndoPaintDatas.add(new PaintData(new Path(mDrawPath), new Paint(mPaint)));
        } else if (drawType == DrawAttribute.DrawStatus.ERASER) {
            /**橡皮擦*/
            mUndoPaintDatas.add(new PaintData(new Path(mDrawPath), new Paint(mEraserPaint)));
        } else if (drawType == DrawAttribute.DrawStatus.XIAN) {
            /**线条*/
            mUndoPaintDatas.add(new PaintData(new Path(shapePath), new Paint(mPaint)));
            drawShape();
        } else if (drawType == DrawAttribute.DrawStatus.CIRCLE) {
            /**圆*/
            mUndoPaintDatas.add(new PaintData(new Path(shapePath), new Paint(mPaint)));
            drawShape();
        } else if (drawType == DrawAttribute.DrawStatus.JUXING) {
            /**矩形*/
            mUndoPaintDatas.add(new PaintData(new Path(shapePath), new Paint(mPaint)));
            drawShape();
        } else if (drawType == DrawAttribute.DrawStatus.TRIANGLE) {
            /**三角形*/
            mUndoPaintDatas.add(new PaintData(new Path(shapePath), new Paint(mPaint)));
            drawShape();
        }
        if (onUndoEnabledListener != null) {
            onUndoEnabledListener.onUndoEnabled(true);
        }
    }


    /**
     * 判断是否移动
     * @return
     */
    boolean calculateMinDistance() {
        double distance = Math.sqrt(Math.pow(Math.abs(endX - startX), 2) + Math.pow(Math.abs(endY - startY), 2));
        if (distance >= slop) {
            return true;
        }
        return false;
    }


    /**
     * 绘制路径
     */
    void drawPath() {
        if (mBufferCanvas == null) {
            return;
        }
        if (drawType == DrawAttribute.DrawStatus.ERASER) {
            mBufferCanvas.drawPath(mDrawPath, mEraserPaint);
        } else {
            mBufferCanvas.drawPath(mDrawPath, mPaint);
        }
        invalidate();

    }

    /**
     * 绘制形状
     */
    void drawShape() {
        if (mBufferCanvas == null) {
            return;
        }
        if (drawType == DrawAttribute.DrawStatus.ERASER) {
            mBufferCanvas.drawPath(shapePath, mEraserPaint);
        } else {
            mBufferCanvas.drawPath(shapePath, mPaint);
        }
        invalidate();
        startX = startY = endX = endY = -10;
        shapePath.reset();
        rectF.setEmpty();
        invalidate();
    }

    Path shapePath = new Path();

    /**
     * 绘制直线
     * @param canvas
     */
    void drawLine(Canvas canvas) {
        if (calculateMinDistance()) {
            canvas.drawLine(startX, startY, endX, endY, mPaint);
            shapePath.reset();
            shapePath.moveTo(startX, startY);
            shapePath.lineTo(endX, endY);
        }

    }

    /**
     * 绘制圆形
     * @param canvas
     */
    void drawCircle(Canvas canvas) {
        if (calculateMinDistance()) {
            rectF.set(startX, startY, endX, endY);
            canvas.drawOval(rectF, mPaint);
            shapePath.reset();
            shapePath.addOval(rectF, Path.Direction.CCW);
        }
    }

    /**
     * 绘制矩形
     * @param canvas
     */
    void drawRect(Canvas canvas) {
        if (calculateMinDistance()) {
            rectF.set(startX, startY, endX, endY);
            canvas.drawRect(rectF, mPaint);
            shapePath.reset();
            shapePath.addRect(rectF, Path.Direction.CCW);
        }
    }

    /**
     * 绘制等腰三角形
     */
    float[] points = new float[12];
    void drawTriangle(Canvas canvas) {
        if (calculateMinDistance()) {

            Log.e(TAG, "drawTriangle" + points.toString());

            points[0] = Math.abs(endX + startX) / 2;
            points[1] = startY;
            points[2] = endX;
            points[3] = endY;

            points[4] = endX;
            points[5] = endY;
            points[6] = startX;
            points[7] = endY;

            points[8] = startX;
            points[9] = endY;
            points[10] = Math.abs(endX + startX) / 2;
            points[11] = startY;

            canvas.drawLines(points, mPaint);
            shapePath.reset();
            shapePath.moveTo(points[0], points[1]);
            shapePath.lineTo(points[2], points[3]);
            shapePath.lineTo(points[4], points[5]);
            shapePath.lineTo(points[6], points[7]);
            shapePath.lineTo(points[8], points[9]);
            shapePath.lineTo(points[10], points[11]);
        }
    }

    DrawAttribute.DrawStatus drawType = DrawAttribute.DrawStatus.NORMAL;

    public void setDrawType(DrawAttribute.DrawStatus drawShape) {
        this.drawType = drawShape;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBitmap(canvas);
        dispatchDrawShape(canvas);
    }

    /**
     * 手指 滑动时 绘制
     * @param canvas
     */
    void dispatchDrawShape(Canvas canvas) {
        if (drawType == DrawAttribute.DrawStatus.XIAN) {
            drawLine(canvas);
        } else if (drawType == DrawAttribute.DrawStatus.CIRCLE) {
            drawCircle(canvas);
        } else if (drawType == DrawAttribute.DrawStatus.JUXING) {
            drawRect(canvas);
        } else if (drawType == DrawAttribute.DrawStatus.TRIANGLE) {
            drawTriangle(canvas);
        }
    }


    void drawBitmap(Canvas canvas) {
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, mPaint);
        }
    }


    /***
     * 设置图片
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        drawBitmap(bitmap);
    }

    /***
     * 设置画笔颜色
     * @param color
     */
    public void setColor(@ColorInt int color) {
        DrawingColor = color;
        if (mPaint != null) {
            mPaint.setColor(color);
        }
    }

    /***
     * 设置 随机画笔颜色
     */
    public void setRandomColor(boolean random) {
        randomColor = random;
    }

    private void drawBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        int width = getWidth();
        int height = getWidth();
        mBufferBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        mBufferCanvas = new Canvas(mBufferBitmap);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        invalidate();
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
            mDrawPath.reset();
            rectF.setEmpty();
            mBufferCanvas.drawColor(0xffffff, PorterDuff.Mode.CLEAR);
            for (PaintData paintData : mUndoPaintDatas) {
                //重新绘制每个path
                paintData.draw(mBufferCanvas);
            }
            invalidate();
        }
    }


    /**
     * 是否使用涂色模式
     *
     * @param canFill
     */
    public void useFill(boolean canFill) {
        this.hasFill = canFill;
    }

    public void clearScreen() {
        if (mBufferCanvas != null) {
            mDrawPath.reset();
            mBufferCanvas.drawColor(0xffffffff, PorterDuff.Mode.DST_OUT);
            rectF.setEmpty();
            mUndoPaintDatas.add(new PaintData(new Path(mDrawPath), new Paint(mPaint)));
            recycleList();
            invalidate();
            if (onUndoEnabledListener != null) {
                onUndoEnabledListener.onAllDisabled(); //都不可用
            }
        }
    }

    void recycleList() {
        mUndoPaintDatas.clear();
        mRedoPaintDatas.clear();
    }


    public boolean canExit() {
        return mUndoPaintDatas.isEmpty();
    }


    public void setPaintSize(int size) {
        if (mPaint != null) {
            mPaint.setStrokeWidth(size);
        }
        if (mEraserPaint != null) {
            mEraserPaint.setStrokeWidth(size);
        }
    }

    public void setDrawingColor(int color) {
        if (mPaint != null) {
            mPaint.setColor(color);
        }
    }

    /***
     * 保存为图片
     * @param share
     */
    public Uri saveBitmap(boolean share) {
        try {
            Bitmap bitmap = drawBitmap();
            if (bitmap != null) {
                return StorageInSDCard.saveBitmapInExternalStorage(bitmap, mContext, share);
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
        canvas.drawColor(Color.WHITE);
        draw(canvas);
        return bmp;
    }


    public void setDrawingColor() {
        setDrawingColor(randomColor());
    }

    public void setFillColor(int color) {
        if (mPaint != null) {
            mPaint.setColor(color);
        }
    }

    public void setFillColor() {
        mPaint.setColor(randomColor());
    }


    /**
     * 根据x,y获得改点颜色，进行填充
     *
     * @param x
     * @param y
     */
    int[] pixels;

    private void fillColorToSameArea(int x, int y) {
        if (mBufferBitmap == null) {
            return;
        }
        //边界检查
        if (x >= mBufferBitmap.getWidth() || y >= mBufferBitmap.getHeight()) {
            return;
        }
        //Log.e(TAG, "fillColorToSameArea 1 ");
        int pixel = mBufferBitmap.getPixel(x, y);
        // 透明 边缘 相同颜色已填充
        if (DrawingColor == pixel) {
            return;
        }
        //Log.e(TAG, "fillColorToSameArea 2 ");
        int newColor = DrawingColor;

        int w = mBufferBitmap.getWidth();
        int h = mBufferBitmap.getHeight();
        //拿到该bitmap的颜色数组
        if (pixels == null) {
            pixels = new int[w * h];
        }
        mBufferBitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        //填色
        fillColor(pixels, w, h, pixel, newColor, x, y);

        //防止 外部区域填充 仅对四个角做了判断
        boolean noneFill = pixels[0] == newColor
                || pixels[w - 1] == newColor
                || pixels[w * h - w + 1] == newColor
                || pixels[w * h - 1] == newColor;
        if (noneFill) {
            return;
        }
        //重新设置bitmap
        mBufferBitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        invalidate();

    }


    /**
     * @param pixels   像素数组
     * @param w        宽度
     * @param h        高度
     * @param pixel    当前点的颜色
     * @param newColor 填充色
     * @param i        横坐标
     * @param j        纵坐标
     */
    private void fillColor(int[] pixels, int w, int h, int pixel, int newColor, int i, int j) {
        //步骤1：将种子点(x, y)入栈；
        mStacks.push(new Point(i, j));

        //步骤2：判断栈是否为空，
        // 如果栈为空则结束算法，否则取出栈顶元素作为当前扫描线的种子点(x, y)，
        // y是当前的扫描线；
        while (!mStacks.isEmpty()) {


            /**
             * 步骤3：从种子点(x, y)出发，沿当前扫描线向左、右两个方向填充，
             * 直到边界。分别标记区段的左、右端点坐标为xLeft和xRight；
             */
            Point seed = mStacks.pop();
            //L.e("seed = " + seed.x + " , seed = " + seed.y);
            int count = fillLineLeft(pixels, pixel, w, h, newColor, seed.x, seed.y);
            int left = seed.x - count + 1;
            count = fillLineRight(pixels, pixel, w, h, newColor, seed.x + 1, seed.y);
            int right = seed.x + count;


            /**
             * 步骤4：
             * 分别检查与当前扫描线相邻的y - 1和y + 1两条扫描线在区间[xLeft, xRight]中的像素，
             * 从xRight开始向xLeft方向搜索，假设扫描的区间为AAABAAC（A为种子点颜色），
             * 那么将B和C前面的A作为种子点压入栈中，然后返回第（2）步；
             */
            //从y-1找种子
            if (seed.y - 1 >= 0)
                findSeedInNewLine(pixels, pixel, w, h, seed.y - 1, left, right);
            //从y+1找种子
            if (seed.y + 1 < h)
                findSeedInNewLine(pixels, pixel, w, h, seed.y + 1, left, right);
        }


    }

    /**
     * 在新行找种子节点
     *
     * @param pixels
     * @param pixel
     * @param w
     * @param h
     * @param i
     * @param left
     * @param right
     */
    private void findSeedInNewLine(int[] pixels, int pixel, int w, int h, int i, int left, int right) {
        /**
         * 获得该行的开始索引
         */
        int begin = i * w + left;
        /**
         * 获得该行的结束索引
         */
        int end = i * w + right;

        boolean hasSeed = false;

        int rx = -1, ry = -1;

        ry = i;

        /**
         * 从end到begin，找到种子节点入栈（AAABAAAB，则B前的A为种子节点）
         */
        while (end >= begin) {
            if (pixels[end] == pixel) {
                if (!hasSeed) {
                    rx = end % w;
                    mStacks.push(new Point(rx, ry));
                    hasSeed = true;
                }
            } else {
                hasSeed = false;
            }
            end--;
        }
    }

    /**
     * 往右填色，返回填充的个数
     *
     * @return
     */
    private int fillLineRight(int[] pixels, int pixel, int w, int h, int newColor, int x, int y) {
        int count = 0;

        while (x < w) {
            //拿到索引
            int index = y * w + x;

            if (needFillPixel(pixels, pixel, index)) {
                pixels[index] = newColor;
                count++;
                x++;
            } else {
                break;
            }

        }

        return count;
    }


    /**
     * 往左填色，返回填色的数量值
     *
     * @return
     */
    private int fillLineLeft(int[] pixels, int pixel, int w, int h, int newColor, int x, int y) {
        int count = 0;
        while (x >= 0) {
            //计算出索引
            int index = y * w + x;


            //找到边界
            if (pixel != pixels[index]) {
                Log.e(TAG, "fillLineLeft ");
                break;
            }

            if (needFillPixel(pixels, pixel, index)) {
                pixels[index] = newColor;
                count++;
                x--;
            } else {
                break;
            }

        }
        return count;
    }


    private boolean needFillPixel(int[] pixels, int pixel, int index) {
        return pixels[index] == pixel;
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

    @Override
    protected void onDetachedFromWindow() {
        release();
        super.onDetachedFromWindow();
    }

    void release() {
        if (mBufferBitmap != null && !mBufferBitmap.isRecycled()) {
            mBufferBitmap.recycle();
        }
        mStacks.clear();
        recycleList();
    }


}
