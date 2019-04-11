package com.lafonapps.common.rate;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lafonapps.common.R;

/**
 * Created by chenjie on 2018/4/10.
 */
public class RateDialog extends Dialog {

    private final static String TAG = RateDialog.class.getCanonicalName();
    /**
     * 设置确定取消按钮的回调
     */
    private OnButtonClickListener onRateButtonClickListener;
    private OnButtonClickListener onSuggestionButtonClickListener;
    private OnButtonClickListener onExitButtonClickListener;
    private OnCancelListener onCancelListener;
    private OnDismissListener onDismissListener;
    /**
     * 显示的图片
     */
    private ImageView starsImageView1;
    private ImageView starsImageView2;
    private ImageView starsImageView3;
    private ImageView starsImageView4;
    private ImageView starsImageView5;
    /**
     * 显示的消息
     */
    private TextView messageTextView;
    /**
     * 确认和取消按钮
     */
    private Button rateButton, suggestionButton, exitButton, cancelButton;

    /**
     * 都是内容数据
     */
    private String message;
    private String rateButtonTitle, suggestionButtonTitle, exitButtonTitle, cancelButtonTitle;

    private int starImageResId = R.drawable.ic_rate_star_green;

    private Context context;

    /**
     * 是否显示在屏幕底部，默认为false
     */
    private boolean bottomLayout;
    /**
     * 点击对话框外部区域是否触发取消操作。取消操作会使当前对话框消失，并回调OnCancelListenner
     */
    private boolean cancelable;

    private RateDialog(Context context) {
        super(context, R.style.RateDialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(cancelable);
        //初始化界面控件
        starsImageView1 = findViewById(R.id.rate_image_1);
        starsImageView2 = findViewById(R.id.rate_image_2);
        starsImageView3 = findViewById(R.id.rate_image_3);
        starsImageView4 = findViewById(R.id.rate_image_4);
        starsImageView5 = findViewById(R.id.rate_image_5);
        messageTextView = findViewById(R.id.message);

        rateButton = findViewById(R.id.go_rate_button);
        suggestionButton = findViewById(R.id.suggestion_button);
        exitButton = findViewById(R.id.exit_button);
        cancelButton = findViewById(R.id.cancel_button);

        configButton(rateButton, rateButtonTitle, onRateButtonClickListener);
        configButton(suggestionButton, suggestionButtonTitle, onSuggestionButtonClickListener);
        configButton(exitButton, exitButtonTitle, onExitButtonClickListener);

        configCancelButton(cancelButton, cancelButtonTitle, onCancelListener);

        starsImageView1.setImageResource(starImageResId);
        starsImageView2.setImageResource(starImageResId);
        starsImageView3.setImageResource(starImageResId);
        starsImageView4.setImageResource(starImageResId);
        starsImageView5.setImageResource(starImageResId);

        messageTextView.setText(message);
        setBottomLayout(bottomLayout);

        Log.d(TAG, "onCreate");

    }

    @Override
    public void cancel() {
        super.cancel();
        if (onCancelListener != null) {
            onCancelListener.onCancel(this);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }

    private void configButton(final Button button, final String buttonTitle, final OnButtonClickListener listener) {
        if (!TextUtils.isEmpty(buttonTitle)) {
            button.setText(buttonTitle);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onButtonClick(RateDialog.this, buttonTitle);

                        dismiss();
                    }
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }
    }

    private void configCancelButton(final Button button, final String buttonTitle, final OnCancelListener listener) {
        if (!TextUtils.isEmpty(buttonTitle)) {
            button.setText(buttonTitle);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCancel(RateDialog.this);

                        cancel();
                    }
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }
    }

    public OnButtonClickListener getOnRateButtonClickListener() {
        return onRateButtonClickListener;
    }

    public OnButtonClickListener getOnSuggestionButtonClickListener() {
        return onSuggestionButtonClickListener;
    }

    public OnButtonClickListener getOnExitButtonClickListener() {
        return onExitButtonClickListener;
    }

    public OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }

    public OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 设置 dialog 位于屏幕底部，并且设置出入动画
     */
    public void setBottomLayout(boolean bottomLayout) {
        if (bottomLayout) {
            Window win = getWindow();
            if (win != null) {
                win.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = win.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                win.setAttributes(lp);
                // dialog 布局位于底部
                win.setGravity(Gravity.BOTTOM);
                // 设置进出场动画
                win.setWindowAnimations(R.style.Animation_Bottom);
            }
        }
    }

    public static interface OnButtonClickListener {
        /**
         * 点击按钮事件
         */
        public void onButtonClick(RateDialog dialog, String buttonTitle);
    }

    public static interface OnCancelListener {

        /**
         * Dialog消失事件
         */
        public void onCancel(RateDialog dialog);
    }

    public static interface OnDismissListener {

        /**
         * Dialog消失事件
         */
        public void onDismiss(RateDialog dialog);
    }

    public static final class Builder {
        private OnButtonClickListener onRateButtonClickListener;
        private OnButtonClickListener onSuggestionButtonClickListener;
        private OnButtonClickListener onExitButtonClickListener;
        private OnButtonClickListener onCancelButtonClickListener;
        private OnCancelListener onCancelListener;
        private OnDismissListener onDismissListener;
        private String message;
        private String rateButtonTitle;
        private String suggestionButtonTitle;
        private String exitButtonTitle;
        private String cancelButtonTitle;
        private int starImageResId;
        private Context context;
        private boolean bottomLayout;
        private boolean cancelable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnRateButtonClickListener(String rateButtonTitle, OnButtonClickListener onRateButtonClickListener) {
            this.rateButtonTitle = rateButtonTitle;
            this.onRateButtonClickListener = onRateButtonClickListener;
            return this;
        }

        public Builder setOnSuggestionButtonClickListener(String suggestionButtonTitle, OnButtonClickListener onSuggestionButtonClickListener) {
            this.suggestionButtonTitle = suggestionButtonTitle;
            this.onSuggestionButtonClickListener = onSuggestionButtonClickListener;
            return this;
        }

        public Builder setOnExitButtonClickListener(String exitButtonTitle, OnButtonClickListener onExitButtonClickListener) {
            this.exitButtonTitle = exitButtonTitle;
            this.onExitButtonClickListener = onExitButtonClickListener;
            return this;
        }

        public Builder setOnCancelButtonClickListener(String cancelButtonTitle, OnCancelListener onCancelListener) {
            this.cancelButtonTitle = cancelButtonTitle;
            this.onCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {

            this.onCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setStarImageResId(int starImageResId) {
            this.starImageResId = starImageResId;
            return this;
        }

        public Builder setBottomLayout(boolean bottomLayout) {
            this.bottomLayout = bottomLayout;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public RateDialog build() {
            final RateDialog dialog = new RateDialog(context);

            dialog.onRateButtonClickListener = onRateButtonClickListener;
            dialog.onSuggestionButtonClickListener = onSuggestionButtonClickListener;
            dialog.onExitButtonClickListener = onExitButtonClickListener;
            dialog.onCancelListener = onCancelListener;
            dialog.onDismissListener = onDismissListener;

            dialog.message = message;
            dialog.rateButtonTitle = rateButtonTitle;
            dialog.suggestionButtonTitle = suggestionButtonTitle;
            dialog.exitButtonTitle = exitButtonTitle;
            dialog.cancelButtonTitle = cancelButtonTitle;
            dialog.starImageResId = starImageResId;
            dialog.bottomLayout = bottomLayout;
            dialog.cancelable = cancelable;

            return dialog;
        }

        public RateDialog show() {
            final RateDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
