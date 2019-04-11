package com.example.liusheng.painboard.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.liusheng.painboard.R;

import java.util.List;

/**
 * Created by chenzhen on 2019/3/26.
 */

public class LinearHorizontalAdapter extends RecyclerView.Adapter<LinearHorizontalAdapter.ViewHolder> {


    static final String TAG = "LinearHorizontalAdapter";

    RequestOptions mOptions;
    List<Integer> mData;
    Context mContext;
    Callback mCallback;

    public LinearHorizontalAdapter(Context mContext, List<Integer> mData, Callback callback) {
        this.mContext = mContext;
        this.mData = mData;
        this.mCallback = callback;
        this.mOptions = new RequestOptions().centerCrop();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.bg_select_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final int resId = mData.get(position);
        Glide.with(mContext.getApplicationContext()).asBitmap().apply(mOptions).load(resId).into(holder.icon);
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemImageClick(v, position, resId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }


    public interface Callback {
        void onItemImageClick(View view, int position, int resId);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
