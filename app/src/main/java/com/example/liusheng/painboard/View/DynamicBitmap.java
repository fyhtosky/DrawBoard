package com.example.liusheng.painboard.View;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * Created by chenzhen on 2019/3/15.
 */

public class DynamicBitmap extends Dynamic {


    public int bitmapHalfWidth, bitmapHalfHeight;

    public Bitmap bitmap;
    public Matrix matrix;


    public DynamicBitmap(Bitmap image) {
        super();
        this.bitmap = image;
        this.matrix = new Matrix();
        this.paint.setColor(Color.RED);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        BitmapShader patternBMPshader = new BitmapShader(image, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        this.paint.setColor(0xFFFFFFFF);
        this.paint.setShader(patternBMPshader);
        bitmapHalfWidth = bitmap.getWidth() / 2;
        bitmapHalfHeight = bitmap.getHeight() / 2;
    }


    public void config(float x, float y) {
        initX = x - bitmapHalfWidth;
        initY = y - bitmapHalfHeight;
        currentX = initX;
        currentY = initY;
    }

    public void draw(Canvas canvas) {
        rotation = rotation + rotationSpeed;
        alpha = 255;
        matrix.reset();
        matrix.postRotate(rotation, bitmapHalfWidth, bitmapHalfHeight);
        matrix.postTranslate(currentX, currentY);
        paint.setAlpha(alpha);
        canvas.drawBitmap(bitmap, matrix, paint);
    }


}
