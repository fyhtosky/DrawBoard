package com.lafonapps.adadapter.utils;

import android.content.Context;
import android.util.DisplayMetrics;


/**
 * Created by chenjie on 2017/7/5.
 */

public final class AdSize {

    private int width;
    private int height;
    private Context context;
    public AdSize(Context context) {
        this.context = context;
    }

    public AdSize(int width, int height, Context context) {
        this.context = context;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDPWidth() {
        return px2dp(width);
    }

    public int getDPHeight() {
        return px2dp(height);
    }
    /**
     * 像素单位转点单位。
     *
     * @param px 像素尺寸 pixels
     * @return 转换成dpi的尺寸
     */
    public float px2dp(float px) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        float dp = px / displayMetrics.density;
        return dp;
    }

    /**
     * 像素单位转点单位。
     *
     * @param px 像素尺寸 pixels
     * @return 转换成dpi的尺寸
     */
    public int px2dp(int px) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        float dp = px / displayMetrics.density;
        return (int) dp;
    }

    /**
     * 点单位转像素单位。
     *
     * @param dp 点尺寸 dpi
     * @return 转换成px的尺寸
     */
    public float dp2px(float dp) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        float px = dp * displayMetrics.density;
        return px;
    }

    /**
     * 点单位转像素单位。
     *
     * @param dp 点尺寸 dpi
     * @return 转换成px的尺寸
     */
    public int dp2px(int dp) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        float px = dp * displayMetrics.density;
        return (int)px;
    }
    @Override
    public String toString() {
        return getClass().getCanonicalName() + "@" + width + "_" + height;
    }
}
