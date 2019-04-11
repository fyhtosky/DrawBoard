package com.lafonapps.paycommon.weChat;

/**
 * Created by leishangming on 2018/7/19.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lafonapps.httputil.BaseUtil;
import com.lafonapps.httputil.HttpManager;
import com.lafonapps.httputil.RxHelper;
import com.lafonapps.paycommon.PayCommonConfig;
import com.lafonapps.paycommon.bean.PayOrderBean;
import com.lafonapps.paycommon.net.Api;
import com.lafonapps.paycommon.payUtils.LocalDataUtil.LocalDataUtil;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
public class PayWeChat {

    public static final PayWeChat sharedPayWeChat= new PayWeChat();
    private IWXAPI iwxapi; //微信支付api
    private ProgressDialog pDialog = null;
    private static class MyHandler extends Handler {

        private WeakReference<Context> mContext;
        private String result = "";
        public MyHandler(Context context){
            mContext = new WeakReference<>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:
                    result = "请求超时！";
                    break;
                case -2:
                    result = "请求失败";
                    break;
            }
            Toast.makeText(mContext.get(), result, Toast.LENGTH_SHORT).show();
        }
    }

    //判断是否支持微信支付
    private boolean isSupportWxPay() {
        return iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
    }

    public  void  wxPay(String subType, final Context context) {
        //判断是否登录
        if (SPUtils.getInstance("user_info").getBoolean("isLogin",false)) {
            String userId = SPUtils.getInstance("user_info").getString("userId");
            String packageName = AppUtils.getAppPackageName();
            //根据类型判断
            String body = "";
            double totalMoney = 2.99;
            String vipType = "1";
            switch (subType) {
                case PayCommonConfig.ONEMONTH:
                    body = LocalDataUtil.sharedLocalUtil.retrunDescribe(" -(一个月)");
                    totalMoney = PayCommonConfig.ONEMONTH_PRICE;
                    PayCommonConfig.subPayType = PayCommonConfig.ONEMONTH;
                    vipType = "1";
                    break;
                case PayCommonConfig.THREEMONTH:
                    body = LocalDataUtil.sharedLocalUtil.retrunDescribe(" -(三个月)");
                    totalMoney = PayCommonConfig.THREEMONTH_PRICE;
                    PayCommonConfig.subPayType = PayCommonConfig.THREEMONTH;
                    vipType = "3";
                    break;
                case PayCommonConfig.ONEYEAR:
                    body = LocalDataUtil.sharedLocalUtil.retrunDescribe(" -(一年)");
                    totalMoney = PayCommonConfig.ONEYEAR_PRICE;
                    PayCommonConfig.subPayType = PayCommonConfig.ONEYEAR;
                    vipType = "12";
                    break;
                case PayCommonConfig.LIFETIME:
                    body = LocalDataUtil.sharedLocalUtil.retrunDescribe(" -(永久)");
                    totalMoney = PayCommonConfig.LIFETIME_PRICE;
                    PayCommonConfig.subPayType = PayCommonConfig.LIFETIME;
                    vipType = "13";
                    break;
            }
            //保存增值服务时间
            SPUtils.getInstance("user_info").put("grade", subType);
            String ip = BaseUtil.getLocalHostIP();
            //调起微信支付
            iwxapi = WXAPIFactory.createWXAPI(context, null); //初始化微信api
            iwxapi.registerApp(PayCommonConfig.wxAppId); //注册appid  appid可以在开发平台获取
            if (!isSupportWxPay()) {
                Toast.makeText(context, "您还没有微信客户端,无法进行支付!", Toast.LENGTH_SHORT).show();
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                return;
            }
            Map<String, String> mapParam = new HashMap<>();
            mapParam.put("appid", PayCommonConfig.wxAppId);
            mapParam.put("body", body);
            mapParam.put("spbill_create_ip", ip);
            mapParam.put("total_fee", String.valueOf(totalMoney));
            mapParam.put("vipType", vipType);
            mapParam.put("userId", userId);
            mapParam.put("packageName", packageName);
            HttpManager
                    .getInstance()
                    .getApiService(Api.class, BaseUtil.baseUrl)
                    .getPayOrder(RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), BaseUtil.mapToJson(mapParam)))
                    .compose(RxHelper.<PayOrderBean>rxSchedulerHelper())
                    .subscribe(new Observer<PayOrderBean>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            Log.i("http", "onSubscribe");
                            //加载进度
                            pDialog = ProgressDialog.show(context, "提示", "支付请求中，请稍后...", false);
                        }

                        @Override
                        public void onNext(@NonNull final PayOrderBean payOrderBean) {
                            Log.i("http", "onNext");
                            if (pDialog != null) {
                                pDialog.dismiss();
                            }
                            if (payOrderBean.isSucc()) {
                                Runnable payRunnable = new Runnable() {  //这里注意要放在子线程
                                    @Override
                                    public void run() {
                                        PayReq request = new PayReq(); //调起微信APP的对象
                                        //下面是设置必要的参数，也就是前面说的参数,这几个参数从何而来请看上面说明
                                        PayOrderBean.DataBean dataBean = payOrderBean.getData();
                                        request.appId = dataBean.getAppid();
                                        request.partnerId = dataBean.getPartnerid();
                                        request.prepayId = dataBean.getPrepayid();
                                        request.packageValue = dataBean.getPackageX();
                                        request.nonceStr = dataBean.getNoncestr();
                                        request.timeStamp = dataBean.getTimestamp();
                                        request.sign = dataBean.getSign();
                                        iwxapi.sendReq(request);//发送调起微信的请求
                                    }
                                };
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                            } else {
                                Toast.makeText(context, "微信支付请求失败!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.i("http", "onError:" + e);
                            if (pDialog != null) {
                                pDialog.dismiss();
                            }
                            Toast.makeText(context, "网络状态不佳，支付失败，请稍候再试！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                            Log.i("http", "onComplete");
                        }
                    });
        } else {
            //
            ToastUtils.showShort("您还未注册登录...");
            ActivityUtils.startActivity((Activity) context, PayCommonConfig.targetClass);
        }
    }
}
