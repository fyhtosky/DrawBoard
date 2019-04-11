package com.example.liusheng.painboard.Tools;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by chenzhen on 2019/2/20.
 */

public class ItemDecoration extends RecyclerView.ItemDecoration {

    int margin = 20;

    public ItemDecoration() {
    }

    public ItemDecoration(int margin) {
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = margin;
        int childIndex = parent.getChildAdapterPosition(view);
        if (childIndex == 0 || childIndex == 1){
            outRect.top = margin;
        }if (childIndex % 2 == 0){
            outRect.left = margin;
        }else {
            outRect.left = margin;
            outRect.right = margin;
        }
    }
}
