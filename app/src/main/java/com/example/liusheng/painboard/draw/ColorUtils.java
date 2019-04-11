package com.example.liusheng.painboard.draw;

import android.graphics.Color;
import java.util.Random;

public class ColorUtils {
    public static boolean colorRandom = true;
    public static int color = Color.RED;
    public static int whiteSize = 6;
    public static int strokeSize = 10;

    public static int randomColor() {
        Random random = new Random();
        if (colorRandom) {
            color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            return color;
        } else {
            return color;
        }
    }

    public static int getwhiteSize() {
        return whiteSize;
    }

    public static int getstrokeSize() {
        return strokeSize;
    }

}
