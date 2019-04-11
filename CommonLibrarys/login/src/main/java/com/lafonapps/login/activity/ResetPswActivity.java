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

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lafonapps.httputil.BaseUtil;
import com.lafonapps.httputil.HttpManager;
import com.lafonapps.httputil.RxHelper;
import com.lafonapps.login.Api;
import com.lafonapps.login.R;
import com.lafonapps.login.bean.FindPswBean;
import com.lafonapps.login.utils.ViewUtils;
import com.lafonapps.paycommon.PayCommonConfig;


import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class ResetPswActivity extends AppCompatActivity {

    ImageView ivResetpswBack;
    EditText etPsw0;
    ImageView ivClose0;
    EditText etPsw1;
    ImageView ivClose1;
    TextView tvFindpsw;
    private String tel = "";

    private ProgressDialog pDialog;
    private boolean showPassword0 = true;
    private boolean showPassword1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pws);
        initView();
        initData();
    }

    private void initView() {
        ivResetpswBack = findViewById(R.id.iv_resetpsw_back);
        etPsw0 = findViewById(R.id.et_psw0);
        ivClose0 = findViewById(R.id.iv_close0);
        etPsw1 = findViewById(R.id.et_psw1);
        ivClose1 = findViewById(R.id.iv_close1);
        tvFindpsw = findViewById(R.id.tv_findpsw);

        ivResetpswBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivClose0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPswShow(1);
            }
        });

        ivClose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPswShow(2);
            }
        });

        tvFindpsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFindPswImpl();
            }
        });
    }

    private void initData() {
        //
        if (getIntent()!=null) {
            tel = getIntent().getExtras().getString("mobile");
        }
        //设置监听
        ViewUtils.showDelete(etPsw0, ivClose0);
        ViewUtils.showDelete(etPsw1, ivClose1);
    }

    //找回密码
    public void toFindPswImpl() {
        final String password0 = etPsw0.getText().toString().trim();
        final String password1 = etPsw1.getText().toString().trim();
        String packageName = AppUtils.getAppPackageName();
        if (TextUtils.isEmpty(password0) || password0.length() < 6) {
            ToastUtils.showShort(getResources().getString(R.string.toast2));
            return;
        }
        if (TextUtils.isEmpty(password1) || password1.length() < 6) {
            ToastUtils.showShort(getResources().getString(R.string.toast2));
            return;
        }
        if (!password0.equals(password1)) {
            ToastUtils.showShort(getResources().getString(R.string.toast8));
            return;
        }
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(getResources().getString(R.string.toast6));
            return;
        }
        String noncestr = BaseUtil.genNonceStr();
        String timestamp = String.valueOf(BaseUtil.genTimeStamp());

        SortedMap<Object, Object> signParams = new TreeMap<Object, Object>();
        signParams.put("noncestr", noncestr);
        signParams.put("timestamp", timestamp);
        String sign = BaseUtil.createSign(PayCommonConfig.KEY, signParams);

        Map<String, String> mapParam = new HashMap<>();
        mapParam.put("noncestr", noncestr);
        mapParam.put("timestamp", timestamp);
        mapParam.put("sign", sign);
        mapParam.put("mobile", tel);
        mapParam.put("password", password1);
        mapParam.put("packageName", packageName);
        HttpManager
                .getInstance()
                .getApiService(Api.class, BaseUtil.baseUrl)
                .toFindPaw(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), BaseUtil.mapToJson(mapParam)))
                .compose(RxHelper.<FindPswBean>rxSchedulerHelper())
                .subscribe(new Observer<FindPswBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i("http", "onSubscribe");
                        //加载进度
                        pDialog = ProgressDialog.show(ResetPswActivity.this, getString(R.string.hint), getString(R.string.dialog5), false);
                    }

                    @Override
                    public void onNext(@NonNull final FindPswBean findPswBean) {
                        Log.i("http", "onNext");
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
                        if (findPswBean.isSucc()) {
                            finish();
                        }
                        ToastUtils.showShort(findPswBean.getMsg());


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("http", "onError:" + e);
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
                        ToastUtils.showShort(getString(R.string.toast9));
                    }

                    @Override
                    public void onComplete() {
                        Log.i("http", "onComplete");
                    }
                });
    }


    //密码显示隐藏控制
    private void toPswShow(int sign) {
        if (sign == 1) {
            if (showPassword0) {// 显示密码
                ViewUtils.showPsw(this, etPsw0, ivClose0, showPassword0);
                showPassword0 = !showPassword0;
            } else {
                ViewUtils.showPsw(this, etPsw0, ivClose0, showPassword0);
                showPassword0 = !showPassword0;
            }
        } else {
            if (showPassword1) {// 显示密码
                ViewUtils.showPsw(this, etPsw1, ivClose1, showPassword1);
                showPassword1 = !showPassword1;
            } else {
                ViewUtils.showPsw(this, etPsw1, ivClose1, showPassword1);
                showPassword1 = !showPassword1;
            }
        }

    }
}
