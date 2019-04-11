package com.example.liusheng.painboard.View;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.liusheng.painboard.Tools.StorageInSDCard;
import com.example.liusheng.painboard.event.OnUndoEnabledListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * 涂色
 * Created by chenzhen on 2019/2/18.
 */

public class FillImageView extends PhotoView {

    static final String TAG = FillImageView.class.getSimpleName();

    static final int DEFAULT_STROKE_COLOR = Color.BLACK;

    Stack<Point> mStacks = new Stack<>();
    int mBorderColor = DEFAULT_STROKE_COLOR;
    int DrawingColor = DEFAULT_STROKE_COLOR; //绘制颜色

    boolean randomColor = false;

    Matrix mSuppMatrix = new Matrix();

    Bitmap mBitmap;

    List<Bitmap> mUndoBitmaps = new ArrayList<>();
    List<Bitmap> mRedoBitmaps = new ArrayList<>();
    boolean hasBorderColor = false;

    int[] pixels;

    String updatePath;

    boolean oom = false;//判断是否oom

    private OnUndoEnabledListener onUndoEnabledListener;
    private boolean excuted = false;//已经调用

    public void setOnUndoEnabledListener(OnUndoEnabledListener listener) {
        this.onUndoEnabledListener = listener;
    }

    public FillImageView(Context context) {
        this(context, null);
    }

    public FillImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public FillImageView(final Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        getAttacher().setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                final RectF displayRect = getDisplayRect();
                final float x = e.getX(), y = e.getY();
                if (displayRect != null) {
                    // Check to see if the user tapped on the photo
                    if (displayRect.contains(x, y)) {

                        float xResult = (x - displayRect.left) / displayRect.width();
                        float yResult = (y - displayRect.top) / displayRect.height();
                        // TODO: 2019/2/27 针对内存小机型 临时捕获并提示
                        try {
                            if (oom) {
                                Toast.makeText(context, "当前APP可用内存不足，涂色已保存，建议退出当前页面再进行涂色操作", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            fillColorToSameArea(xResult, yResult);
                        } catch (OutOfMemoryError error) {
                            oom = true;
                            Toast.makeText(context, "当前APP可用内存不足，涂色已保存，建议退出当前页面再进行涂色操作", Toast.LENGTH_SHORT).show();
                            updateBitmap(updatePath);
                            saveBitmap(false);
                            EventBus.getDefault().post(new Object());
                        }
                        isReset = false;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });

    }


    public void setUpdatePath(String path) {
        this.updatePath = path;
    }


    /***
     * 设置填充图片
     * @param bitmap
     */

    public void setBitmap(Bitmap bitmap, boolean init) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        setBitmap(bitmap);
        if (init) {
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
            mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();
            //拿到该bitmap的颜色数组
            pixels = new int[w * h];
            mUndoBitmaps.add(mBitmap);
        }
    }

    private void setBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        mSuppMatrix.reset();
        getSuppMatrix(mSuppMatrix);
        setImageBitmap(bitmap);
        setSuppMatrix(mSuppMatrix);
    }


    /***
     * 设置填充颜色
     * @param fillColor
     */
    public void setColor(int fillColor) {
        randomColor = false;
        DrawingColor = fillColor;
    }

    /**
     *
     */
    public void setRandomColor() {
        randomColor = true;
    }


    /**
     * 重新填色
     */
    boolean isReset = true;

