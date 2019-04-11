/*
 * Copyright (C) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.liusheng.painboard.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.annotation.DrawableRes;

import java.io.ByteArrayOutputStream;

/**
 * BitmapUtils
 *
 * @author panyi
 */
public class BitmapUtils {


    private static final String TAG = "BitmapUtils";

    public static Bitmap decodeBitmapFromData(byte[] data, int reqWidth, int reqHeight) {
        final Options options = new Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeByteArray(data, 0, data.length, options); // 第一次解码，目的是：读取图片长宽
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeByteArray(data, 0, data.length, options); // 产生一个稍大的缩略图
        return src; // 通过得到的bitmap进一步产生目标大小的缩略图
    }

    public static Bitmap decodeBitmapFromResource(Context context, @DrawableRes int res, int reqWidth, int reqHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), res, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inMutable = true;
        return BitmapFactory.decodeResource(context.getResources(), res, options);
    }


    public static Bitmap decodeBitmapFromBitmap(Bitmap bitmap, int reqWidth, int reqHeight) throws Exception{
        if (bitmap == null) {
            throw new RuntimeException("decodeBitmapFromBitmap() bitmap 不能为空");
        }
        if (bitmap.isRecycled()) {
            throw new RuntimeException("decodeBitmapFromBitmap() 无法处理isRecycled()的bitmap");
        }
        if (reqWidth <= 0 || reqHeight <= 0) {
            throw new RuntimeException("decodeBitmapFromBitmap() reqWidth or reqHeight 不能<= 0");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);
        byte[] data = bos.toByteArray();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeByteArray(data, 0, data.length, options); // 第一次解码，目的是：读取图片长宽
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeByteArray(data, 0, data.length, options); // 产生一个稍大的缩略图
        return src;
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
