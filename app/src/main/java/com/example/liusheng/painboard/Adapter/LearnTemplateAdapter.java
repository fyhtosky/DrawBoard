package com.example.liusheng.painboard.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.RoundedCornersTransformation;
import com.example.liusheng.painboard.bean.LearnItemBean;
import java.util.List;

/**
 * Created by chenzhen on 2019/2/20.
 */

public class LearnTemplateAdapter extends RecyclerView.Adapter<LearnTemplateAdapter.ViewHolder> {


    static final String TAG = LearnTemplateAdapter.class.getSimpleName();

    Context mContext;
    List<LearnItemBean> mData;
    RequestOptions mOptions;
    Callback mCallback;


    public LearnTemplateAdapter() {
    }

    public LearnTemplateAdapter(Context context, List<LearnItemBean> data) {
        this.mContext = context;
        this.mData = data;
        this.mOptions = new RequestOptions().transform(new RoundedCornersTransformation(15, 0, RoundedCornersTransformation.CornerType.TOP));
    }

    public LearnTemplateAdapter(Context context, List<LearnItemBean> data, Callback callback) {
        this(context, data);
        this.mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.template_item_layout, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        LearnItemBean bean = mData.get(position);
        final int backgroundImageId = bean.getBackgroundImageId();
        final int guideId = bean.getGuideImageId();
        Glide.with(mContext).asBitmap().apply(mOptions).load(backgroundImageId).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemImageClick(v, backgroundImageId, guideId);
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
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_img);
        }
    }
}
