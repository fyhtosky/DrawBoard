package com.example.liusheng.painboard.View;

import android.graphics.Canvas;
import android.graphics.Path;

/**
 * Created by chenzhen on 2019/3/15.
 */

public class DynamicPath extends Dynamic {


    Path path;

    public DynamicPath(Path p,int width) {
        super();
        this.path = p;
        this.paint.setStrokeWidth(width);
    }

    @Override
    public void config(float x, float y) {
        //
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }
}
