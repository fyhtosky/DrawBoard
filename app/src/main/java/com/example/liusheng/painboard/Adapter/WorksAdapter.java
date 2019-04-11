package com.example.liusheng.painboard.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.RoundedCornersTransformation;
import com.example.liusheng.painboard.bean.WorkBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chenzhen on 2019/2/15.
 */

public class WorksAdapter extends RecyclerView.Adapter<WorksAdapter.ViewHolder> {

    static final String TAG = WorksAdapter.class.getSimpleName();


    Context mContext;
    List<WorkBean> mData;
    RequestOptions mOptions;
    Callback mCallback;
    SimpleDateFormat mFormat;
    Date mDate;

    public WorksAdapter() {
    }

    public WorksAdapter(Context mContext, List<WorkBean> data) {
        this.mContext = mContext;
        this.mData = data;
        this.mOptions = new RequestOptions().transform(new RoundedCornersTransformation(15,0,
                RoundedCornersTransformation.CornerType.TOP));
        this.mFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.mDate = new Date();
    }

    public WorksAdapter(Context context, List<WorkBean> data, Callback callback) {
        this(context,data);
        this.mCallback = callback;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.works_item_layout, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        WorkBean workBean = mData.get(position);
        final String imagePath = workBean.getImagePath();
        Glide.with(mContext.getApplicationContext()).asBitmap().apply(mOptions).load(imagePath).into(holder.mThumb);
        mDate.setTime(workBean.getCreatDate());
        holder.mDate.setText(mFormat.format(mDate));
        holder.mTitle.setText(workBean.getCreatDate()+"");

        holder.mThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemImageClick(v, position, imagePath);
                }
            }
        });

        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemDeleteClick(v, position, imagePath);
                }
            }
        });

        holder.mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemShareClick(v, position, imagePath);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public interface Callback {
        void onItemImageClick(View view, int position, String imagePath);
        void onItemShareClick(View view, int position, String imagePath);
        void onItemDeleteClick(View view, int position, String imagePath);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mThumb,mShare,mDelete;
        TextView mDate,mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.iv_thumb);
            mShare = itemView.findViewById(R.id.iv_share);
            mDelete = itemView.findViewById(R.id.iv_delete);
            mDate = itemView.findViewById(R.id.tv_date);
            mTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
