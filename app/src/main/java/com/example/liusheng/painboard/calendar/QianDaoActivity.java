package com.example.liusheng.painboard.calendar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.liusheng.painboard.Activity.MyActivity;
import com.example.liusheng.painboard.Other.Singleton;
import com.example.liusheng.painboard.Other.StatusBarCompat;
import com.example.liusheng.painboard.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QianDaoActivity extends MyActivity {

    public String currentTime;
    public TextView haveUseText;
    public TextView policyText;
    public TextView nextProgText;
    public ImageView policyBg;

    public ImageView progressImg2;
    public ImageView progressImg3;
    public static QianDaoActivity instance = null;
    public ImageView haveComplete1;
    public ImageView haveComplete2;
    public ImageView haveComplete3;
    public ImageView yestadyImg;

    public TextView sevenText;
    public TextView lianxuDetalText;
    public TextView yestadyText;

    public TextView fourtyText;

    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qiandao);
        StatusBarCompat.compat(this, Color.parseColor("#cf2a3f"));

        init();
        instance=this;

        SharedPreferences IntervalTimepreferences = getSharedPreferences("IntervalTime", MODE_PRIVATE);
        String interTime = IntervalTimepreferences.getString("Interval", "0");
        int t = Integer.parseInt(interTime);

        optionUI(t);

    }


    private void init(){

        haveUseText = (TextView)findViewById(R.id.haveuseText);
        policyText = (TextView)findViewById(R.id.lianxuDetalText);
        nextProgText = (TextView)findViewById(R.id.needToUseText);
        progressImg2 = (ImageView) findViewById(R.id.progressImg2);
        progressImg3 = (ImageView) findViewById(R.id.progressImg3);

        haveComplete1 = (ImageView) findViewById(R.id.haveCompleteThree);
        haveComplete2 = (ImageView) findViewById(R.id.haveCompleteSeven);
        haveComplete3 = (ImageView) findViewById(R.id.haveCompleteFourty);

        sevenText = (TextView)findViewById(R.id.sevenText);
        fourtyText = (TextView)findViewById(R.id.fourtyText);

        yestadyImg = (ImageView) findViewById(R.id.zuotianImg);
        yestadyText = (TextView)findViewById(R.id.zuotianText);

        lianxuDetalText = (TextView)findViewById(R.id.lianxuDetalText);
        String detailText = "您在使用画画板时会产生一个累计使用天，每个自然日只能获得一个累计使用天，当累计使用天数达到相应标准时，您将获得包括移除广告在内的相关特权";
        SpannableStringBuilder span = new SpannableStringBuilder("缩进"+detailText);
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        lianxuDetalText.setText(span);

        progressBar = (ProgressBar)findViewById(R.id.progressBar05_id);


    }



    //UI操作
    private void optionUI(int continTime){

        String coutinStr = "您已经累计使用 "+continTime+"  天";
        String str = coutinStr;
        int size = str.length();
        Spannable span = new SpannableString(str);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pingColor)), 7, size-2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new RelativeSizeSpan(3.0f), 7, size-2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        haveUseText.setText(span);
        progressBar.setProgress(continTime);
//        if (continTime<3){
//            int t = 3-continTime;
//            nextProgText.setText("还需要累计"+t+"天即可解锁获取更多素材特权");
//
//        }else
            if (continTime > 0 && continTime<7){
            int t = 7-continTime;
            nextProgText.setText("还需要累计"+t+"天即可解锁移除应用内部广告特权");
            haveComplete1.setImageResource(R.drawable.yiwancheng_icon);
        }else if (continTime >= 7 && continTime<14){
            int t = 14-continTime;
            nextProgText.setText("还需要累计"+t+"天即可解锁启动无广告特权");
            haveComplete1.setImageResource(R.drawable.yiwancheng_icon);
            haveComplete2.setImageResource(R.drawable.yiwancheng_icon);
            sevenText.setTextColor(getResources().getColor(R.color.greenColor));
            progressImg2.setImageResource(R.drawable.lvse_yuandian_qd);
        }else {
            nextProgText.setText("您已解锁全部特权");
            haveComplete1.setImageResource(R.drawable.yiwancheng_icon);
            haveComplete3.setImageResource(R.drawable.yiwancheng_icon);
            haveComplete2.setImageResource(R.drawable.yiwancheng_icon);
            sevenText.setTextColor(getResources().getColor(R.color.greenColor));
            fourtyText.setTextColor(getResources().getColor(R.color.greenColor));
            progressImg2.setImageResource(R.drawable.lvse_yuandian_qd);
            progressImg3.setImageResource(R.drawable.lvse_yuandian_qd);

        }

        SaveArrayListUtil saveArr = new SaveArrayListUtil();
        ArrayList arrayList = saveArr.getSearchArrayList(QianDaoActivity.this);
        String lastDay = "";
        if (arrayList.size()>=2){
            lastDay = (String) arrayList.get(1);
        }else {

            return;
        }
        Singleton.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date Todaydate = null;
        try {
            Todaydate = formatter.parse(Singleton.currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date lastDate = null;
        try {
            lastDate = lastDate = formatter.parse(lastDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long slightly = twoDateDistance(lastDate,Todaydate);
        if (slightly == 1){


            yestadyImg.setImageResource(R.drawable.yishiyong_icon_qd);
            yestadyText.setText("昨天已使用");

        }


    }

    /**
     * 计算两个日期型的时间相差多少时间
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return
     */
    public  long twoDateDistance(Date startDate, Date endDate) {

        if (startDate == null || endDate == null) {
            return 0;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 60 * 60 * 24 * 1000 * 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return timeLong;
        }

        return 0;
    }



    public void clickBtton(View view){

        switch (view.getId()){

            case R.id.guabbiBtn:
                finish();
                break;
            case R.id.jiliuBtn:

                //首先取出数组
                SaveArrayListUtil saveArr = new SaveArrayListUtil();
                ArrayList arrayList = saveArr.getSearchArrayList(this);
                CalendarActivity.show(this, arrayList,"yyyy-MM-dd");

                Log.i("签到记录", "签到记录");

                break;

        }

    }

}

