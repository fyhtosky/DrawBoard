package com.lafonapps.login.bean;

import com.google.gson.annotations.SerializedName;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/09/18
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class PayOrderBean {


    /**
     * succ : true
     * statusCode : 200
     * data : {"package":"Sign=WXPay","appid":"wxf8bb4b1a4c17d359","sign":"EEF3D98A4AF2013863F518295D419D95","partnerid":"1509189221","prepayid":"wx191008550746076ce3eace1a3062377511","noncestr":"sh0eynLUGe6na474"}
     * time : 1537322936161
     */

    private boolean succ;
    private int statusCode;
    private DataBean data;
    private long time;
    private String msg;

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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * package : Sign=WXPay
         * appid : wxf8bb4b1a4c17d359
         * sign : EEF3D98A4AF2013863F518295D419D95
         * partnerid : 1509189221
         * prepayid : wx191008550746076ce3eace1a3062377511
         * noncestr : sh0eynLUGe6na474
         */

        @SerializedName("package")
        private String packageX;
        private String appid;
        private String sign;
        private String partnerid;
        private String prepayid;
        private String noncestr;
        private String timestamp;

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
