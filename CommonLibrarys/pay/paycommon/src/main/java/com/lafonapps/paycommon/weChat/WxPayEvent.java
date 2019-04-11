package com.lafonapps.paycommon.weChat;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/07/25
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class WxPayEvent {
    private int  wxResultCode;
    public WxPayEvent(int wxResultCode){
        this.wxResultCode=wxResultCode;
    }

    public int getWxResultCode() {
        return wxResultCode;
    }

    public void setWxResultCode(int wxResultCode) {
        this.wxResultCode = wxResultCode;
    }
}
