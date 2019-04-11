package com.lafonapps.login.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lafonapps.httputil.BaseUtil;
import com.lafonapps.httputil.HttpManager;
import com.lafonapps.httputil.RxHelper;
import com.lafonapps.login.Api;
import com.lafonapps.login.R;
import com.lafonapps.login.bean.GetCodeBean;
import com.lafonapps.login.bean.SignCodeBean;
import com.lafonapps.login.utils.TimeCount;
import com.lafonapps.login.utils.ViewUtils;
import com.lafonapps.paycommon.PayCommonConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;


public class FindPswActivity extends AppCompatActivity {

    ImageView ivFindpswBack;
    EditText etUser;
    ImageView ivClose;
    EditText etCode;
    TextView tvGetcode;
    TextView tvFindpsw;

    private TimeCount timeCount;
    private ProgressDialog pDialog1, pDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_psw);
        initView();
        initData();
    }

    private void initView() {
        ivFindpswBack = findViewById(R.id.iv_findpsw_back);
        etUser = findViewById(R.id.et_user);
        etCode = findViewById(R.id.et_code);
        tvGetcode = findViewById(R.id.tv_getcode);
        tvFindpsw = findViewById(R.id.tv_findpsw);
        ivClose = findViewById(R.id.iv_close);

        ivFindpswBack.setOnClickListener(new View.OnClickListener() {
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

        tvGetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGetCodeImpl();
            }
        });

        tvFindpsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSignCodeImpl();
            }
        });

    }

    private void initData() {
        timeCount = new TimeCount(this,60*1000,tvGetcode);
        //设置监听
        ViewUtils.showDelete(etUser, ivClose);
        ViewUtils.showDelete(etUser, ivClose);
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
                        pDialog1 = ProgressDialog.show(FindPswActivity.this,getString(R.string.hint), getString(R.string.dialog2),false);
                    }

                    @Override
                    public void onNext(@NonNull final GetCodeBean getCodeBean) {
                        Log.i("http","onNext");
                        if (pDialog1 != null) {
                            pDialog1 .dismiss();
                        }
                        ToastUtils.showShort(getCodeBean.getMsg());
                        timeCount.start();
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
        final String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(mobile) || !RegexUtils.isMobileSimple(mobile)) {
            ToastUtils.showShort(getResources().getString(R.string.toast1));
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
                        pDialog2 = ProgressDialog.show(FindPswActivity.this,getString(R.string.hint), getString(R.string.dialog3),false);
                    }

                    @Override
                    public void onNext(@NonNull final SignCodeBean signCodeBean) {
                        Log.i("http","onNext");
                        if (pDialog2 != null) {
                            pDialog2.dismiss();
                        }
                        //成功跳转去重置
                        if (signCodeBean.isSucc()) {
                            Intent intent = new Intent(FindPswActivity.this,ResetPswActivity.class);
                            intent.putExtra("mobile",mobile);
                            startActivity(intent);
                            finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeCount!=null) {
            timeCount.cancel();
        }
    }
}
