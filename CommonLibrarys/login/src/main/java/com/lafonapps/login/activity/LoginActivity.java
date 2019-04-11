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
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lafonapps.httputil.BaseUtil;
import com.lafonapps.httputil.HttpManager;
import com.lafonapps.httputil.RxHelper;
import com.lafonapps.login.Api;
import com.lafonapps.login.R;
import com.lafonapps.login.bean.LoginBean;
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


public class LoginActivity extends AppCompatActivity {

    EditText etUser;
    ImageView ivClose;
    EditText etPsw;
    ImageView ivEye;
    TextView tvLogin;
    TextView tvRegister;
    TextView tvFindpsw;
    ImageView ivLoginBack;

    private boolean showPassword = true;
    private ProgressDialog pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        etUser.setText(SPUtils.getInstance("user_info").getString("mobile",""));
        etPsw.setText(SPUtils.getInstance("user_info").getString("password",""));
        //判断是否登录
        if (SPUtils.getInstance("user_info").getBoolean("isLogin",false)) {
            tvLogin.setText(getString(R.string.login_ok));
        } else {
            if (PayCommonConfig.sharedCommonConfig.getProductIsValid(this)&&(getIntent()!=null&&getIntent().getBooleanExtra("isshow",false))) {
                LocalDataUtil.sharedLocalUtil.showDialog(this,getString(R.string.dialog8));
            }
        }
    }

    private void initView() {
        etUser = findViewById(R.id.et_user);
        ivClose = findViewById(R.id.iv_close);
        etPsw = findViewById(R.id.et_psw);
        ivEye = findViewById(R.id.iv_eye);
        tvLogin = findViewById(R.id.tv_login);
        tvRegister = findViewById(R.id.tv_register);
        tvFindpsw = findViewById(R.id.tv_findpsw);
        ivLoginBack = findViewById(R.id.iv_login_back);

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
                tvLogin.setText(getString(R.string.login_sure));
            }
        });

        ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPswShow();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLoginImpl();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去注册直接结束登录
                ActivityUtils.startActivity(LoginActivity.this, RegisterActivity.class);
                finish();
            }
        });

        tvFindpsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(LoginActivity.this, FindPswActivity.class);
            }
        });

    }

    private void initData() {
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

    //登录
    public void toLoginImpl() {
        final String mobile = etUser.getText().toString().trim();
        final String password = etPsw.getText().toString().trim();
        String packageName = AppUtils.getAppPackageName();
        if (TextUtils.isEmpty(mobile) || !RegexUtils.isMobileSimple(mobile)) {
            ToastUtils.showShort(getResources().getString(R.string.toast1));
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            ToastUtils.showShort(getResources().getString(R.string.toast2));
            return;
        }
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(getResources().getString(R.string.toast6));
            return;
        }
        String noncestr = BaseUtil.genNonceStr();
        String timestamp = String.valueOf(BaseUtil.genTimeStamp());
        //新添加
        String vipDate= SPUtils.getInstance("user_info").getString("deadtime", "");
        if (TextUtils.equals(vipDate,"永久")) {
            vipDate = "2099-01-01 00:00:00";
        }
        if (TextUtils.equals(vipDate,"0")) {
            vipDate = "";
        }

        SortedMap<Object, Object> signParams = new TreeMap<Object, Object>();
        signParams.put("noncestr", noncestr);
        signParams.put("timestamp", timestamp);
        String sign = BaseUtil.createSign(PayCommonConfig.KEY, signParams);

        Map<String, String> mapParam = new HashMap<>();
        mapParam.put("noncestr", noncestr);
        mapParam.put("timestamp", timestamp);
        mapParam.put("sign", sign);
        mapParam.put("mobile", mobile);
        mapParam.put("password", password);
        mapParam.put("packageName", packageName);
        //新添加
        mapParam.put("vipDate", vipDate);
        HttpManager
                .getInstance()
                .getApiService(Api.class, BaseUtil.baseUrl)
                .toLogin(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), BaseUtil.mapToJson(mapParam)))
                .compose(RxHelper.<LoginBean>rxSchedulerHelper())
                .subscribe(new Observer<LoginBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i("http", "onSubscribe");
                        //加载进度
                        pDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.hint), getString(R.string.dialog1), false);
                    }

                    @Override
                    public void onNext(@NonNull final LoginBean loginBean) {
                        Log.i("http", "onNext");
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
                        SPUtils.getInstance("user_info").put("mobile",mobile);
                        SPUtils.getInstance("user_info").put("password",password);
                        if (loginBean.isSucc()) {
                            //保存登录信息
                            SPUtils.getInstance("user_info").put("timeExpire",loginBean.getData().getTimeExpire());
                            SPUtils.getInstance("user_info").put("isR",loginBean.getData().isIsR());
                            SPUtils.getInstance("user_info").put("accessToken",loginBean.getData().getAccessToken());
                            SPUtils.getInstance("user_info").put("userId",loginBean.getData().getUserId());
                            SPUtils.getInstance("user_info").put("isLogin",true);
                            //vip老用户
                            if (PayCommonConfig.sharedCommonConfig.getProductIsValid(LoginActivity.this)) {
                                LocalDataUtil.sharedLocalUtil.setInitSharePrefence(LoginActivity.this);
                            }
                            finish();
                        } else {
                            SPUtils.getInstance("user_info").put("timeExpire","");
                            SPUtils.getInstance("user_info").put("isR",false);
                            SPUtils.getInstance("user_info").put("accessToken","");
                            SPUtils.getInstance("user_info").put("userId","");
                            SPUtils.getInstance("user_info").put("isLogin",false);
                        }
                        ToastUtils.showShort(loginBean.getMsg());

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("http", "onError:" + e);
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
                        ToastUtils.showShort(getResources().getString(R.string.toast9));
                    }

                    @Override
                    public void onComplete() {
                        Log.i("http", "onComplete");
                    }
                });
    }
}

