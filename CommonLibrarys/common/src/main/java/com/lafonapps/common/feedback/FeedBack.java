package com.lafonapps.common.feedback;

import android.app.Application;
import android.content.Context;

import com.lafonapps.adadapter.utils.CommonUtils;
import com.lafonapps.common.CommonConfig;
import com.lafonapps.common.R;
import com.lafonapps.common.feedback.view.PromptDialog;
import com.lafonapps.common.feedback.view.SuggestionDialog;
import com.lafonapps.common.feedback.view.UserFeelDialog;
import com.lafonapps.common.rate.AppRater;

/**
 * Created by heshaobo on 2018/1/27.
 */

public class FeedBack {

    private static Application mApplication;


    public static void initialize(Application application, String applicationID, String clientKey){
        mApplication = application;
        FeedbackOperation.Configuration(application,applicationID,clientKey);
    }

    private static void setQQ(String qq){
        JumpContactOperation.SetQQ(qq);
    }

    private static void setEmail(String email){
        JumpContactOperation.SetEmail(email);
    }

    public static void showUserFeelDialog(final Context context){

        UserFeelDialog userFeelDialog = new UserFeelDialog(context);
        userFeelDialog.setBackgroundResource(R.drawable.ic_comment_one);
        String content = context.getString(R.string.think_this_app_feel, CommonUtils.getAppDisplayName(mApplication));
        userFeelDialog.setContent(content);
        userFeelDialog.setPositiveTitle(context.getString(R.string.easy_to_use));
        userFeelDialog.setNegativeTitle(context.getString(R.string.not_easy_to_use));
        userFeelDialog.setPositiveUserCallBack(new CallBack() {
            @Override
            public void run() {
                showRateDialog(context);
            }
        });

        userFeelDialog.setNegativeCallBack(new CallBack() {
            @Override
            public void run() {
                showSuggestionDialog(context);
            }
        });
        userFeelDialog.show();
    }

    public static void showRateDialog(final Context context){
        UserFeelDialog rateDialog = new UserFeelDialog(context);
        rateDialog.setBackgroundResource(R.drawable.ic_comment_two);
        String content = context.getString(R.string.go_app_store_rate);
        rateDialog.setContent(content);
        rateDialog.setPositiveTitle(context.getString(R.string.rate_rate_button_title));
        rateDialog.setNegativeTitle(context.getString(R.string.can_ren_ju_jue));
        rateDialog.setCancelable(false);
        rateDialog.setPositiveUserCallBack(new CallBack() {
            @Override
            public void run() {
                AppRater appRater = new AppRater();
                appRater.goRating(context);
            }
        });

        rateDialog.setNegativeCallBack(new CallBack() {
            @Override
            public void run() {

            }
        });
        rateDialog.show();
    }

    public static void showSuggestionDialog(final Context context){
        SuggestionDialog dialog = new SuggestionDialog(context);
        dialog.setPresetSuggestions(CommonConfig.sharedCommonConfig.presetSuggestions);
        dialog.setSuccessCallBack(new CallBack() {
            @Override
            public void run() {
                showThanksSuggestionDialog(context);
            }
        });
        dialog.show();
    }

    public static void showThanksSuggestionDialog(Context context){
        PromptDialog dialog = new PromptDialog(context);
        dialog.show();
    }

}
