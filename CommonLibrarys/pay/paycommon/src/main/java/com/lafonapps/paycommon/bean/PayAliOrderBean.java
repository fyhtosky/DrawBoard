package com.lafonapps.paycommon.bean;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/10/11
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class PayAliOrderBean {

    /**
     * succ : true
     * statusCode : 200
     * msg : 请求成功
     * data : app_id=2018032602449829&biz_content={"out_trade_no":"201810111110041801544788","total_amount":"0.01","subject":"手机防盗报警器 - VIP增值服务 -(一个月) - B","timeout_express":"30m","product_code":"QUICK_MSECURITY_PAY"}&charset=utf-8&method=alipay.trade.app.pay&notify_url=http://47.99.115.53/appPay/api/alipay/order/callback&sign=Y29LK2tSVldxcEdmMldwcTFHMVV3UjV4Z05uckI1eE50V0dxRGxIRmFvcC9NYmw0VUZRYzNQWmJGNjlvak1hcGh0SjErYU1VaHJWaHQ5Rjk4N3lNbnh4eG5UU3RLK0pEVTR2bjI1SEsxU3RqQWp0c3hPSEcvN2NLT1l5bWRNTU95ZEpoMUR4R05KNENqS3ZFYW5qc2Z1N01jSTQzUE9DUnpBMXhXQ3VFVk55am9ialJKYWpmNytSMGltNCtPS1lHOUFtN0M2c0l2YitxaG9pZytWbnc0RGcyRXdtQXdIZGRMSzg4S2I1U1pFcE9zR3FJUExuczJxVi9Pd1FtVVdLYlk5QkFuYUF1MHRtSTlUM1Y0M1hTL0haakdNRmdOazdGcTlTc3hqdHhGN0JKQ3dzd043MXkyWHJtTHNxc3NwR1pmUkR3RFdQN0FFeDZON1JYODZHa1lnPT0=&sign_type=RSA2&timestamp=2018-10-11 11:10:04&version=1.0
     * time : 1539227404164
     */

    private boolean succ;
    private int statusCode;
    private String msg;
    private String data;
    private long time;

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
