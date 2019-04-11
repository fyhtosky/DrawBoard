package com.example.liusheng.painboard.event;

import com.example.liusheng.painboard.draw.DrawAttribute;

/**
 * Created by chenzhen on 2019/3/21.
 */

public class ShapeFragmentEvent extends FragmentEvent {

    private DrawAttribute.DrawStatus drawStatus;

    public ShapeFragmentEvent(String tag, DrawAttribute.DrawStatus status) {
        super(tag);
        this.drawStatus = status;
    }

    public DrawAttribute.DrawStatus getDrawStatus() {
        return drawStatus;
    }
}
