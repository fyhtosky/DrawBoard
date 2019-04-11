package com.example.liusheng.painboard.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.Tools;
import com.example.liusheng.painboard.View.PaletteView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liusheng on 2017/10/24.
 */

public class MyGridViewAdapter extends BaseAdapter {

    int count;
    Context context;
    int cellWidth;

    ArrayList<Integer> widths = new ArrayList<Integer>();

    public MyGridViewAdapter(Context context, int count, int cellWidth) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.count = count;
        this.cellWidth = cellWidth;
        widths.add(16);
        widths.add(18);
        widths.add(20);
        widths.add(22);
        widths.add(24);
        widths.add(26);
        widths.add(28);
        widths.add(30);

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
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_pensizeview, null);
            holder.backgroundIV = convertView.findViewById(R.id.circle_backguoundView);
            holder.frontIV = convertView.findViewById(R.id.circle_frontView);
            convertView.setLayoutParams(new AbsListView.LayoutParams(cellWidth, Tools.dip2px(context ,40)));
            convertView.setTag(holder);
            setHolderWidth(holder, widths.get(position));
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SharedPreferences preferences = context.getSharedPreferences("PenMessage", Context.MODE_PRIVATE);
        int penSize = preferences.getInt("PenSize", 0);
        if (position == penSize) {
            holder.frontIV.setVisibility(View.VISIBLE);
        } else {
            holder.frontIV.setVisibility(View.INVISIBLE);
        }
        holder.backgroundIV.setImageResource(R.color.bar_grey);

        return convertView;
    }

    class ViewHolder {
        CircleImageView backgroundIV;
        CircleImageView frontIV;
    }

    private void setHolderWidth(ViewHolder holder, int width) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.backgroundIV.getLayoutParams();
        params.width = Tools.dip2px(context, width);
        params.height = Tools.dip2px(context, width);
        holder.backgroundIV.setLayoutParams(params);

        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.frontIV.getLayoutParams();
        params2.width = Tools.dip2px(context, width);
        params2.height = Tools.dip2px(context, width);
        holder.frontIV.setLayoutParams(params2);
    }

}
