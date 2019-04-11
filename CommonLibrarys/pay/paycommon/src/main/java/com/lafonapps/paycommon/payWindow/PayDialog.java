package com.lafonapps.paycommon.payWindow;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lafonapps.paycommon.PayCommonConfig;
import com.lafonapps.paycommon.R;
import com.lafonapps.paycommon.aliPay.PayCallBack;
import com.lafonapps.paycommon.payUtils.wechatUtils.SPUtils;
import com.lafonapps.paycommon.payUtils.wechatUtils.ScreenSizeUtil;

import me.shaohui.bottomdialog.BottomDialog;

public class PayDialog {
    BottomDialog bottomDialog;
    TextView tvTitle,tvPaymoney,tvAli,tvSuggest,tvWx,tvSure;
    ImageView imClose,imAli,imAlichoice,imWx,imWxchoice;
    RelativeLayout rlAli,rlWx;
    //支付选择方式标示
    private int sign =1;
    public static final PayDialog sharedPayDialog = new PayDialog();
    Activity activity;
    PayCallBack payCallBack;
    String subType;
    public  void showPayDialog(Activity activity, FragmentManager fragmentManager,String subType,PayCallBack callBack) {
        this.activity =activity;
        sign = PayCommonConfig.ALIPAY;
        this.payCallBack =callBack;
        this.subType = subType;
        bottomDialog = BottomDialog.create(fragmentManager);
        bottomDialog.setViewListener(new BottomDialog.ViewListener() {      // 可以进行一些必要对View的操作
            @Override
            public void bindView(View v) {
                // you can do bind view operation
                initView(v);
            }
        })
                .setLayoutRes(R.layout.pay)
                .setDimAmount(0.5f)            // Dialog window 背景色深度 范围：0 到 1，默认是0.2f
                .setCancelOutside(true)     // 点击外部区域是否关闭，默认true
                .setTag("BottomDialog")     // 设置DialogFragment的tag
                .setHeight(ScreenSizeUtil.getScreenSize(activity)[1] * 2 / 5)
                .show();
    }

    public void dismissDialog(){
        bottomDialog.dismissAllowingStateLoss();
    }

    public void initView(View view) {
        //支付方式
        tvTitle = (TextView)view.findViewById(R.id.tv_title);
        //支付金额
        tvPaymoney = (TextView)view.findViewById(R.id.tv_paymoney);
        //支付宝
        tvAli = (TextView)view.findViewById(R.id.tv_ali);
        imAli = (ImageView)view.findViewById(R.id.im_ali);
        imAlichoice = (ImageView)view.findViewById(R.id.im_alichoice);
        rlAli = (RelativeLayout)view.findViewById(R.id.rl_ali);
        //推荐
        tvSuggest = (TextView)view.findViewById(R.id.tv_suggest);
        //微信
        tvWx = (TextView)view.findViewById(R.id.tv_wx);
        imWx = (ImageView)view.findViewById(R.id.im_wx);
        imWxchoice = (ImageView)view.findViewById(R.id.im_wxchoice);
        rlWx = (RelativeLayout)view.findViewById(R.id.rl_wx);
        //立即支付
        tvSure = (TextView)view.findViewById(R.id.tv_sure);
        //关闭弹窗
        imClose = (ImageView)view.findViewById(R.id.im_close);
        //关闭
        imClose.setOnClickListener(new onClickListener());
        //选择支付宝支付
        rlAli.setOnClickListener(new onClickListener());
        //选择微信支付
        rlWx.setOnClickListener(new onClickListener());
        //立即支付
        tvSure.setOnClickListener(new onClickListener());

        switch (PayCommonConfig.subPayType)
        {
            case PayCommonConfig.ONEMONTH:

                tvPaymoney.setText("支付金额:"+PayCommonConfig.ONEMONTH_PRICE);
                break;

            case PayCommonConfig.THREEMONTH:
                tvPaymoney.setText("支付金额:"+PayCommonConfig.THREEMONTH_PRICE);

                break;
            case PayCommonConfig.ONEYEAR:
                tvPaymoney.setText("支付金额:"+PayCommonConfig.ONEYEAR_PRICE);

                break;
            case PayCommonConfig.LIFETIME:
                tvPaymoney.setText("支付金额:"+PayCommonConfig.LIFETIME_PRICE);

                break;
        }

    }


    class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.im_close) {
                bottomDialog.dismiss();

            } else if (i == R.id.rl_ali) {
                imAlichoice.setImageResource(R.mipmap.pay_icon_chose);
                imWxchoice.setImageResource(R.mipmap.pay_icon_no_chose);
                sign = PayCommonConfig.ALIPAY;
            } else if (i == R.id.rl_wx) {
                imAlichoice.setImageResource(R.mipmap.pay_icon_no_chose);
                imWxchoice.setImageResource(R.mipmap.pay_icon_chose);
                sign = PayCommonConfig.WEPAY;

            } else if (i == R.id.tv_sure) {//支付

                if(isNetworkConnected(activity)){
                    //判断是否是永久VIP
                    boolean isLifeTime = (boolean) SPUtils.get(activity,"isLifetime",false);
                    if (isLifeTime){
                        Toast.makeText(activity, "您已经是永久VIP用户,无需重复购买", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sign == PayCommonConfig.ALIPAY) {
                        PayCommonConfig.sharedCommonConfig.alipay(subType,activity,payCallBack);
                    } else {
                        PayCommonConfig.sharedCommonConfig.weChatPay(subType,activity);//该参数设置vip类型
                    }
                }else {
                    Toast.makeText(activity,"检测到不可用的网络,请连接网络重试",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
