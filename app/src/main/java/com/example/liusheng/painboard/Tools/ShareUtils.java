package com.example.liusheng.painboard.Tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by chenzhen on 2019/2/18.
 */

public class ShareUtils {


    public static boolean share(Context context, Uri shareUri){
        if (context == null || shareUri == null){
            return false;
        }
        Intent shareIntent = new Intent();
        shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
        return true;
    }


    public static boolean share(Context context, String sharePath){
        if (context == null || sharePath == null){
            return false;
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            // authority 第三方库 PictureSelector
            uri = FileProvider.getUriForFile(context,context.getPackageName()+".provider",new File(sharePath));
        }else {
            uri = Uri.fromFile(new File(sharePath));
        }
        return share(context,uri);
    }

}
