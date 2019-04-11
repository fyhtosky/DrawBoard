package com.example.liusheng.painboard.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;

import com.example.liusheng.painboard.BuildConfig;
import com.example.liusheng.painboard.constant.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by chenzhen on 2017/10/10.
 */

public class FileHelper {

    private Context context;
    private static volatile FileHelper instance;

    private FileHelper(Context context) {
        this.context = context;
    }


    public static FileHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (FileHelper.class) {
                if (instance == null) {
                    instance = new FileHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public boolean copyAssetsToDst(String srcPath, String cacheDir) {
        try {
            File file = getTemplateCacheDir(cacheDir);//获取涂色模板存储目录

            // 针对 升级添加 涂色模板
            //文件目录存在 文件个数大于零 同版本app 则无需拷贝   否则 就需要重新拷贝
            if (file.exists() && file.listFiles().length > 0 && !isAppUpdate()) {
                return true;
            }
            AssetManager assets = context.getAssets();
            String[] fileNames = assets.list(srcPath);
            if (fileNames.length > 0) {
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (int i = 0; i < fileNames.length; i++) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(srcPath + File.separator + fileNames[i],cacheDir + File.separator + fileNames[i]);
                    } else { // assets 文件夹
                        copyAssetsToDst(fileNames[i],cacheDir + File.separator + fileNames[i]);
                    }
                }

            } else {
                //File outFile = new File(Environment.getExternalStorageDirectory(), dstPath);
                //文件不存在 则写入
                if (!file.exists()){
                    InputStream is = context.getAssets().open(srcPath);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int byteCount;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public String getDiskCacheDir() {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public File getTemplateCacheDir(String cacheDir) {
        return new File(getDiskCacheDir(), cacheDir);
    }



    public boolean isAppUpdate(){
        SharedPreferences pref = context.getSharedPreferences(Constant.APP_CONFIG,Context.MODE_PRIVATE);
        int versionCode = pref.getInt(Constant.APP_VERSION_CODE,0);
        pref.edit().putInt(Constant.APP_VERSION_CODE,BuildConfig.VERSION_CODE).apply();
        if (BuildConfig.VERSION_CODE > versionCode){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 清理之前缓存涂色模板
     */
    public void cleanCache(){
        File cacheFile = getTemplateCacheDir(Constant.TEMPLATE_CACHE_DIR_NAME);
        delete(cacheFile);
    }

    public void delete(File deleteFile){
        if (deleteFile == null){
            return;
        }
        if (deleteFile.isDirectory()){
            File[] dirs = deleteFile.listFiles();
            for (File file : dirs) {
                if (file.isDirectory()){
                    delete(file);
                }else {
                    file.delete();
                }
            }
        }else {
            deleteFile.delete();
        }
    }

}

