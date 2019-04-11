package com.example.liusheng.painboard.View;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by chenzhen on 2019/3/8.
 */

public abstract class Dynamic {



    public float initX, initY, currentX, currentY;


    public int alpha = 100;
    public int alphaSpeed = 100;


    public float rotation = 10;
    public float rotationSpeed = 30;

    public Paint paint;
    Random random;

    protected Dynamic() {
        this.paint = new Paint();
        this.paint.setFilterBitmap(true);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStyle(Paint.Style.STROKE);
        init();
    }

    public void init() {
        random = new Random();
        rotationSpeed = random.nextInt(10);
        alphaSpeed = random.nextInt(150);
    }

    public void setColor(int color){
        this.paint.setColor(color);
    }

    public abstract void config(float x, float y);

    public abstract void draw(Canvas canvas);


}
