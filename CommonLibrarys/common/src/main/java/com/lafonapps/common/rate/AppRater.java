package com.lafonapps.common.rate;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.lafonapps.adadapter.utils.AppStatusDetector;
import com.lafonapps.adadapter.utils.SPUtils;
import com.lafonapps.common.CommonConfig;
import com.lafonapps.common.AdManager;
import com.lafonapps.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/9/28.
 * <p>
 * 与评论相关的类
 */

public class AppRater {
    private final static String kRated = "Rated";

    public static final AppRater defaultAppRater = new AppRater();
    private static final String TAG = AppRater.class.getCanonicalName();

    private boolean canExitByBackEvent; //按back键是否可以退出应用
    private boolean alertToRating; //正在提示评论
    private RateText rateText; //提示用语

    private List<Listener> allListeners = new ArrayList<>();

    /**
     * 判断当前设备是否安装了应用商店
     *
     * @return
     */
    public boolean hasMarketInstalled() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("market://details?id=android.browser"));
        List list = AdManager.sharedApplication.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 是否可以评论。
     *
     * @return 当前设备没有安装应用商店，或者已经评论过就会返回false，其他情况返回true
     */
    public boolean shouldRate(Context context) {
        boolean shouldRate = isRated(context) == false && hasMarketInstalled();
        return shouldRate;
    }

    /**
     * 打开评论界面
     *
     * @param context
     */
    public void goRating(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarketIntent = new Intent(Intent.ACTION_VIEW, uri);
        goToMarketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarketIntent);
            setRated(context,true);

            Listener[] listeners = getAllListeners();
            for (Listener listener : listeners) {
                listener.rated();
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.no_market_install_message), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 弹出提示评论框，引导用户评论。使用自定义对话框
     *
     * @param context
     */
    public void promptToRateWithCustomDialog(final Context context) {
        if (!alertToRating) {
            alertToRating = true;

            RateText rateText = getRateText(context);

            final RateDialog dialog = new RateDialog.Builder(context)
                    .setMessage(rateText.message)
                    .setStarImageResId(rateText.starImageResId)
                    .setBottomLayout(false)
                    .setCancelable(true)
                    .setOnRateButtonClickListener(rateText.rateButtonTitle, new RateDialog.OnButtonClickListener() {
                        @Override
                        public void onButtonClick(RateDialog dialog, String buttonTitle) {
                            goRating(context);

                        }
                    })
                    .setOnSuggestionButtonClickListener(rateText.suggestionButtonTitle, new RateDialog.OnButtonClickListener() {
                        @Override
                        public void onButtonClick(RateDialog dialog, String buttonTitle) {
                            FeedbackSender.sendEmail(CommonConfig.sharedCommonConfig.feedbackEmailAddress, context);

                           setRated(context,true);
                        }
                    })
                    .setOnCancelButtonClickListener(rateText.cancelButtonTitle, new RateDialog.OnCancelListener() {
                        @Override
                        public void onCancel(RateDialog dialog) {

                        }
                    }).setOnDismissListener(new RateDialog.OnDismissListener() {
                        @Override
                        public void onDismiss(RateDialog dialog) {

                            alertToRating = false;
                        }
                    })
                    .show();
        }
    }

    private boolean promptToRateForBackEvent(final Activity activity, final boolean forcePrompt) {
        if (!alertToRating) {
            Log.d(TAG, "CustomDialog");

            alertToRating = true;

            RateText rateText = getRateText(activity);

            new RateDialog.Builder(activity)
                    .setStarImageResId(rateText.starImageResId)
                    .setMessage(rateText.message)
                    .setBottomLayout(true)
                    .setCancelable(true)
                    .setOnRateButtonClickListener(rateText.rateButtonTitle, new RateDialog.OnButtonClickListener() {
                        @Override
                        public void onButtonClick(RateDialog dialog, String buttonTitle) {
                            Log.d(TAG, "onRate");

                            goRating(activity);
                        }
                    })
                    .setOnSuggestionButtonClickListener(rateText.suggestionButtonTitle, new RateDialog.OnButtonClickListener() {
                        @Override
                        public void onButtonClick(RateDialog dialog, String buttonTitle) {
                            Log.d(TAG, "onSuggestion");

                            FeedbackSender.sendEmail(CommonConfig.sharedCommonConfig.feedbackEmailAddress, activity);

                            setRated(activity,true);
                        }
                    })
                    .setOnExitButtonClickListener(rateText.exitButtonTitle, new RateDialog.OnButtonClickListener() {
                        @Override
                        public void onButtonClick(RateDialog dialog, String buttonTitle) {
                            Log.d(TAG, "onExit");

                            activity.finish();
                        }
                    })
                    .setOnCancelListener(new RateDialog.OnCancelListener() {
                        @Override
                        public void onCancel(RateDialog dialog) {
                            Log.d(TAG, "onCancel");

                            if (!forcePrompt) {
                                Toast.makeText(activity, activity.getString(R.string.again_to_exit_app), Toast.LENGTH_SHORT).show();
                                canExitByBackEvent = true;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        canExitByBackEvent = false;
                                    }
                                }, 2000);
                            }
                        }
                    })
                    .setOnDismissListener(new RateDialog.OnDismissListener() {
                        @Override
                        public void onDismiss(RateDialog dialog) {
                            Log.d(TAG, "onDismiss");

                            alertToRating = false;
                        }
                    })
                    .show();
            return true;
        } else {
            Log.w(TAG, "is prompting");
            return false;
        }
    }

    public boolean handBackEventToPromtRate(final Activity activity) {
        // 捕获back键，当只有最后一个Activity的时候，如果没有评论过，则提示评论
        boolean isLastActivity = new AppStatusDetector().getActivities().length == 1;
        if (isLastActivity) {
            if (shouldRate(activity) && !canExitByBackEvent && promptToRateForBackEvent(activity, CommonConfig.sharedCommonConfig.forcePromptToRateForBackEvent)) {
                return true;
            } else {
                if (!alertToRating && !canExitByBackEvent && !shouldRate(activity)) {
                    Log.d(TAG, "Type back again to exit");

                    Toast.makeText(activity, activity.getString(R.string.again_to_exit_app), Toast.LENGTH_SHORT).show();

                    canExitByBackEvent = true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canExitByBackEvent = false;
                        }
                    }, 2000);

                    return true;
                }

            }
        }
        return false;
    }

    public RateText getRateText() {
        return rateText;
    }

    public RateText getRateText(Context context) {
        if (rateText == null) {
            if (CommonConfig.sharedCommonConfig.useRadicalismRateText) {
                rateText = RateText.radicalismRateText(context);
            } else {
                rateText = RateText.conservatismRateText(context);
            }
        }
        return rateText;
    }

    public void setRateText(RateText rateText) {
        this.rateText = rateText;
    }

    public synchronized void addListener(Listener listener) {
        if (listener != null && !allListeners.contains(listener)) {
            allListeners.add(listener);
            Log.d(TAG, "addListener:" + listener);
        }
    }

    public synchronized void removeListener(Listener listener) {
        if (allListeners.contains(listener)) {
            allListeners.remove(listener);
            Log.d(TAG, "removeListener:" + listener);
        }
    }

    public Listener[] getAllListeners() {
        return allListeners.toArray(new Listener[allListeners.size()]);
    }

    public static interface Listener {

        /**
         * 用户评论后被调用
         */
        void rated();

    }

    public static class RateText {
        public int starImageResId;
        public String message;
        public String rateButtonTitle;
        public String suggestionButtonTitle;
        public String exitButtonTitle;
        public String cancelButtonTitle;

        /**
         * 保守用语
         *
         * @param context
         * @return
         */
        public static RateText conservatismRateText(Context context) {
            RateText rateText = new RateText();

            rateText.starImageResId = R.drawable.ic_rate_smile_green;
            rateText.message = context.getString(R.string.rate_message, context.getString(R.string.app_name));
            rateText.rateButtonTitle = context.getString(R.string.rate_rate_button_title);
            rateText.suggestionButtonTitle = context.getString(R.string.rate_suggestion_button_title);
            rateText.exitButtonTitle = context.getString(R.string.rate_exit_button_title);
            rateText.cancelButtonTitle = context.getString(R.string.cancel);

            return rateText;
        }

        /**
         * 激进用语
         *
         * @param context
         * @return
         */
        public static RateText radicalismRateText(Context context) {
            RateText rateText = new RateText();

            rateText.starImageResId = R.drawable.ic_rate_star_green;
            rateText.message = context.getString(R.string.rate_message_2);
            rateText.rateButtonTitle = context.getString(R.string.rate_now);
            rateText.suggestionButtonTitle = context.getString(R.string.feedback);
            rateText.exitButtonTitle = context.getString(R.string.exit);
            rateText.cancelButtonTitle = context.getString(R.string.cancel);

            return rateText;
        }
    }

    public boolean isRated(Context context) {
        return (boolean) SPUtils.get(context,kRated,false);
    }

    public void setRated(Context context,boolean rated) {
        SPUtils.put(context,kRated,rated);
    }

}