package com.example.liusheng.painboard.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.Tools;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liusheng on 2017/11/1.
 */

public class MyGridViewAdapter2 extends BaseAdapter {

    int count;
    Context context;
    int cellWidth;

    ArrayList<Integer> colors = new ArrayList<Integer>();

    public MyGridViewAdapter2(Context context, int count, int cellWidth) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.count = count;
        this.cellWidth = cellWidth;

        colors.add(R.color.penColor1);
        colors.add(R.color.penColor2);
        colors.add(R.color.penColor3);
        colors.add(R.color.penColor4);
        colors.add(R.color.penColor5);
        colors.add(R.color.penColor6);
        colors.add(R.color.penColor7);
        colors.add(R.color.penColor8);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return count;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stud
        ViewHolder2 holder;
        if (null == convertView) {
            holder = new ViewHolder2();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_pensizeview, null);
            holder.backgroundIV = convertView.findViewById(R.id.circle_backguoundView);
            holder.frontIV = convertView.findViewById(R.id.circle_frontView);
            convertView.setLayoutParams(new AbsListView.LayoutParams(cellWidth, Tools.dip2px(context ,40)));
            convertView.setTag(holder);
            holder.backgroundIV.setImageResource(colors.get(position));
        } else {
            holder = (ViewHolder2) convertView.getTag();
        }

        SharedPreferences preferences = context.getSharedPreferences("PenMessage", Context.MODE_PRIVATE);
        int penSize = preferences.getInt("PenColor", 0);
        if (position == penSize) {
            holder.frontIV.setVisibility(View.VISIBLE);
        } else {
            holder.frontIV.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    static class ViewHolder2 {
        CircleImageView backgroundIV;
        CircleImageView frontIV;
    }

}
