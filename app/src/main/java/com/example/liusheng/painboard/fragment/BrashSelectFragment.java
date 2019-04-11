package com.example.liusheng.painboard.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.draw.DrawAttribute;
import com.example.liusheng.painboard.event.BrushFragmentEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * 笔刷选择
 * Created by chenzhen on 2019/3/21.
 */

public class BrashSelectFragment extends BaseFragment {

    public static final String TAG = BrashSelectFragment.class.getSimpleName();

    RecyclerView mRecyclerView;

    public static BrashSelectFragment newInstance(String tag) {
        BrashSelectFragment fragment = new BrashSelectFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int generateLayoutId() {
        return R.layout.fragment_brash_select;
    }

    @Override
    protected void init() {
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //mRecyclerView.addItemDecoration(new SpaceItemDecoration(5, "recycle1"));
        BrushAdapter adapter = new BrushAdapter(getBrash());
        mRecyclerView.setAdapter(adapter);
    }


    static final int[] icons = {
            R.drawable.ic_brush_round,
            R.drawable.ic_brush_fluore,
            R.drawable.ic_brush_mark,
            R.drawable.ic_brush_pixel,
            R.drawable.ic_brush_tan,
    };

    public SparseArray<String> getBrash() {
        SparseArray<String> array = new SparseArray();
        array.append(0, "圆笔");
        array.append(1, "荧光笔");
        array.append(2, "马克笔");
        array.append(3, "像素笔");
        array.append(4, "炭笔");
        return array;
    }

    public class BrushAdapter extends RecyclerView.Adapter<BrushAdapter.BrushViewHolder> {


        int currentIndex = 0;
        SparseArray<String> stringSparseArray;

        public BrushAdapter(SparseArray<String> array) {
            this.stringSparseArray = array;
        }

        @Override
        public BrushAdapter.BrushViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brush_item_layout, parent, false);
            return new BrushAdapter.BrushViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BrushAdapter.BrushViewHolder holder, final int position) {
            int key = stringSparseArray.keyAt(position);
            String value = stringSparseArray.valueAt(position);
            final int resId = icons[key];
            holder.icon.setImageResource(resId);
            holder.title.setText(value);
            if (currentIndex == position) {
                holder.itemView.setBackgroundColor(Color.RED);
            } else {
                holder.itemView.setBackgroundColor(Color.BLACK);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentIndex == position) {
                        return;
                    }
                    if (position == 0) {
                        //普通画笔
                        EventBus.getDefault().post(new BrushFragmentEvent(TAG, DrawAttribute.DrawStatus.NORMAL, resId));
                    } else if (position == 1) {
                        //双重画笔
                        EventBus.getDefault().post(new BrushFragmentEvent(TAG, DrawAttribute.DrawStatus.FLUORE, resId));
                    } else if (position == 2) {
                        //马克画笔
                        EventBus.getDefault().post(new BrushFragmentEvent(TAG, DrawAttribute.DrawStatus.MARK, resId));
                    } else if (position == 3) {
                        //像素画笔
                        EventBus.getDefault().post(new BrushFragmentEvent(TAG, DrawAttribute.DrawStatus.PIXEL, resId));
                    } else if (position == 4) {
                        //炭笔
                        EventBus.getDefault().post(new BrushFragmentEvent(TAG, DrawAttribute.DrawStatus.TAN, resId));
                    } else {
                        //图片画笔
                        //EventBus.getDefault().post(new BrushFragmentEvent(TAG, DrawAttribute.DrawStatus.BITMAP, resId));
                    }
                    int lastIndex = currentIndex;
                    currentIndex = position;
                    notifyItemChanged(currentIndex);
                    if (lastIndex != -1) {
                        notifyItemChanged(lastIndex);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.stringSparseArray != null ? stringSparseArray.size() : 0;
        }

        class BrushViewHolder extends RecyclerView.ViewHolder {

            ImageView icon;
            TextView title;

            public BrushViewHolder(View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.iv_icon);
                title = itemView.findViewById(R.id.tv_title);
            }
        }
    }

}
