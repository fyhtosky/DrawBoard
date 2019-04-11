package com.lafonapps.paycommon.payUtils.LocalDataUtil;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.blankj.utilcode.util.ActivityUtils;
import com.lafonapps.paycommon.PayCommonConfig;
import com.lafonapps.paycommon.payUtils.wechatUtils.SPUtils;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class LocalDataUtil {
    public String netTime;
    public static final LocalDataUtil sharedLocalUtil = new LocalDataUtil();

    // 重置本地数据
    public void setInitSharePrefence(Context context) {
        SPUtils.put(context,"isVIP",false);
        SPUtils.put(context,"starttime","0");
        SPUtils.put(context,"deadtime","");
        SPUtils.put(context,"grade","0");
        SPUtils.put(context,"isLifetime",false);
    }

    public void setVipSharePrefence(Context context) {
        SPUtils.put(context,"isVIP",true);
        SPUtils.put(context,"starttime","2018-10-12");
        SPUtils.put(context,"deadtime","2018-11-12");
        SPUtils.put(context,"grade","1");
        SPUtils.put(context,"isLifetime",false);
    }


    // 比较两个时间的大小
    public boolean timeCompare(String date1, String date2) {
        //格式化时间
        SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date beginTime = CurrentTime.parse(date1);
            Date endTime = CurrentTime.parse(date2);
            //判断是否大于
            if (((endTime.getTime() - beginTime.getTime()) / (24 * 60 * 60 * 1000)) >= 0) {
                //大于
                return true;
            } else {
                //小于
                return false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    public boolean getIsVip(Context context) {
        boolean isVip = (boolean)SPUtils.get(context,"isVIP",false);
        boolean isLifetime= (boolean)SPUtils.get(context,"isLifetime",false);
        if (isVip){
            String currenttime= getNetTime();
            String deadtime = (String)SPUtils.get(context,"deadtime","");
            if (deadtime.equals("")) {
                deadtime = "0";
            }
            if (isLifetime){
                isVip = true;
            } else if (timeCompare(currenttime , deadtime)){
                isVip = true;
            }else {
                isVip = false;
                setInitSharePrefence(context);
            }
        }
        return isVip;

    }
    // 获取网络时间,并修改是否是VIP的状态
    public String getNetTime() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;//取得资源对象

                try {
                    url = new URL("http://www.baidu.com");
                    URLConnection uc = url.openConnection();//生成连接对象
                    uc.connect(); //发出连接
                    long ld = uc.getDate(); //取得网站日期时间

                    if (ld == 0) {// 获取网络时间失败，获取系统的时间
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");//设置日期格式
                        df.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                        netTime = df.format(new Date());
                    } else {
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd ");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(ld);
                        String format = formatter.format(calendar.getTime());
                        netTime = format;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");//设置日期格式
                    df.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                    netTime = df.format(new Date());
                }
            }
        }).start();

        if (netTime == null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            df.setTimeZone(TimeZone.getTimeZone("GMT+08"));
            netTime = df.format(new Date());
        }
        return netTime;
    }

    public void vipUser(Context context) {

        String deadtime = (String)SPUtils.get(context,"deadtime","");

        if (deadtime.equals("")) {// 如果订阅截至时间为空，订阅日期以当前网络计算

            deadtime = getNetTime();
        }
        switch (PayCommonConfig.subPayType) {
            case PayCommonConfig.ONEMONTH:
                deadtime = addtime(deadtime, 30);
                break;
            case PayCommonConfig.THREEMONTH:
                deadtime = addtime(deadtime, 90);
                break;
            case PayCommonConfig.ONEYEAR:
                deadtime = addtime(deadtime, 365);
                break;
            case PayCommonConfig.LIFETIME:
                SPUtils.put(context,"isLifetime",true);
                deadtime = "2099-01-01";
                break;
            default:
                break;
        }
        //
        SPUtils.put(context,"isVIP",true);
        SPUtils.put(context,"starttime",getNetTime());
        SPUtils.put(context,"deadtime",deadtime);
    }

    // 计算日期（加日期）
    private String addtime(String day, long dayAddNum) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date newDate2 = new Date(nowDate.getTime() + dayAddNum * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }

    public String getDeadTime(Context context) {

        return   (String) SPUtils.get(context,"deadtime","");

    }

    public String retrunDescribe(String subType){

        return PayCommonConfig.sharedCommonConfig.orderInformation+" - VIP增值服务"+subType+" - "+getFlavor();
    }

    public String getFlavor() {
        String flavorName = PayCommonConfig.sharedCommonConfig.flavorName;
        if (flavorName != null && flavorName.length()>2){
            flavorName = flavorName.substring(0, 1).toUpperCase();
        }
        return flavorName;
    }

    public String getBuySign(String type) {
        return getFlavor()+"-"+type+"-";
    }

    public void showDialog(final Activity context,String title,String message,String positiveText,final Class targetClass) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.create().dismiss();
                    }
                })
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                        ActivityUtils.startActivity(context, targetClass);
                    }
                }).create().show();
    }

    public void showDialog(final Activity context,String message) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("提示")
                .setMessage(message)
                .setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.create().dismiss();
                        //

                    }
                }).create().show();
    }

}
