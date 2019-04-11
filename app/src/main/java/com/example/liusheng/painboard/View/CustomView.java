package com.example.liusheng.painboard.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 王世坚 on 2018/3/22.
 */

public class CustomView extends View {

    static final String TAG = CustomView.class.getSimpleName();

    public int paintColor = Color.BLACK;
    public Bitmap bitmap;

    //画笔
    Paint paint;

    //画布宽
    int canvasWidth;
    //画布高
    int canvasHeight;
    //圆半径
    int radius;

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.canvasWidth = w;
        this.canvasHeight = h;
        this.radius = canvasWidth / 2;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (selected) {
            drawSelected(canvas);
        } else {
            if (bitmap != null) {
                drawBitmap(canvas);
            } else {
                //Log.e(TAG, "drawNormal ");
                drawNormal(canvas);
            }
        }

    }

    /**
     * 绘制图片
     *
     * @param canvas
     */
    private void drawBitmap(Canvas canvas) {
        int padding = 1;
        Rect bitmapRect = new Rect(padding, padding, (radius - padding) * 2, (radius - padding) * 2);
        canvas.drawBitmap(bitmap, null, bitmapRect, paint);
    }


    /**
     * 普通状态
     *
     * @param canvas
     */
    private void drawNormal(Canvas canvas) {

        paint.setColor(paintColor);

        //外圈圆画笔宽度
        int externalCircleWidth;
        //中部空心圆画笔宽度
        int middleCircleWidth;
        //内圈圆半径
        int innerCircleWidth;
        //整个同心圆距离边界距离
        int padding = 1;
        //圆心
        Point circleCenter = new Point(canvasHeight / 2, canvasHeight / 2);
        externalCircleWidth = 7;
        middleCircleWidth = 2;
        innerCircleWidth = radius - externalCircleWidth - middleCircleWidth - padding;

        //为避免Item重用导致画笔变成绘制模式而非填充模式，在此重置一下画笔填充方式。
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(circleCenter.x, circleCenter.y, innerCircleWidth, paint);

        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(externalCircleWidth);

        canvas.drawCircle(circleCenter.x, circleCenter.y, radius - externalCircleWidth / 2 - padding, paint);
    }

    /**
     * 被选中
     *
     * @param canvas
     */
    private void drawSelected(Canvas canvas) {

        // 3个圆的宽度
        int width = radius / 3;
        //圆心
        Point circleCenter = new Point(canvasHeight / 2, canvasHeight / 2);

        //为避免Item重用导致画笔变成绘制模式而非填充模式，在此重置一下画笔填充方式。
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(width);

        //外圆
        paint.setColor(paintColor);
        canvas.drawCircle(circleCenter.x, circleCenter.y, width * 3, paint);

        //中间圆
        paint.setColor(Color.WHITE);
        canvas.drawCircle(circleCenter.x, circleCenter.y, width * 2, paint);

        //内圆
        paint.setColor(Color.GRAY);
        canvas.drawCircle(circleCenter.x, circleCenter.y, width, paint);


    }


    /**
     * 设置新的颜色进行绘制。
     */
    public void setColor(int color) {
        this.bitmap = null;
        paintColor = color;
        postInvalidate();
    }


    boolean selected = false;

    public void setSelected(boolean select) {
        this.selected = select;
        postInvalidate();
    }


    /**
     * 设置背景图片进行绘制。
     */
    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        postInvalidate();
    }


}
