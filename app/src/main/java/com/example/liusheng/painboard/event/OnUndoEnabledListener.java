package com.example.liusheng.painboard.event;

/**
 * Created by chenzhen on 2019/2/28.
 */

public interface OnUndoEnabledListener {

    void onUndoEnabled(boolean enabled);//撤销是否可用
    void onRedoEnabled(boolean enabled);//反撤销是否可用
    void onAllDisabled();//都不可用可用
}
