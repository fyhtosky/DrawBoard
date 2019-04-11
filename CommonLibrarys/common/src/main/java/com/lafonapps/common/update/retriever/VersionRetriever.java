package com.lafonapps.common.update.retriever;

import com.lafonapps.common.update.bean.Version;

/**
 * Created by chenjie on 2018/2/1.
 */

public interface VersionRetriever {

    /**
     * 设置检测的应用包名
     * @param applicationId
     */
    void setApplicationId(String applicationId);

    /**
     * 设置检测的应用商店
     * @param market
     */
    void setMarket(String market);

    /**
     * 获取最新版本信息
     * @param listener
     */
    void retrieve(VersionRetrieverListener listener);

    interface VersionRetrieverListener {

        /**
         * 获取完成回调。成功失败都调用此方法。
         * @param version 获取成功会返回服务器上记录的最新版本号及相关信息。获取失败返回null
         * @param e 获取失败才会有值，获取成功为null
         */
        void onRetrieved(Version version, Exception e);

    }

}
