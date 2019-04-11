package com.example.liusheng.painboard.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {
    private Paint mPaint;
    private int paintSize;
    private int paintColor;
    private DRAWMODE mode;

    public enum DRAWMODE {
        CIRCLE,
        RECT,
        LINE1,
        LINE2,
        DASHLINE1,
        DASHLINE2
    }

    public DrawView(Context context, int paintSize, int paintColor, DRAWMODE drawMode) {
        super(context);
        this.paintSize = paintSize;
        this.paintColor = paintColor;
        this.mode = drawMode;
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG); // 画笔抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(paintSize);
        mPaint.setColor(paintColor);


        int margin = paintSize / 2;
        if (this.mode == DRAWMODE.CIRCLE) {
            RectF rectF = new RectF(0 + margin, 0 + margin, getWidth() - margin, getHeight() - margin);
            canvas.drawOval(rectF, mPaint);
        } else if (this.mode == DRAWMODE.RECT) {
            Path mPath = new Path();
            mPath.moveTo(0 + margin, 0 + margin);
            mPath.lineTo(0 + margin, getHeight() - margin);
            mPath.lineTo(getWidth() - margin, getHeight() - margin);
            mPath.lineTo(getWidth() - margin, 0 + margin);
            mPath.lineTo(0 + margin, 0 + margin);
            canvas.drawPath(mPath, mPaint);
        } else if (this.mode == DRAWMODE.LINE1) {
            Path mPath = new Path();
            mPath.moveTo(margin, margin);
            mPath.lineTo(getWidth() - margin, getHeight() - margin);
            canvas.drawPath(mPath, mPaint);
        } else if (this.mode == DRAWMODE.LINE2) {
            Path mPath = new Path();
            mPath.moveTo(0 + margin, getHeight() - margin);
            mPath.lineTo(getWidth() - margin, 0 + margin);
            canvas.drawPath(mPath, mPaint);
        } else if (this.mode == DRAWMODE.DASHLINE1) {

            PathEffect effect = new DashPathEffect(new float[] {20, 20}, 0);
            PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
            mPaint.setPathEffect(effect);

            Path mPath = new Path();
            mPath.moveTo(0 + margin, 0 + margin);
            mPath.lineTo(getWidth() - margin, getHeight() - margin);
            canvas.drawPath(mPath, mPaint);
        } else if (this.mode == DRAWMODE.DASHLINE2) {

            PathEffect effect = new DashPathEffect(new float[] {20, 20}, 0);
            PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
            mPaint.setPathEffect(effect);

            Path mPath = new Path();
            mPath.moveTo(0 + margin, getHeight() - margin);
            mPath.lineTo(getWidth() - margin, 0 + margin);
            canvas.drawPath(mPath, mPaint);
        }
    }
}
