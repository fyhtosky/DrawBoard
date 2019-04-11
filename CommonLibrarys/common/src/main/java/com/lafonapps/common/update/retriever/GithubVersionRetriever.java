package com.lafonapps.common.update.retriever;

import android.os.Looper;
import android.util.Log;

import com.lafonapps.common.update.bean.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by chenjie on 2018/2/1.
 */

public class GithubVersionRetriever implements VersionRetriever {

    private static final String TAG = GithubVersionRetriever.class.getCanonicalName();

    private static final String apiBasePath = "https://api.github.com/repos";
    private static final String owner = "np20180202";
    private static final String repo = "VersionAutoUpdater";

    private String applicationId;
    private String market;

    /**
     * 设置检测的应用包名
     *
     * @param applicationId
     */
    @Override
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * 设置检测的应用商店
     *
     * @param market
     */
    @Override
    public void setMarket(String market) {
        this.market = market;
    }

    /**
     * 获取最新版本信息
     *
     * @param listener
     */
    @Override
    public void retrieve(final VersionRetrieverListener listener) {

        // TODO: 2018/2/1  将使用Thread修改为使用AsyncTask
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {

                    String versionFilePath = "versions/" + applicationId + ".json";
                    String requestURLPath = apiBasePath + "/" + owner + "/" + repo + "/contents/" + versionFilePath;
                    String githubJSONString = retriveStringFromURL(requestURLPath);
                    Log.d(TAG, "File content json from github = " + githubJSONString);

                    JSONObject jsonObject = new JSONObject(githubJSONString);

                    String downloadURL = jsonObject.getString("download_url");
                    String versionJSONString = retriveStringFromURL(downloadURL);
                    Log.d(TAG, "versions_for_all_app = " + versionJSONString);

                    JSONObject fullJSON = new JSONObject(versionJSONString);
                    JSONObject versionJSON = fullJSON.getJSONObject(market);

                    Version version = new Version();
                    version.setVersionCode(versionJSON.getInt("version_code"));
                    version.setVersionName(versionJSON.getString("version_name"));
                    version.setUpdatePolicy(versionJSON.getInt("update_policy"));
                    JSONArray jsonArray = versionJSON.getJSONArray("update_logs");
                    List<String> logs = new ArrayList<String>();
                    for (int i=0;i<jsonArray.length();i++){
                        logs.add(jsonArray.getString(i));
                    }
                    version.setUpdateLogs(logs);

                    callback(version, null, listener);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback(null, e, listener);
                } catch (IOException e) {
                    e.printStackTrace();
                    callback(null, e, listener);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback(null, e, listener);
                }

                Looper.loop();
            }
        }).start();

    }

    private void callback(Version version, Exception e, VersionRetrieverListener listener) {
        if (listener != null) {
            listener.onRetrieved(version, e);
        }
    }

    private String retriveStringFromURL(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
//        Log.e("response",response.body().toString());
        return response.body().string();
//        return "";
    }
}
