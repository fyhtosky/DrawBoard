package com.lafonapps.login.jobcreator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

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

public class TokenJobCreator implements JobCreator {

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case TokenSyncJob.TAG:
                return new TokenSyncJob();
            default:
                return null;
        }
    }
}
