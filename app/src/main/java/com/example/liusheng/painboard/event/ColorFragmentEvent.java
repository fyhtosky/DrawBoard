package com.example.liusheng.painboard.event;

/**
 * Created by chenzhen on 2019/3/21.
 */

public class ColorFragmentEvent extends FragmentEvent {

    private int selectIndex;

    public ColorFragmentEvent(String tag, int index) {
        super(tag);
        this.selectIndex = index;
    }

    public int getSelectIndex() {
        return selectIndex;
    }
}
