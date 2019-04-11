package com.example.liusheng.painboard.bean;

import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by chenzhen on 2019/2/28.
 * 路径画笔记录 -- 撤销反撤销功能
 */

public class PaintData {

    static final String TAG = PaintData.class.getSimpleName();

    Paint[] paint;
    Path path;

    public PaintData() {
    }

    public PaintData(Path path, Paint... paint) {
        this.path = path;
        this.paint = paint;
    }

    public void draw(Canvas canvas) {
        if (paint.length > 1){
            //针对 荧光笔
            canvas.drawPath(path, paint[0]);
            canvas.drawPath(path, paint[1]);
        }else {
            canvas.drawPath(path, paint[0]);
        }
    }


    @Override
    public String toString() {
        return "PaintData{" +
                "paint=" + paint +
                ", path=" + path +
                '}';
    }
}
