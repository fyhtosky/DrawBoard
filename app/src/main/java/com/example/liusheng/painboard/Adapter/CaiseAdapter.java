package com.example.liusheng.painboard.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.liusheng.painboard.R;

import java.util.List;

/**
 * Created by Administrator on 2017/10/4 0004.
 */

public class CaiseAdapter extends BaseAdapter {
    private Context context;

    public CaiseAdapter(Context context, List<Drawable> list) {
        this.context = context;
        this.list = list;
    }

    private List<Drawable> list;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View conterView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (conterView == null){
            conterView = View.inflate(context, R.layout.adapter_color,null);
            viewHolder = new ViewHolder(conterView);
            conterView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) conterView.getTag();

        }
        viewHolder.pic.setBackground(list.get(i));
        return conterView;
    }
    static class ViewHolder{
        ImageView pic;
        public ViewHolder(View view) {
            pic = (ImageView)view.findViewById(R.id.imageview);

        }
    }

}
