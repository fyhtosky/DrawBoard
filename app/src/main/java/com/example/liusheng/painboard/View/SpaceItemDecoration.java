package com.example.liusheng.painboard.View;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by 王世坚 on 2018/3/22.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    int mSpace;
    String recycle;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        if (parent.getChildAdapterPosition(view) == 0 && recycle.equals("recycle1")){
            outRect.top = mSpace;
        }
    }

    public SpaceItemDecoration(int mSpace,String recycle) {
        this.mSpace = mSpace;
        this.recycle = recycle;
    }
}
