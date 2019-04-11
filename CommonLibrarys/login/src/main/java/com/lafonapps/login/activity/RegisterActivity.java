package com.lafonapps.login.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lafonapps.httputil.BaseUtil;
import com.lafonapps.httputil.HttpManager;
import com.lafonapps.httputil.RxHelper;
import com.lafonapps.login.Api;
import com.lafonapps.login.R;
import com.lafonapps.login.bean.GetCodeBean;
import com.lafonapps.login.bean.LoginBean;
import com.lafonapps.login.bean.RegisterBean;
import com.lafonapps.login.bean.SignCodeBean;
import com.lafonapps.login.utils.TimeCount;
import com.lafonapps.login.utils.ViewUtils;
import com.lafonapps.paycommon.PayCommonConfig;
import com.lafonapps.paycommon.payUtils.LocalDataUtil.LocalDataUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class RegisterActivity extends AppCompatActivity {

    ImageView ivLoginBack;
    EditText etUser;
    ImageView ivClose;
    EditText etPsw;
    ImageView ivEye;
    EditText etCode;
    TextView tvGetcode;
    TextView tvRegister;
    TextView tvBacklogin;

    private TimeCount timeCount;

    private boolean showPassword = true;
    private ProgressDialog pDialog1, pDialog2, pDialog3,pDialog4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initData();
    }

    private void initView() {
        ivLoginBack = findViewById(R.id.iv_login_back);
        etUser = findViewById(R.id.et_user);
        ivClose = findViewById(R.id.iv_close);
        etPsw = findViewById(R.id.et_psw);
        ivEye = findViewById(R.id.iv_eye);
        etCode = findViewById(R.id.et_code);
        tvGetcode = findViewById(R.id.tv_getcode);
        tvRegister = findViewById(R.id.tv_register);
        tvBacklogin = findViewById(R.id.tv_backlogin);

        ivLoginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUser.setText("");
            }
        });

        ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPswShow();
            }
        });

        tvGetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGetCodeImpl();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSignCodeImpl();
            }
        });

        tvBacklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(RegisterActivity.this, LoginActivity.class);
                finish();
            }
        });
    }

    private void initData() {
        timeCount = new TimeCount(this,60*1000,tvGetcode);
        //设置监听
        ViewUtils.showDelete(etUser, ivClose);
        ViewUtils.showDelete(etPsw, ivEye);
    }

    //密码显示隐藏控制
    private void toPswShow() {
        if (showPassword) {// 显示密码
            ViewUtils.showPsw(this, etPsw, ivEye, showPassword);
            showPassword = !showPassword;
        } else {
            ViewUtils.showPsw(this, etPsw, ivEye, showPassword);
            showPassword = !showPassword;
        }

    }

    public void toGetCodeImpl() {
        final String mobile = etUser.getText().toString().trim();
        String packageName = AppUtils.getAppPackageName();
        if (TextUtils.isEmpty(mobile) || !RegexUtils.isMobileSimple(mobile)) {
            ToastUtils.showShort(getResources().getString(R.string.toast1));
            return;
        }
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(getResources().getString(R.string.toast6));
            return;
        }
        String noncestr = BaseUtil.genNonceStr();
        String timestamp = String.valueOf(BaseUtil.genTimeStamp());

        SortedMap<Object,Object> signParams = new TreeMap<Object,Object>();
        signParams.put("noncestr", noncestr);
        signParams.put("timestamp", timestamp);
        String sign = BaseUtil.createSign(PayCommonConfig.KEY,signParams);

        Map<String,String> mapParam = new HashMap<>();
        mapParam.put("mobile",mobile);
        mapParam.put("packageName",packageName);
        mapParam.put("noncestr",noncestr);
        mapParam.put("timestamp",timestamp);
        mapParam.put("sign",sign);
        HttpManager
                .getInstance()
                .getApiService(Api.class, BaseUtil.baseUrl)
                .toGetCode(RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), BaseUtil.mapToJson(mapParam)))
                .compose(RxHelper.<GetCodeBean>rxSchedulerHelper())
                .subscribe(new Observer<GetCodeBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i("http","onSubscribe");
                        //加载进度
                        pDialog1 = ProgressDialog.show(RegisterActivity.this,getString(R.string.hint), getString(R.string.dialog2),false);
                    }

                    @Override
                    public void onNext(@NonNull final GetCodeBean getCodeBean) {
                        Log.i("http","onNext");
                        if (pDialog1 != null) {
                            pDialog1 .dismiss();
                        }
                        timeCount.start();
                        ToastUtils.showShort(getCodeBean.getMsg());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("http","onError:"+e);
                        if (pDialog1 != null) {
                            pDialog1 .dismiss();
                        }
                        ToastUtils.showShort(getString(R.string.toast9));
                    }

                    @Override
                    public void onComplete() {
                        Log.i("http","onComplete");
                    }
                });
    }

    public void toSignCodeImpl() {
        final String mobile = etUser.getText().toString().trim();
        final String password = etPsw.getText().toString().trim();
        final String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(mobile) || !RegexUtils.isMobileSimple(mobile)) {
            ToastUtils.showShort(getResources().getString(R.string.toast1));
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            ToastUtils.showShort(getResources().getString(R.string.toast2));
            return;
        }
        if (TextUtils.isEmpty(code) || code.length() != 6) {
            ToastUtils.showShort(getResources().getString(R.string.toast3));
            return;
        }
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(getResources().getString(R.string.toast6));
            return;
        }
        String noncestr = BaseUtil.genNonceStr();
        String timestamp = String.valueOf(BaseUtil.genTimeStamp());
        String packageName = AppUtils.getAppPackageName();

        SortedMap<Object,Object> signParams = new TreeMap<Object,Object>();
        signParams.put("noncestr", noncestr);
        signParams.put("timestamp", timestamp);
        String sign = BaseUtil.createSign(PayCommonConfig.KEY,signParams);

        Map<String,String> mapParam = new HashMap<>();
        mapParam.put("mobile",mobile);
        mapParam.put("securityCode",code);
        mapParam.put("noncestr",noncestr);
        mapParam.put("timestamp",timestamp);
        mapParam.put("sign",sign);
        mapParam.put("packageName",packageName);
        HttpManager
                .getInstance()
                .getApiService(Api.class, BaseUtil.baseUrl)
                .toSignCode(RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), BaseUtil.mapToJson(mapParam)))
                .compose(RxHelper.<SignCodeBean>rxSchedulerHelper())
                .subscribe(new Observer<SignCodeBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i("http","onSubscribe");
                        //加载进度
                        pDialog2 = ProgressDialog.show(RegisterActivity.this,getString(R.string.hint), getString(R.string.dialog3),false);
                    }

                    @Override
                    public void onNext(@NonNull final SignCodeBean signCodeBean) {
                        Log.i("http","onNext");
                        if (pDialog2 != null) {
                            pDialog2.dismiss();
                        }
                        //成功去注册
                        if (signCodeBean.isSucc()) {
                            toRegisterImpl();
                        }
                        ToastUtils.showShort(signCodeBean.getMsg());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("http","onError:"+e);
                        if (pDialog2 != null) {
                            pDialog2 .dismiss();
                        }
                        ToastUtils.showShort(getString(R.string.toast9));
                    }

                    @Override
                    public void onComplete() {
                        Log.i("http","onComplete");
                    }
                });
    }

    //注册
    public void toRegisterImpl() {
        //
        final String mobile = etUser.getText().toString().trim();
        final String password = etPsw.getText().toString().trim();
        final String code = etCode.getText().toString().trim();
        String packageName = AppUtils.getAppPackageName();
        String deviceId = "";
        //如果权限没有授权，就随机数
        try {
            deviceId = PhoneUtils.getIMEI();
        } catch (Exception e) {

        }
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = BaseUtil.getRandomString(15);
        }
        if (TextUtils.isEmpty(mobile) || !RegexUtils.isMobileSimple(mobile)) {
            ToastUtils.showShort(getResources().getString(R.string.toast1));
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            ToastUtils.showShort(getResources().getString(R.string.toast2));
            return;
        }
        if (TextUtils.isEmpty(code) || code.length() != 6) {
            ToastUtils.showShort(getResources().getString(R.string.toast3));
            return;
        }
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(getResources().getString(R.string.toast6));
            return;
        }
        String noncestr = BaseUtil.genNonceStr();
        String timestamp = String.valueOf(BaseUtil.genTimeStamp());

        SortedMap<Object,Object> signParams = new TreeMap<Object,Object>();
        signParams.put("noncestr", noncestr);
        signParams.put("timestamp", timestamp);
        String sign = BaseUtil.createSign(PayCommonConfig.KEY,signParams);
        String timeExpire= SPUtils.getInstance("user_info").getString("deadtime", "");
        if (TextUtils.equals(timeExpire,"永久")) {
            timeExpire = "2099-01-01 00:00:00";
        }
        if (TextUtils.equals(timeExpire,"0")) {
            timeExpire = "";
        }

        Map<String,String> mapParam = new HashMap<>();
        mapParam.put("noncestr",noncestr);
        mapParam.put("timestamp",timestamp);
        mapParam.put("sign",sign);
        mapParam.put("deviceId", deviceId);
        mapParam.put("mobile",mobile);
        mapParam.put("password",password);
        mapParam.put("packageName",packageName);
        mapParam.put("platformType", PayCommonConfig.sharedCommonConfig.flavorName);
        mapParam.put("timeExpire", timeExpire);
        HttpManager
                .getInstance()
                .getApiService(Api.class, BaseUtil.baseUrl)
                .toRegist(RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), BaseUtil.mapToJson(mapParam)))
                .compose(RxHelper.<RegisterBean>rxSchedulerHelper())
                .subscribe(new Observer<RegisterBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i("http","onSubscribe");
                        //加载进度
                        pDialog3 = ProgressDialog.show(RegisterActivity.this,getString(R.string.hint), getString(R.string.dialog4),false);
                    }

                    @Override
                    public void onNext(@NonNull final RegisterBean registerBean) {
                        Log.i("http","onNext");
                        if (pDialog3 != null) {
                            pDialog3 .dismiss();
                        }
                        ToastUtils.showShort(registerBean.getMsg());
                        if (registerBean.isSucc()) {
                            //
                            SPUtils.getInstance("user_info").put("mobile",mobile);
                            SPUtils.getInstance("user_info").put("password",password);
                            //新添加修改  注意更改实体类
                            //保存登录信息
                            boolean isR = registerBean.getData().isIsR();
                            String timeExpire = "";
                            if (isR) {
                                timeExpire = registerBean.getData().getTimeExpire();
                            }
                            SPUtils.getInstance("user_info").put("timeExpire",timeExpire);
                            SPUtils.getInstance("user_info").put("isR",isR);
                            SPUtils.getInstance("user_info").put("accessToken",registerBean.getData().getAccessToken());
                            SPUtils.getInstance("user_info").put("userId",registerBean.getData().getUserId());
                            SPUtils.getInstance("user_info").put("isLogin",true);
                            finish();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("http","onError:"+e);
                        if (pDialog3 != null) {
                            pDialog3 .dismiss();
                        }
                        ToastUtils.showShort(getString(R.string.toast9));
                    }

                    @Override
                    public void onComplete() {
                        Log.i("http","onComplete");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeCount!=null) {
            timeCount.cancel();
        }
    }
}