    public void reset(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        recycleList();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        isReset = true;
        setImageBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true));
        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        mUndoBitmaps.add(mBitmap);
        if (bitmap == null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        if (onUndoEnabledListener != null) {
            onUndoEnabledListener.onAllDisabled();
        }
    }

    /***
     * 撤销
     * @return
     */
    public void undo() {
        int size = mUndoBitmaps.size();
        if (size <= 0) {
            return;
        }
        //Log.e(TAG, "undo size " + size);
        Bitmap bitmap = mUndoBitmaps.remove(size - 1);
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        setBitmap(bitmap);
        mRedoBitmaps.add(bitmap);
        if (onUndoEnabledListener != null) {
            //是否可用
            onUndoEnabledListener.onRedoEnabled(mRedoBitmaps.isEmpty() ? false : true);
            onUndoEnabledListener.onUndoEnabled(mUndoBitmaps.isEmpty() ? false : true);
        }
    }

    /**
     * 反撤销
     *
     * @return
     */
    public void redo() {
        int size = mRedoBitmaps.size();
        //Log.e(TAG, "redo size " + size);
        if (size <= 0) {
            return;
        }
        Bitmap bitmap = mRedoBitmaps.remove(size - 1);
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        setBitmap(bitmap);
        mUndoBitmaps.add(bitmap);
        if (onUndoEnabledListener != null) {
            //是否可用
            onUndoEnabledListener.onRedoEnabled(mRedoBitmaps.isEmpty() ? false : true);
            onUndoEnabledListener.onUndoEnabled(mUndoBitmaps.isEmpty() ? false : true);
        }
    }

    /***
     * 是否处于重置状态
     * @return
     */
    public boolean hasReset() {
        return mUndoBitmaps.size() <= 1;
    }


    /**
     * 根据x,y获得改点颜色，进行填充
     */

    private void fillColorToSameArea(float scaleX, float scaleY) {
        if (mBitmap == null) {
            return;
        }
        if (mBitmap.isRecycled()) {
            return;
        }
        float fx = mBitmap.getWidth() * scaleX;
        float fy = mBitmap.getHeight() * scaleY;

        int x = (int) fx;
        int y = (int) fy;

        //边界检查
        if (x >= mBitmap.getWidth() || y >= mBitmap.getHeight()) {
            return;
        }
        if (mBitmap.isRecycled()) {
            return;
        }
        int pixel = mBitmap.getPixel(x, y);
        // 透明 边缘 相同颜色已填充
        if (pixel == Color.TRANSPARENT || pixel == mBorderColor || DrawingColor == pixel) {
            return;
        }
        // TODO: 2019/2/19 内存消耗大待优化
        mUndoBitmaps.add(mBitmap.copy(Bitmap.Config.ARGB_8888, true));
        if (onUndoEnabledListener != null && !excuted) {
            onUndoEnabledListener.onUndoEnabled(true);
            excuted = true;
        }

        int newColor;
        if (randomColor) {
            newColor = randomColor();
        } else {
            newColor = DrawingColor;
        }

        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        //拿到该bitmap的颜色数组
        if (pixels == null) {
            pixels = new int[w * h];
        }
        try {
            mBitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        } catch (Exception e) {
            pixels = new int[w * h];
        }
        //填色
        fillColor(pixels, w, h, pixel, newColor, x, y);
        //重新设置bitmap
        mBitmap.setPixels(pixels, 0, w, 0, 0, w, h);


        mSuppMatrix.reset();
        getSuppMatrix(mSuppMatrix);

        setImageBitmap(mBitmap);

        setSuppMatrix(mSuppMatrix);


        //Log.e(TAG, "fill undo size " + mUndoBitmaps.size());

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
        int fillPixel = pixels[index];
        if (hasBorderColor) {
            //return pixels[index] != mBorderColor;
            return !isLookLikePaintColor(fillPixel, mBorderColor);
        } else {
            return fillPixel == pixel;
        }
    }


    /**
     * 根据色值判断和目标颜色是否近似
     * 此处  主要用来处理 图片有锯齿以及毛边问题
     */
    static final int dither = 20; //越大相似度越小
    static final int adither = 200; //越大相似度越小


    private static boolean isLookLikePaintColor(int sourceColor, int targetColor) {

        int a = Color.alpha(sourceColor);
        int r = Color.red(sourceColor);
        int g = Color.green(sourceColor);
        int b = Color.blue(sourceColor);

        int a1 = Color.alpha(targetColor);
        int r1 = Color.red(targetColor);
        int g1 = Color.green(targetColor);
        int b1 = Color.blue(targetColor);

        int alphaDither = Math.abs(a1 - a);
        int redDither = Math.abs(r1 - r);
        int greenDither = Math.abs(g1 - g);
        int blueDither = Math.abs(b1 - b);

        if (alphaDither < adither && redDither < dither && greenDither < dither && blueDither < dither) {
            return true;
        } else {
            return false;
        }

    }


//
//    private boolean needFillPixel(int[] pixels, int pixel, int index) {
//        if (hasBorderColor) {
//            return pixels[index] != mBorderColor;
//        } else {
//            return pixels[index] == pixel;
//        }
//    }

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
        super.onDetachedFromWindow();
        release();
    }

    void release() {
        recycleBitmap();
        recycleList();
        mStacks.clear();
    }


    void recycleBitmap() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }

    void recycleList() {
        for (Bitmap bitmap : mUndoBitmaps) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        for (Bitmap bitmap : mRedoBitmaps) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        mUndoBitmaps.clear();
        mRedoBitmaps.clear();
    }

    /***
     * 保存为图片 不透明
     */
    public Bitmap getSaveBitmap() {
        try {
            setZoomable(false);
            Bitmap bitmap = createViewBitmap(this);
            setZoomable(true);
            if (bitmap != null) {
                return bitmap;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /***
     * 更新已涂色图片
     */
    public Bitmap getUpdateBitmap() {
        try {
            setZoomable(false);
            BitmapDrawable drawable = (BitmapDrawable) getDrawable();
            setZoomable(true);
            if (drawable == null) {
                return null;
            }
            Bitmap bitmap = drawable.getBitmap();
            if (bitmap == null) {
                return null;
            }
            return bitmap;
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
            return null;
        }
    }

    /***
     * 保存为图片 不透明
     */
    private Uri saveBitmap(boolean share) {
        try {
            Bitmap bitmap = getSaveBitmap();
            if (bitmap != null) {
                return StorageInSDCard.saveBitmapInExternalStorage(bitmap, getContext(), share);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * 更新已涂色图片
     */
    private boolean updateBitmap(String imagePath) {
        try {
            Bitmap bitmap = getUpdateBitmap();
            if (bitmap == null) {
                return false;
            }
            return StorageInSDCard.updateBitmap(bitmap, imagePath);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
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
}
