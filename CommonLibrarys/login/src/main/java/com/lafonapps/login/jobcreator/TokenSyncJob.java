package com.lafonapps.login.jobcreator;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.lafonapps.login.utils.ViewUtils;

/**
 * <pre>
 *     function
 *     author : leishangming
 *     time   : 2018/10/31
 *     e-mail : shangming.lei@lafonapps.com
 *     简 书  : https://www.jianshu.com/u/644036b17b6f
 *     github : https://github.com/lsmloveu
 * </pre>
 */

public class TokenSyncJob extends Job {

    public static final String TAG = "TokenSyncJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        ViewUtils.toGetTakenImpl();
        return Result.SUCCESS;
    }

    public static void runTokenJobImmediately() {
        new JobRequest.Builder(TokenSyncJob.TAG)
                .startNow()
                .build()
                .schedule();
    }
}
