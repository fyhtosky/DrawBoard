package com.lafonapps.login.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.lafonapps.login.R;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/10/09
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */
public class TimeCount extends CountDownTimer {
    private static final int TIME_TASCK = 1000;
    private TextView textView;
    private Context context;
    public TimeCount(Context context, long millisInFuture, TextView view) {
        super(millisInFuture, TIME_TASCK);
        textView = view;
        this.context = context;
    }

    @Override
    public void onFinish() {// 计时完毕
        textView.setTextColor(context.getResources().getColor(R.color.register_code));
        textView.setBackgroundResource(R.drawable.register_getcode0);
        textView.setText(context.getString(R.string.register_getcode));
        textView.setClickable(true);
    }

    @Override
    public void onTick(long millisUntilFinished) {// 计时过程
        textView.setTextColor(context.getResources().getColor(R.color.login_text));
        textView.setClickable(false);//防止重复点击
        textView.setBackgroundResource(R.drawable.register_getcode1);
        textView.setText(context.getString(R.string.getcode_again)+"("+millisUntilFinished / TIME_TASCK+")");
    }
}