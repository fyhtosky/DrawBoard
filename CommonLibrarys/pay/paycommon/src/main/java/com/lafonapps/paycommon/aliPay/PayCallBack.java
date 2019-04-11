package com.lafonapps.paycommon.aliPay;

/**
 * Created by wuguangfa on 2018/1/23.
 */
 public interface PayCallBack {
    /**
     *  购买成功或者恢复成功
     * @param restore true 恢复成功, false 购买成功
     */
    void paySuccess();

    /**
     *  购买失败
     * @param errorCode 失败码
     */
    void payFailure(String errorCode);
}
