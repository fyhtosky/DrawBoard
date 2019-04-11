package com.example.liusheng.painboard.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.View.CustomView;
import com.example.liusheng.painboard.View.SpaceItemDecoration;
import com.example.liusheng.painboard.draw.ColorUtils;
import com.example.liusheng.painboard.event.ColorFragmentEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * 画笔颜色选择
 * Created by chenzhen on 2019/3/21.
 */

public class ColorSelectFragment extends BaseFragment {


    public static final String TAG = ColorSelectFragment.class.getSimpleName();


    RecyclerView mRecyclerView;


    public static ColorSelectFragment newInstance(String tag) {
        ColorSelectFragment fragment = new ColorSelectFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int generateLayoutId() {
        return R.layout.fragment_color_select;
    }

    @Override
    protected void init() {
        mRecyclerView = rootView.findViewById(R.id.colorlist_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //mRecyclerView.addItemDecoration(new SpaceItemDecoration(5, "recycle1"));
        final int offests = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, getResources().getDisplayMetrics());
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = offests;
                outRect.bottom = offests;
            }
        });
        ColorAdapter adapter = new ColorAdapter(getColors());
        mRecyclerView.setAdapter(adapter);
    }

    public List<String> getColors() {
        return Arrays.asList(
                "#FFC0CB", "#DC143C", "#DB7093",  "#FF1493", "#C71585", "#DA70D6",
                "#EE82EE", "#FF00FF", "#8B008B",  "#BA55D3", "#9932CC",
                "#4B0082", "#8A2BE2", "#9370DB", "#6A5ACD", "#483D8B", "#191970",
                "#00008B", "#4169E1", "#6495ED", "#778899",  "#1E90FF", "#4682B4",
                "#87CEEB", "#00BFFF", "#5F9EA0", "#AFEEEE", "#00FFFF",  "#00CED1",
                "#008080", "#48D1CC", "#20B2AA",  "#7FFFAA", "#00FA9A",   "#2E8B57",
                "#F0FFF0",  "#8FBC8F", "#32CD32", "#00FF00", "#228B22", "#008000",
                "#7FFF00",  "#ADFF2F", "#FFFF00", "#808000", "#BDB76B","#F0E68C",
                "#FFD700", "#DAA520",  "#FFEBCD", "#FAEBD7",
                "#D2B48C", "#DEB887",  "#FAF0E6", "#CD853F",  "#F4A460",
                "#8B4513", "#A0522D", "#FFA07A",   "#E9967A",  "#FFE4E1",
                "#BC8F8F", "#FF0000", "#A52A2A",  "#800000", "#DCDCDC",
                 "#C0C0C0", "#A9A9A9", "#808080", "#696969", "#000000", "#000000"
        );
    }


    private class ColorAdapter extends RecyclerView.Adapter<ColorSelectFragment.ColorAdapter.ColorViewHolder> {

        List<String> colorList;
        int currentIndex = 0;

        public ColorAdapter(List<String> colorList) {
            this.colorList = colorList;
        }

        @Override
        public ColorSelectFragment.ColorAdapter.ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pen_color_imageview, parent, false);
            return new ColorSelectFragment.ColorAdapter.ColorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ColorSelectFragment.ColorAdapter.ColorViewHolder holder, final int position) {

            holder.customView.setColor(Color.BLACK);
            if (position == 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.caise_icon);
                holder.customView.setImage(bitmap);
            } else {
                String color = colorList.get(position - 1);
                holder.customView.setColor(Color.parseColor(color));
            }
            if (currentIndex == position) {
                holder.customView.setSelected(true);
            } else {
                holder.customView.setSelected(false);
            }
            holder.customView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //onColorItemClickListener.colorClick(view, position);
                    int lastIndex = currentIndex;
                    currentIndex = position;
                    notifyItemChanged(currentIndex);
                    if (lastIndex != -1) {
                        notifyItemChanged(lastIndex);
                    }
                    if (position == 0) {
                        ColorUtils.colorRandom = true;
                    } else {
                        ColorUtils.colorRandom = false;
                        ColorUtils.color = Color.parseColor(colorList.get(position - 1));
                    }
                    EventBus.getDefault().post(new ColorFragmentEvent(TAG, position));

                }
            });
        }

        @Override
        public int getItemCount() {
            return this.colorList.size();
        }

        class ColorViewHolder extends RecyclerView.ViewHolder {

            CustomView customView;

            public ColorViewHolder(View itemView) {
                super(itemView);

                customView = itemView.findViewById(R.id.color_imageView);
                customView.getBackground().setAlpha(0);
            }
        }
    }
}
