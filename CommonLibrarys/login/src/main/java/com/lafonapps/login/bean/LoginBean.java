package com.lafonapps.login.bean;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/10/9
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class LoginBean {


    /**
     * succ : true
     * statusCode : 200
     * msg : 成功
     * data : {"timeExpire":"2018-11-09 00:00:00","expirationTime":43783,"isR":true,"accessToken":"df752596ce2644be869d4a470c4e9747","userId":"188719875201538984215308"}
     * time : 1539065777839
     */

    private boolean succ;
    private int statusCode;
    private String msg;
    private DataBean data;
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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static class DataBean {
        /**
         * timeExpire : 2018-11-09 00:00:00
         * expirationTime : 43783
         * isR : true
         * accessToken : df752596ce2644be869d4a470c4e9747
         * userId : 188719875201538984215308
         */

        private String timeExpire;
        private int expirationTime;
        private boolean isR;
        private String accessToken;
        private String userId;

        public String getTimeExpire() {
            return timeExpire;
        }

        public void setTimeExpire(String timeExpire) {
            this.timeExpire = timeExpire;
        }

        public int getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(int expirationTime) {
            this.expirationTime = expirationTime;
        }

        public boolean isIsR() {
            return isR;
        }

        public void setIsR(boolean isR) {
            this.isR = isR;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
