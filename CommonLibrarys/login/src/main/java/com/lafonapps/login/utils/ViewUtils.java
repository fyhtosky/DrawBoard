package com.lafonapps.login.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lafonapps.httputil.BaseUtil;
import com.lafonapps.httputil.HttpManager;
import com.lafonapps.httputil.RxHelper;
import com.lafonapps.login.Api;
import com.lafonapps.login.R;
import com.lafonapps.login.activity.LoginActivity;
import com.lafonapps.login.bean.LoginBean;
import com.lafonapps.paycommon.PayCommonConfig;
import com.lafonapps.paycommon.payUtils.LocalDataUtil.LocalDataUtil;


import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/10/10
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class ViewUtils {


    public static void showPsw(Activity activity, EditText editText, ImageView imageView, boolean isShow) {
        if (isShow) {// 显示密码
            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.password_visible));
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setSelection(editText.getText().toString().length());
        } else {// 隐藏密码
            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.password_invisible));
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setSelection(editText.getText().toString().length());
        }
    }

    public static void showDelete(EditText editText, final ImageView imageView) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
        });
    }

    public static boolean isCanUseVip(Context context){
        if (SPUtils.getInstance("user_info").getBoolean("isR",false)||PayCommonConfig.sharedCommonConfig.getProductIsValid(context)) {
            return true;
        } else {
            return false;
        }
    }

    public static void initNotice(final Activity context) {
        if (SPUtils.getInstance("user_info").getBoolean("isLogin",false)) {
            //新用户  获取taken  验证vip
            toGetTakenImpl();
        } else {
            if (PayCommonConfig.sharedCommonConfig.getProductIsValid(context)) {
                //弹出对话框
                LocalDataUtil.sharedLocalUtil.showDialog(context,context.getString(R.string.dialog6_title),context.getString(R.string.dialog6),context.getString(R.string.login_go0), PayCommonConfig.targetClass);
            }

        }
    }

    //获取taken
    public static void toGetTakenImpl() {
        String noncestr = BaseUtil.genNonceStr();
        String timestamp = String.valueOf(BaseUtil.genTimeStamp());
        String taken = SPUtils.getInstance("user_info").getString("accessToken","");
        String packageName = AppUtils.getAppPackageName();

        SortedMap<Object,Object> signParams = new TreeMap<Object,Object>();
        signParams.put("noncestr", noncestr);
        signParams.put("timestamp", timestamp);
        String sign = BaseUtil.createSign(PayCommonConfig.KEY,signParams);

        Map<String,String> mapParam = new HashMap<>();
        mapParam.put("accessToken",taken);
        mapParam.put("packageName",packageName);
        mapParam.put("noncestr",noncestr);
        mapParam.put("timestamp",timestamp);
        mapParam.put("sign",sign);
        HttpManager
                .getInstance()
                .getApiService(Api.class, BaseUtil.baseUrl)
                .toGetTaken(RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), BaseUtil.mapToJson(mapParam)))
                .compose(RxHelper.<LoginBean>rxSchedulerHelper())
                .subscribe(new Observer<LoginBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i("http","onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull final LoginBean loginBean) {
                        Log.i("http","onNext");
                        if (loginBean.isSucc()) {
                            //保存登录信息
                            SPUtils.getInstance("user_info").put("timeExpire",loginBean.getData().getTimeExpire());
                            SPUtils.getInstance("user_info").put("isR",loginBean.getData().isIsR());
                            SPUtils.getInstance("user_info").put("accessToken",loginBean.getData().getAccessToken());
                            SPUtils.getInstance("user_info").put("userId",loginBean.getData().getUserId());
                            SPUtils.getInstance("user_info").put("isLogin",true);
                        } else {
                            SPUtils.getInstance("user_info").put("timeExpire","");
                            SPUtils.getInstance("user_info").put("isR",false);
                            SPUtils.getInstance("user_info").put("accessToken","");
                            SPUtils.getInstance("user_info").put("userId","");
                            SPUtils.getInstance("user_info").put("isLogin",false);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("http","onError:"+e);
                        //
                        String date1 = SPUtils.getInstance("user_info").getString("timeExpire");
                        String date2 = LocalDataUtil.sharedLocalUtil.getNetTime();
                        if (!LocalDataUtil.sharedLocalUtil.timeCompare(date1,date2)) {
                            SPUtils.getInstance("user_info").put("timeExpire","");
                            SPUtils.getInstance("user_info").put("isR",false);
                            SPUtils.getInstance("user_info").put("accessToken","");
                            SPUtils.getInstance("user_info").put("userId","");
                            SPUtils.getInstance("user_info").put("isLogin",false);
                        }

                    }

                    @Override
                    public void onComplete() {
                        Log.i("http","onComplete");
                    }
                });
    }

}
