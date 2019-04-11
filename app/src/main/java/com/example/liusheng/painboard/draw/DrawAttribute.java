package com.example.liusheng.painboard.draw;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class DrawAttribute {


    public enum DrawStatus {
        //画笔
        NORMAL, FLUORE, MARK, PIXEL, TAN, BITMAP,
        //橡皮擦
        ERASER,
        //形状与
        JUXING, CIRCLE, XIAN,TRIANGLE
    }

    public static int screenHeight;
    public static int screenWidth;

    public static Bitmap getImageFromAssetsFile(Context context, String fileName, boolean isBackground) {
        Bitmap image = null;

        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isBackground) {
            image = Bitmap.createScaledBitmap(image, DrawAttribute.screenWidth, DrawAttribute.screenHeight, false);
        }
        return image;
    }
}
