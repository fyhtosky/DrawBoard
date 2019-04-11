package com.lafonapps.login.bean;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/09/29
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class RegisterBean {
    /**
     * succ : true
     * statusCode : 200
     * msg : 恭喜您,注册成功!
     * data : {"isR":false,"accessToken":"17b8c0fa490a4afc8887665788ab5496","userId":"188719875201545720662660"}
     * time : 1545720662683
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
         * isR : false
         * accessToken : 17b8c0fa490a4afc8887665788ab5496
         * userId : 188719875201545720662660
         */

        private boolean isR;
        private String accessToken;
        private String userId;
        private String timeExpire;
        private int expirationTime;

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
