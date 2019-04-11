package com.lafonapps.common.feedback.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lafonapps.common.R;
import com.lafonapps.common.feedback.JumpContactOperation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 反馈界面 2018.6.4
 * 提交反馈信息到公司后台
 */
public class FeedbackInputActivity extends RBActivity {
    /**
     * 服务器url
     */
    public static String FEEDBACK_URL = "http://121.40.61.21:8080/Statistics_branch/transit/addData";
    /**
     * app 名称
     */
    public static String APP_NAME = null;
    /**
     * 设备唯一标识
     */
    public static String DEVICE_ID = null;

    /**
     * 意见内容，不可为空
     */
    private EditText feedback_content_edit;
    /**
     * 联系方式，不可为空
     */
    private EditText information;

    private TextView commit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_input);

        feedback_content_edit = (EditText)findViewById(R.id.feedback_content_edit);
        commit = (TextView)findViewById(R.id.commit);
        information = (EditText)findViewById(R.id.information);
    }

    public void onClick(View v) {
        email(v);
    }

    public void onClick_back(View v) {
        finish();
    }

    public void commit(View v) {

        String content = feedback_content_edit.getText().toString().trim();
        String contact = information.getText().toString().trim();
        if ("".equals(content)) {
            Toast.makeText(this, "请输入反馈信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(contact)) {
            Toast.makeText(this, "请输入您的联系方式", Toast.LENGTH_SHORT).show();
            return;
        }
        commit.setEnabled(false);
        sendFeedback();
    }

    /**
     * qq联系
     */
    public void clickQQ(View v) {
        boolean isQQ = JumpContactOperation.installQQ(this);
        if (!isQQ) {
            Toast.makeText(this, "您没有安装QQ", Toast.LENGTH_SHORT).show();
            return;
        }
        JumpContactOperation operation = new JumpContactOperation(this);
        operation.jumpQQ("3213640836");
    }

    /**
     * email联系
     */
    public void email(View v) {
        JumpContactOperation operation = new JumpContactOperation(this);
        operation.jumpEmail("wmxyfacebook@gmail.com");
    }



    /**
     * 发送反馈信息
     */
    void sendFeedback() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, FEEDBACK_URL, getParm(), new Response.Listener<JSONObject>() {
            /**
             * @param response
             */
            @Override
            public void onResponse(JSONObject response) {
                commit.setEnabled(true);
                if (response != null) {
                    String code;
                    try {
                        code = response.getString("code");
                    } catch (JSONException e) {
                        code = "-1";
                    }
                    if ("200".equals(code)) {
                        finish();
                        Toast.makeText(FeedbackInputActivity.this, "评论发送成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FeedbackInputActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                commit.setEnabled(true);
                Toast.makeText(FeedbackInputActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * 反馈信息参数
     */
    public JSONObject getParm() {
        if (APP_NAME == null || DEVICE_ID == null) {
            throw new NullPointerException("请在自定义application内对FeedbackInputActivity.APP_NAME和DEVICE_ID进行赋值");
        }
        JSONObject map = new JSONObject();
        try {
            String content = feedback_content_edit.getText().toString().trim();
            map.put("product_name", APP_NAME);
            map.put("package_name", getPackageName());
            map.put("device_type", getDeviceType());
            map.put("comments", content);
            map.put("comments_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            map.put("feedback_email", information.getText().toString().trim().equals("") != true ? information.getText().toString() : "null");
            map.put("device_id", getIMEI());
            map.put("product_version", getVersion());
            map.put("region", getRegionCode());
            map.put("product_language", getLanguageCode());
        /*    Log.i("map",map.toString());*/
        } catch (Exception e) {
        }
        return map;
    }

    /**
     * 获取当前使用语言
     */
    public String getLanguageCode() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }

        String language = locale.getLanguage();
        return language != null ? language : "ch";
    }

    /**
     * 例："CN"
     */
    public String getRegionCode() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }

        String region = locale.getCountry();
        return region != null ? region : "CN";
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public String getVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * 设备唯一标识  需
     */
    public String getIMEI() {
        String deviceID = DEVICE_ID;
        return deviceID != null ? deviceID : "null";
    }

    /***
     * 获取手机类型
     */
    public String getDeviceType() {
        StringBuilder type = new StringBuilder();
        String brand = Build.BRAND;
        String modal = Build.MODEL;
        if (brand != null && modal != null) {
            type.append(brand).append(" ").append(modal);
        }
        return type.toString();
    }
}
