package com.lafonapps.common.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.lafonapps.common.CommonConfig;
import com.lafonapps.common.R;
import com.lafonapps.common.update.bean.Version;
import com.lafonapps.common.update.retriever.GithubVersionRetriever;
import com.lafonapps.common.update.retriever.VersionRetriever;
import com.lafonapps.common.util.Common;

/**
 * Created by chenjie on 2018/2/1.
 * <p>
 * 使用github保存版本文件
 * github api 文档：https://developer.github.com/v3/repos/contents/
 */
public class VersionAutoUpdater {

    private static final String TAG = VersionAutoUpdater.class.getCanonicalName();
    private VersionRetriever retriever;
    private boolean retrieved = false;
    private boolean hasNewVersion = false;
    private Version newVersion ;
    public static VersionAutoUpdater defaultUpdater ;

    public   VersionAutoUpdater() {
        retriever = new GithubVersionRetriever();
        retriever.setApplicationId(Common.getSharedApplication().getPackageName());
        retriever.setMarket(CommonConfig.sharedCommonConfig.market);
    }

    /**
     * 自动检测更新，整个应用运行期间只检测一次
     * @param activity
     */
    public static void autoUpdateOnce(Activity activity) {
        synchronized (VersionAutoUpdater.class) {
            if (defaultUpdater == null) {
                defaultUpdater = new VersionAutoUpdater();
            }
            if (!defaultUpdater.retrieved) {
                defaultUpdater.retrieved = true;
                defaultUpdater.autoUpdate(activity);
            }
        }
    }

    /**
     * 是否有新版本
     * @return
     */
    public static boolean hasNewVersion() {
        return defaultUpdater.hasNewVersion;
    }

    /**
     * 最新版本信息。建议调用此方法之前先调用hasNewVersion()方法判断一下
     * @return 如果没有检测到新版本，则返回null。只有服务器配置的版本比当前版本高时，才会有返回值
     */
    public static Version getNewVersion() {
        return defaultUpdater.newVersion;
    }

    /**
     * 获取最新版本信息, 并根据返回的更新策略决定是否弹出升级提示窗
     */
    public void autoUpdate(final Activity activity) {
        retriever.retrieve(new VersionRetriever.VersionRetrieverListener() {
            @Override
            public void onRetrieved(final Version version, Exception e) {

                if (version != null) {
                    if (Common.getAppVersionCode() < version.getVersionCode()) {
                        Log.d(TAG, "New version retrieved:" + version.getVersionName());
                        hasNewVersion = true;
                        newVersion = version;
                        if (version.getUpdatePolicy() == Version.UPDATE_POLICY_SILENCE) {
                            Log.d(TAG, "New version detected, do nothing");
                        } else if (version.getUpdatePolicy() == Version.UPDATE_POLICY_PROMOTE) {
                            showUpdateDialog(activity, version);
                        } else if (version.getUpdatePolicy() == Version.UPDATE_POLICY_FORCE) {
                            showForceUpdateDialog(activity, version);
                        }
                    } else if (Common.getAppVersionCode() == version.getVersionCode()) {
                        Log.d(TAG, "Current is the last version, skip");
                    } else {
                        Log.d(TAG, "Current version grate than market.");
                    }
                } else {
                    Log.d(TAG, "Version retrieved failed: " + e.getLocalizedMessage());
                }
            }
        });
    }

    /**
     * 展示升级提示框。用户可选择升级和取消。点击升级后打开应用商店并跳转到当前应用详情界面
     *
     * @param context
     */
    private void showUpdateDialog(Context context, Version version) {
        showDialog(context, version, true);
    }

    /**
     * 展示升级提示窗。用户只能点击升级，无法取消。点击升级后打开应用商店并跳转到当前应用详情界面
     */
    private void showForceUpdateDialog(Context context, Version version) {
        showDialog(context, version, false);
    }

    /**
     * 展示升级提示窗。
     *
     * @param context    用于弹出提示框的context
     * @param version    检测到的版本信息
     * @param cancelable 是否可以选择取消
     */
    private void showDialog(final Context context, Version version, boolean cancelable) {

        StringBuilder logsBuilder = new StringBuilder();
        if (version.getUpdateLogs().size() > 0) {
            logsBuilder.append("更新内容：\n");
            for (int i = 0; i < version.getUpdateLogs().size(); i++) {
                String log = version.getUpdateLogs().get(i);
                logsBuilder.append((i + 1) + "." + log + "\n");
            }
        }
        String logs = logsBuilder.toString();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("发现新版本")
                .setMessage(logs)
                .setCancelable(cancelable)
                .setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openUpdatePage(context);
                    }
                });
        if (cancelable) {
            alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        alertDialog.show();
    }

    /**
     * 打开升级页面
     *
     * @param context
     */
    private void openUpdatePage(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarketIntent = new Intent(Intent.ACTION_VIEW, uri);
        goToMarketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarketIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.no_market_install_message), Toast.LENGTH_SHORT).show();
        }
    }
}
