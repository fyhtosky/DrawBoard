package com.example.liusheng.painboard.event;

/**
 * Created by chenzhen on 2019/3/21.
 */

public class BgFragmentEvent extends FragmentEvent {


    private int selectIndex;

    public BgFragmentEvent(String tag, int index) {
        super(tag);
        this.selectIndex = index;
    }

    public int getSelectIndex() {
        return selectIndex;
    }
}
