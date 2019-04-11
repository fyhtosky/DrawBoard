package com.example.liusheng.painboard.Tools;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class StorageInSDCard {

    private static final String TAG = "StorageInSDCard - ";

    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera";
    public static final String DESCRIPTION = "PaintBoard";

    public static boolean IsExternalStorageAvailableAndWriteable() {
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            //you can read and write the media
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            //you can only read the media
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            externalStorageAvailable = externalStorageWriteable = false;
        }
        return externalStorageAvailable && externalStorageWriteable;
    }


    /**
     * 更新 已涂色的模板
     *
     * @param bitmap
     * @param fileName
     */
    public static boolean updateBitmap(Bitmap bitmap, String fileName) {
        if (bitmap == null || fileName == null){
            return false;
        }
        try {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }


    /**
     * 保存
     *
     * @param bitmap
     * @param context1
     * @param share
     * @return 图片uri
     */
    public static Uri saveBitmapInExternalStorage(Bitmap bitmap, Context context1, boolean share) {
        if (bitmap == null || context1 == null){
            return null;
        }
        try {
            Context context = context1.getApplicationContext();
            if (IsExternalStorageAvailableAndWriteable()) {
                File extStorage = new File(FILE_PATH);
                if (!extStorage.exists()) {
                    extStorage.mkdirs();
                }
                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(extStorage, fileName);
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION);
                values.put(MediaStore.Images.Media.WIDTH, bitmap.getWidth());
                values.put(MediaStore.Images.Media.HEIGHT, bitmap.getHeight());
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                context.sendBroadcast(intent);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                if (share) {
                    // 分享
                    ShareUtils.share(context, uri);
                }
                return uri;
            }
            return null;
        } catch (Exception e) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return null;
        }
    }


}
