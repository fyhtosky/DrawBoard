package com.lafonapps.paycommon.net;

import com.lafonapps.paycommon.bean.PayAliOrderBean;
import com.lafonapps.paycommon.bean.PayOrderBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/09/18
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public interface Api {

    //微信支付请求
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST ("appPay/api/weixin/order/sign")
    Observable<PayOrderBean> getPayOrder(@Body RequestBody info);   // 请求体RequestBody 类型

    //支付宝支付请求
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST ("appPay/api/alipay/order/sign")
    Observable<PayAliOrderBean> getPayAliOrder(@Body RequestBody info);   // 请求体RequestBody 类型
}
