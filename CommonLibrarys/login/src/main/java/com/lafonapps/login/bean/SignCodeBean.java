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

public class SignCodeBean {

    /**
     * succ : false
     * statusCode : 200
     * msg : 接口请求异常
     * time : 1539072713722
     */

    private boolean succ;
    private int statusCode;
    private String msg;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
