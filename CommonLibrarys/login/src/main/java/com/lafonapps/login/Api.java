package com.lafonapps.login;



import com.lafonapps.login.bean.FindPswBean;
import com.lafonapps.login.bean.GetCodeBean;
import com.lafonapps.login.bean.LoginBean;
import com.lafonapps.login.bean.PayOrderBean;
import com.lafonapps.login.bean.RegisterBean;
import com.lafonapps.login.bean.SignCodeBean;

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

    //用户登录
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST ("appPay/api/user/info/login")
    Observable<LoginBean> toLogin(@Body RequestBody info);   // 请求体RequestBody 类型

    //用户注册
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST ("appPay/api/user/info/registered")
    Observable<RegisterBean> toRegist(@Body RequestBody info);   // 请求体RequestBody 类型

    //用户找回密码
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST ("appPay/api/user/info/resetPassword")
    Observable<FindPswBean> toFindPaw(@Body RequestBody info);   // 请求体RequestBody 类型

    //获取taken
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST ("appPay/api/user/info/tokenLogin")
    Observable<LoginBean> toGetTaken(@Body RequestBody info);   // 请求体RequestBody 类型

    //获取短信验证码
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST ("appPay/api/app/sms/getSecurityCode")
    Observable<GetCodeBean> toGetCode(@Body RequestBody info);   // 请求体RequestBody 类型

    //获取短信验证码
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST ("appPay/api/app/sms/confirmSecurityCode")
    Observable<SignCodeBean> toSignCode(@Body RequestBody info);   // 请求体RequestBody 类型
}
