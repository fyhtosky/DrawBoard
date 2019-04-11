package com.lafonapps.paycommon.aliPay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lafonapps.httputil.BaseUtil;
import com.lafonapps.httputil.HttpManager;
import com.lafonapps.httputil.RxHelper;
import com.lafonapps.paycommon.PayCommonConfig;
import com.lafonapps.paycommon.bean.PayAliOrderBean;
import com.lafonapps.paycommon.net.Api;
import com.lafonapps.paycommon.payUtils.LocalDataUtil.LocalDataUtil;
import com.lafonapps.paycommon.payUtils.alipayUtils.OrderInfoUtil2_0;
import com.lafonapps.paycommon.payUtils.alipayUtils.PayResult;
import com.lafonapps.paycommon.payUtils.alipayUtils.SignUtils;
import com.lafonapps.paycommon.weChat.Constant;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

/**
 * Created by shaozhike on 2018/2/26.
 */
public class AliPayManager {
    public static final AliPayManager sharedPayManager = new AliPayManager();
    public PayCallBack payCallBack;
    public Activity mActivity;

    private ProgressDialog pDialog = null;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知
                        if (!SPUtils.getInstance("user_info").getBoolean("isLogin",false)) {
                            LocalDataUtil.sharedLocalUtil.vipUser(mActivity);
                        }
                        if (payCallBack != null) {
                            payCallBack.paySuccess();
                        }
                    } else {
                         //该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (payCallBack != null) {
                            payCallBack.payFailure(payResult.getResultStatus());
                        }
                    }

                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 支付宝支付业务
     *
     * @param
     */
    public void payNotLogin(String subject,double price,String type) {
        /**
         *
         * orderInfo的获取必须来自服务端；
         * 如果出现4000错误,就是订单构建错误,这边是2018-6-01的sdk,提交服务器的构建订单字符串连接出现更新,一切以
         * 官方文档里面的构建语法来构建,小心踩坑
         */
        boolean rsa2 = (PayCommonConfig.RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(PayCommonConfig.APPID, rsa2,subject,price+"",LocalDataUtil.sharedLocalUtil.getBuySign(type));
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = PayCommonConfig.RSA2_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Log.i("http", orderInfo);

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mActivity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
        }

    /**
     * 支付宝支付业务
     *
     * @param
     */
    public void pay(String subtype, final Activity activity, PayCallBack payCallBack) {

        this.payCallBack = payCallBack;
        this.mActivity = activity;
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         * 间一  二 三 四 花漾 心形 翅膀 符号 图案 同名 情侣 临摹 推荐 空空
         */
        String subject ="";
        double price = 2.99;
        String vipType = "1";
        String type = "";
        switch (subtype) {
            case PayCommonConfig.ONEMONTH:
                subject = LocalDataUtil.sharedLocalUtil.retrunDescribe(" -(一个月)") ;
                price = PayCommonConfig.sharedCommonConfig.ONEMONTH_PRICE;
                PayCommonConfig.subPayType = PayCommonConfig.ONEMONTH;
                vipType = "1";
                type = "OM";
                break;
            case PayCommonConfig.THREEMONTH:
                subject = LocalDataUtil.sharedLocalUtil.retrunDescribe(" -(三个月)");
                price = PayCommonConfig.sharedCommonConfig.THREEMONTH_PRICE;
                PayCommonConfig.subPayType = PayCommonConfig.THREEMONTH;
                vipType = "3";
                type = "TM";
                break;
            case PayCommonConfig.ONEYEAR:
                subject = LocalDataUtil.sharedLocalUtil.retrunDescribe(" -(一年)");
                price = PayCommonConfig.sharedCommonConfig.ONEYEAR_PRICE;
                PayCommonConfig.subPayType = PayCommonConfig.ONEYEAR;
                vipType = "12";
                type = "OY";
                break;
            case PayCommonConfig.LIFETIME:
                subject = LocalDataUtil.sharedLocalUtil.retrunDescribe(" -(永久)");
                price = PayCommonConfig.sharedCommonConfig.LIFETIME_PRICE;
                PayCommonConfig.subPayType = PayCommonConfig.LIFETIME;
                vipType = "13";
                type = "YY";
                break;
        }
        //1.老用户vip提示登录  2.老用户飞儿vip购买
        if (SPUtils.getInstance("user_info").getBoolean("isLogin",false)) {
            String userId = SPUtils.getInstance("user_info").getString("userId");
            String packageName = AppUtils.getAppPackageName();
            Map<String, String> mapParam = new HashMap<>();
            mapParam.put("app_id", PayCommonConfig.APPID);
            mapParam.put("subject", subject);
            mapParam.put("total_amount", String.valueOf(price));
            mapParam.put("vipType", vipType);
            mapParam.put("userId", userId);
            mapParam.put("packageName", packageName);
            HttpManager
                    .getInstance()
                    .getApiService(Api.class, Constant.baseUrl)
                    .getPayAliOrder(RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), BaseUtil.mapToJson(mapParam)))
                    .compose(RxHelper.<PayAliOrderBean>rxSchedulerHelper())
                    .subscribe(new Observer<PayAliOrderBean>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            Log.i("http", "onSubscribe");
                            //加载进度
                            pDialog = ProgressDialog.show(activity, "提示", "支付请求中，请稍后...", false);
                        }

                        @Override
                        public void onNext(@NonNull final PayAliOrderBean payAliOrderBean) {
                            Log.i("http", "onNext");
                            if (pDialog != null) {
                                pDialog.dismiss();
                            }
                            if (payAliOrderBean.isSucc()) {
                                Runnable payRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        PayTask alipay = new PayTask(mActivity);
                                        Map<String, String> result = alipay.payV2(payAliOrderBean.getData(), true);
                                        Log.i("msp", result.toString());
                                        Message msg = new Message();
                                        msg.what = 1;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };

                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                            } else {
                                Toast.makeText(activity, "支付宝支付请求失败！", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.i("http", "onError:" + e);
                            if (pDialog != null) {
                                pDialog.dismiss();
                            }
                            Toast.makeText(activity, "网络状态不佳，支付失败，请稍候再试！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                            Log.i("http", "onComplete");
                        }
                    });
        } else {
            //判断是否需要登录才能购买
            if (PayCommonConfig.isAliPayShouldToLogin) {
                ToastUtils.showShort("您还未注册登录...");
                ActivityUtils.startActivity(activity, PayCommonConfig.targetClass);
            } else {
                payNotLogin(subject,price,type);
            }
        }
    }

}
