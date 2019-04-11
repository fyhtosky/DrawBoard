package com.example.liusheng.painboard.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;


import com.example.liusheng.painboard.Activity.MyActivity;
import com.example.liusheng.painboard.Other.StatusBarCompat;
import com.example.liusheng.painboard.R;

import java.io.Serializable;
import java.util.List;

public class CalendarActivity extends MyActivity {

    public static final String SIGN_IN_DATES = "SignInDates";
    public static final String DATE_FORMAT = "DateFormat";

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        StatusBarCompat.compat(this, Color.parseColor("#cf2a3f"));
        calendarView = (CalendarView) findViewById(R.id.calendar);
        Intent intent = getIntent();

        List<String> signInDates = (List<String>) intent.getSerializableExtra(SIGN_IN_DATES);
        String format = intent.getStringExtra(DATE_FORMAT);

        calendarView.setSignInDatesAndFormat(signInDates, format);
    }

    // 弹出日历界面
    public static void show(Context context, List<String> dates, String format) {
        Intent intent = new Intent(context, CalendarActivity.class);
        intent.putExtra(SIGN_IN_DATES, (Serializable) dates);
        intent.putExtra(DATE_FORMAT, format);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.calendar_in, 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.calendar_out, 0);
    }
}
