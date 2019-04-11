package com.example.liusheng.painboard.event;

import com.example.liusheng.painboard.draw.DrawAttribute;

/**
 * Created by chenzhen on 2019/3/21.
 */

public class BrushFragmentEvent extends FragmentEvent {

    int resId;
    DrawAttribute.DrawStatus drawStatus;
    public BrushFragmentEvent(String tag, DrawAttribute.DrawStatus status,int id) {
        super(tag);
        this.resId = id;
        this.drawStatus = status;
    }


    public int getResId() {
        return resId;
    }

    public DrawAttribute.DrawStatus getDrawStatus() {
        return drawStatus;
    }
}
