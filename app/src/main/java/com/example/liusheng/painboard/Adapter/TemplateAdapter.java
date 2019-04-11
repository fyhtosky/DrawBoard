package com.example.liusheng.painboard.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.RoundedCornersTransformation;
import com.example.liusheng.painboard.bean.TemplateBean;
import java.util.List;

/**
 * Created by chenzhen on 2019/2/20.
 */

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {


    static final String TAG = TemplateAdapter.class.getSimpleName();

    Context mContext;
    List<TemplateBean> mData;
    RequestOptions mOptions;
    Callback mCallback;


    public TemplateAdapter() {
    }

    public TemplateAdapter(Context context, List<TemplateBean> data) {
        this.mContext = context;
        this.mData = data;
        this.mOptions = new RequestOptions().transform(new RoundedCornersTransformation(15,0,
                RoundedCornersTransformation.CornerType.TOP)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
    }

    public TemplateAdapter(Context context, List<TemplateBean> data,Callback callback) {
        this(context,data);
        this.mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.template_item_layout, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TemplateBean bean = mData.get(position);
        String path = bean.getPath();
        Glide.with(mContext.getApplicationContext()).asBitmap().apply(mOptions).load(path).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null){
                    mCallback.onItemImageClick(v,position,bean.getPath(),bean.getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public interface Callback {
        void onItemImageClick(View view, int position, String imagePath,String imageName);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_img);
        }
    }
}
