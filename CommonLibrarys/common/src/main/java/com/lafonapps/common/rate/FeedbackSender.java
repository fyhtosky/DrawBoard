package com.lafonapps.common.rate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.lafonapps.adadapter.utils.CommonUtils;
import com.lafonapps.common.AdManager;
import com.lafonapps.common.R;

import java.util.Locale;

/**
 * Created by liujun on 2017/7/6.
 */

public class FeedbackSender {

    public static void sendEmail(String address, Context context) {
        String title = context.getResources().getString(R.string.issues_and_suggestions);

        String appName = CommonUtils.getAppDisplayName(AdManager.sharedApplication);
        String appVersionName = CommonUtils.getAppVersionName(AdManager.sharedApplication);
        String deviceModel = Build.MODEL;
//        String systemVersion = Build.DISPLAY;
        String androidVersion = Build.VERSION.RELEASE;
        String languageCode = Locale.getDefault().toString();;

        String subject = title + " - " + appName + "(" + appVersionName + ")" + "(" + deviceModel + "," + androidVersion + "," + languageCode + ")";
        String content = context.getResources().getString(R.string.please_describe_your_issues_or_suggestions_here);

        // 底部弹窗中的标题
        String chooserTitle = context.getResources().getString(R.string.select_mail_client);

          /**  可以直接跳转到邮件客户端，但是无法进入写邮件界面以及预置邮件内容 */
//        Intent emailIntent = new Intent(Intent.ACTION_SEND);
//        emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
//        emailIntent.setType("message/rfc822");
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
//        emailIntent.putExtra(Intent.EXTRA_TEXT, content); // 文本信息

        /**  会弹出多个支持邮件的应用选择，可以预置内容，但是操作麻烦，容易使用户放弃 */
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//        emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
//        emailIntent.setType("message/rfc822");
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
//        emailIntent.putExtra(Intent.EXTRA_TEXT, content); // 文本信息
//        try {
//            context.startActivity(Intent.createChooser(emailIntent, chooserTitle)); // "Choose an Email Client"
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//        }

        /** 可以直接跳入写邮件界面，并且可以预置内容 */
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + address));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
        emailIntent.putExtra(Intent.EXTRA_TEXT, content); // 文本信息
        try {
            context.startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
