package com.lafonapps.paycommon;

import android.app.Activity;
import android.content.Context;

import com.lafonapps.paycommon.aliPay.AliPayManager;
import com.lafonapps.paycommon.aliPay.PayCallBack;
import com.lafonapps.paycommon.payUtils.LocalDataUtil.LocalDataUtil;
import com.lafonapps.paycommon.payWindow.PayDialog;
import com.lafonapps.paycommon.weChat.PayWeChat;

/**
 * Created by shaozhike on 2018/2/26.
 */

public class PayCommonConfig {
    public static final PayCommonConfig sharedCommonConfig = new PayCommonConfig();
    /**
     * 支付宝支付配置
     */
    public static String APPID = "";
    public static String RSA2_PRIVATE = "";
    /**
     * 微信支付业务配置
     */
    public static String wxAppId = "";
    /**
     * 应用名
     */
    public String orderInformation;
    /**
     * 平台名
     */
    public String flavorName;

    /**
     * 订阅一个月的价格
     */
    public static Double ONEMONTH_PRICE = 6.99;
    /**
     * 订阅三个月的价格
     */
    public static Double THREEMONTH_PRICE = 16.99;
    /**
     * 订阅一年的价格
     */
    public static Double ONEYEAR_PRICE = 60.99;
    /**
     * 一次性内购的价格
     */
    public static double LIFETIME_PRICE = 0;

    public static Class targetClass;

    //接口请求签名秘钥

    public static String KEY = "";


    /**
     * 一个月的标识符
     */
    public static final String ONEMONTH = "oneMonth";
    /**
     * 三个月的标识符
     */
    public static final String THREEMONTH = "threeMonth";
    /**
     * 一年的标识符
     */
    public static final String ONEYEAR = "oneYear";
    /**
     * 永久的标识符
     */
    public static final String LIFETIME = "lifeTime";
    /**
     * 判断订阅类型的标识符
     */
    public static String subPayType = ONEMONTH;

    /**
     * 支付宝支付标识符
     */
    public static final int ALIPAY = 0;

    /**
     * 微信支付标识符
     */
    public static final int WEPAY = 1;
    //判断支付宝购买是否需要去注册登录  true 必须先登录注册才能购买  false 不注册登录可以直接购买
    public static boolean isAliPayShouldToLogin = false;



    /**
     * 支付宝购买方法
     */
    public void alipay(String subType, Activity activity, PayCallBack payCallBack) {
        AliPayManager.sharedPayManager.pay(subType, activity, payCallBack);

    }
    /**
     * 微信支付方法
     *
     * 回调方法在自己的activity里面利用eventbus通知实现,具体的实现看demo
     */
    public void weChatPay(String subPayType,Context context){
        PayWeChat.sharedPayWeChat.wxPay(subPayType,context);
    }

    /**
     * 购买是否订阅
     *
     * @return true 有效, false 无效, 需要购买
     */
    public boolean getProductIsValid(Context context) {
        // 购买之前查询是否之前可能购买过
        return LocalDataUtil.sharedLocalUtil.getIsVip(context);
    }



    /**
     * 获取当前本地存的订阅时间
     *
     */
    public String getDeadTime(Context context) {

        return LocalDataUtil.sharedLocalUtil.getDeadTime(context);
    }


    /**
     * 微信购买成功,因为微信回调写不了library里面,所以需要购买成功后对本地存的vip状态和订阅时间进行修改
     *  支付宝购买则不用调用,库里面都写好了,具体看demo毁掉成功调用的方法
     */
    public void localVip(Context context){
        LocalDataUtil.sharedLocalUtil.vipUser(context);
    }
    /**
     * 在需要的时候dissmiss这个弹窗
     */
    public void dissmissDialog(){
        PayDialog.sharedPayDialog.dismissDialog();
    }



    /**
     * 支付宝订阅失败错误码,有需要的来对照
     *
     */
    //switch (payResult.getResultStatus()) {
//        case "9000":
//        GMToastUtil.showToast("支付成功");
//        break;
//        case "8000":
//        GMToastUtil.showToast("正在处理中");
//        break;
//        case "4000":这里一般是sdk更新了,里面构建订单的参数连接有问题
//        GMToastUtil.showToast("订单支付失败");
//        break;
//        case "5000":
//        GMToastUtil.showToast("重复请求");
//        break;
//        case "6001":
//        GMToastUtil.showToast("已取消支付");
//        break;
//        case "6002":
//        GMToastUtil.showToast("网络连接出错");
//        break;
//        case "6004":
//        GMToastUtil.showToast("正在处理中");
//        break;
//default:
//        GMToastUtil.showToast("支付失败");
//        break;

}
