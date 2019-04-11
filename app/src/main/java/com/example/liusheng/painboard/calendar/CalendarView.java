package com.example.liusheng.painboard.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.liusheng.painboard.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xiongzhifan on 2018/1/24.
 */

public class CalendarView extends ConstraintLayout implements View.OnClickListener {

    // 所有签到日
    private List<Date> signInDates = new ArrayList<>();
    // 日期格式化
    private SimpleDateFormat dateFormat;

    // 顶部标题栏
    private LinearLayout topbar;
    // 顶部标题栏文本框
    private TextView titleTV;
    // 上一个月 按钮
    private ImageView lastMonthBtn;
    // 下一个月 按钮
    private ImageView nextMonthBtn;
    // 当前月份文本框
    private TextView currentMonthTV;
    // 当月所有天数 内容视图
    private RecyclerView daysGridView;
    // 完成按钮
    private TextView doneBtn;

    // 顶部标题栏背景颜色
    private int titleBarColor;
    // 顶部标题栏文字
    private String title;
    // 顶部标题栏文字颜色
    private int titleTextColor;
    // 当前时间TextView文字颜色
    private int monthTitleColor;
    // 上一个月按钮图片
    private int lastMonthImage;
    // 下一个月按钮图片
    private int nextMonthImage;
    // 日历每个天的文字颜色
    private int dayItemColor;
    // 选中天的图片
    private int daySelectItemImage;
    // 完成按钮的背景颜色
    private int doneButtonBackground;
    // 完成按钮的圆角大小
    private float doneButtonRadius;
    // 完成按钮的文字
    private String doneButtonText;
    // 完成按钮的文字颜色
    private int doneButtonTextColor;

    // 当前年份
    private int year;
    // 当前月份
    private int month;
    // 当前月份对应天数
    private int currentMonthDays;

    // 日历类
    private Calendar calendar = Calendar.getInstance();

    private Context context;

    public void setSignInDatesAndFormat(List<String> signInDates, String format) {
        this.dateFormat = new SimpleDateFormat(format);
        this.signInDates.clear();

        for (String item: signInDates) {
            try {
                Date date = this.dateFormat.parse(item);
                this.signInDates.add(date);
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }
        daysAdapter.notifyDataSetChanged();
    }


    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.calendar_view, this, true);

        this.context = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        titleBarColor = ta.getColor(R.styleable.CalendarView_titleBarColor, Color.parseColor("#E7003B"));

        title = ta.getString(R.styleable.CalendarView_title);

        titleTextColor = ta.getColor(R.styleable.CalendarView_titleTextColor, Color.parseColor("#FFFFFF"));

        monthTitleColor = ta.getColor(R.styleable.CalendarView_dateTitleColor, Color.parseColor("#405980"));

        lastMonthImage = ta.getResourceId(R.styleable.CalendarView_dateLeftImage, R.drawable.left_icon_qd);

        nextMonthImage = ta.getResourceId(R.styleable.CalendarView_dateRightImage, R.drawable.right_icon_qd);

        dayItemColor = ta.getColor(R.styleable.CalendarView_dayItemColor, Color.parseColor("#405980"));

        daySelectItemImage = ta.getResourceId(R.styleable.CalendarView_daySelectItemImage, R.drawable.current_icon_qd);

        doneButtonBackground = ta.getColor(R.styleable.CalendarView_doneButtonBackground, Color.parseColor("#A4C7FD"));

        doneButtonRadius = ta.getFloat(R.styleable.CalendarView_doneButtonRadius, 5.f);

        doneButtonText = ta.getString(R.styleable.CalendarView_doneButtonText);

        doneButtonTextColor = ta.getColor(R.styleable.CalendarView_doneButtonTextColor, Color.parseColor("#FFFFFF"));

        ta.recycle();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);

        setupSubviews();
    }


    private void setupSubviews() {

        topbar = (LinearLayout) findViewById(R.id.title_bar);
        topbar.setBackgroundColor(titleBarColor);

        titleTV = (TextView) findViewById(R.id.title);
        titleTV.setText(title);
        titleTV.setTextColor(titleTextColor);

        lastMonthBtn = (ImageView) findViewById(R.id.last_month);
        lastMonthBtn.setImageResource(lastMonthImage);
        lastMonthBtn.setOnClickListener(this);

        nextMonthBtn = (ImageView) findViewById(R.id.next_month);
        nextMonthBtn.setImageResource(nextMonthImage);
        nextMonthBtn.setOnClickListener(this);

        currentMonthTV = (TextView) findViewById(R.id.current_month);

        updateContent();

        daysGridView = (RecyclerView) findViewById(R.id.month_days);
        daysGridView.setAdapter(daysAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 7);
        daysGridView.setLayoutManager(layoutManager);


        doneBtn = (TextView) findViewById(R.id.done);
        doneBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.last_month:
                calendar.add(Calendar.MONTH, -1);
                updateContent();
                break;
            case R.id.next_month:
                calendar.add(Calendar.MONTH, 1);
                updateContent();
                break;
            case R.id.done:
                ((Activity) context).finish();
                break;
        }
    }


    private void updateContent() {

        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        String ymStr = String.format("%04d-%02d", year, month+1);

        currentMonthTV.setText(ymStr);

        daysAdapter.notifyDataSetChanged();
    }

    private RecyclerView.Adapter<ViewHolder> daysAdapter = new RecyclerView.Adapter<ViewHolder>() {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_day_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int begin = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            int end = calendar.get(Calendar.DAY_OF_MONTH) + begin - 1;

            holder.daySelImgV.setVisibility(View.GONE);

            if (position < begin || position > end) {
                holder.dayStrTV.setText("");
            }else {
                String dayStr = String.format("%d", position - begin + 1);
                holder.dayStrTV.setText(dayStr);

                SimpleDateFormat tempFormat = new SimpleDateFormat("yyyy");

                for (Date date: signInDates) {
                    int day = date.getDate();
                    int month = date.getMonth();
                    int year = Integer.valueOf(tempFormat.format(date)).intValue();

                    int c_day = position - begin + 1;
                    int c_month = calendar.get(Calendar.MONTH);
                    int c_year = calendar.get(Calendar.YEAR);

                    if (c_day == day && c_month == month && c_year == year) {
                        holder.daySelImgV.setVisibility(View.VISIBLE);
                        break;
                    }
                }

            }
        }

        @Override
        public int getItemCount() {

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int begin = calendar.get(Calendar.DAY_OF_WEEK);
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            int dayMax = calendar.get(Calendar.DAY_OF_MONTH);
            int end = dayMax + begin;
            int total = (int)Math.ceil((float)end / 7.f) * 7;
            return total;
        }
    };

    private static class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView daySelImgV;
        TextView dayStrTV;

        public ViewHolder(View itemView) {
            super(itemView);
            daySelImgV = (ImageView) itemView.findViewById(R.id.day_sel_img);
            dayStrTV = (TextView) itemView.findViewById(R.id.day_str);
        }
    }

}
