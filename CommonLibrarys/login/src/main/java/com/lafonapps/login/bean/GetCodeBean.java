package com.lafonapps.login.bean;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/10/09
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class GetCodeBean {

    /**
     * succ : true
     * statusCode : 200
     * msg : 短信发送成功
     * data : 000000
     * time : 1539072349424
     */

    private boolean succ;
    private int statusCode;
    private String msg;
    private String data;
    private long time;

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
