package com.example.liusheng.painboard.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.liusheng.painboard.draw.DrawAttribute;

/**
 * Created by menghanghang on 2018/1/30.
 */

public class TrajectoryView extends View {

    static float downX;
    static float downY;
    static float x;
    static float y;
    static RectF rect = new RectF();

    public static DrawAttribute.DrawStatus mode = null;
    public static Paint mPaint = null;


    public TrajectoryView(Context context) {
        super(context);
    }

    public TrajectoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrajectoryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rect.set(downX, downY, x, y);
        if (this.mode == DrawAttribute.DrawStatus.CIRCLE) {
            canvas.drawOval(rect, mPaint);
        } else if (this.mode == DrawAttribute.DrawStatus.JUXING) {
            canvas.drawRect(rect, mPaint);
        } else if (this.mode == DrawAttribute.DrawStatus.XIAN) {
            canvas.drawLine(downX, downY, x, y, mPaint);
        }
    }
}
