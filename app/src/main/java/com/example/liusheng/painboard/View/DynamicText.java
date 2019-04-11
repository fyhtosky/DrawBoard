package com.example.liusheng.painboard.View;

import android.graphics.Canvas;
import android.graphics.Path;

/**
 * Created by chenzhen on 2019/3/15.
 */

public class DynamicText extends Dynamic {

    String text = "";
    Path path;

    public DynamicText(Path p,String t,float textSize) {
        super();
        this.text = t;
        this.path = p;
        this.paint.setTextSize(textSize);
    }

    @Override
    public void config(float x, float y) {
        //
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawTextOnPath(text, path, 0, 0, paint);
    }
}
