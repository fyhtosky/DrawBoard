package com.lafonapps.httputil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author  : LSM
 * time    : 2017/12/04
 * function:
 * e-mail  : lsmloveu@126.com
 * github  : https://github.com/lsmloveu
 * csdn    : http://blog.csdn.net/csdn_android_lsm
 */

public class HttpManager {
    private static HttpManager httpManager;
    private static final int TIMEOUT_READ = 10;
    private static final int TIMEOUT_CONNECTION = 10;
    private static final LoggingInterceptor interceptor = new LoggingInterceptor();
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            //打印日志
            .addInterceptor(interceptor)
            //time out
            .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();
    private HttpManager(){

    }
    public static HttpManager getInstance(){
        if (httpManager==null) {
            httpManager=new HttpManager();
        }
        return httpManager;
    }

    public <T> T getApiService(Class<T> clazz, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }


}
