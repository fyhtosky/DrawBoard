package com.lafonapps.httputil;


import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * author  : LSM
 * time    : 2018/03/06
 * function:
 * e-mail  : lsmloveu@126.com
 * github  : https://github.com/lsmloveu
 * csdn    : http://blog.csdn.net/csdn_android_lsm
 * 简书    : http://www.jianshu.com/u/644036b17b6f
 */

public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        //这个chain里面包含了request和response，所以你要什么都可以从这里拿
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")  //application/x-www-form-urlencoded
                .addHeader("X-Requested-With","XMLHttpRequest")
                .build();
        long t1 = System.nanoTime();//请求发起的时间
        Log.i("http",String.format("发送请求 %s",request.url()));
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();//收到响应的时间
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        Log.i("http",String.format("接收响应: [%s] %n返回json:【%s】 %n请求耗时:%.1fms%n%s",
                response.request().url(),
                responseBody.string(),
                (t2 - t1) / 1e6d,
                response.headers()));
        return response;
    }


}
