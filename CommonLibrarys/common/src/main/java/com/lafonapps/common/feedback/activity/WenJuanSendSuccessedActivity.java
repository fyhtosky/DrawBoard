package com.lafonapps.common.feedback.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lafonapps.common.R;
import com.lafonapps.common.feedback.KeyInformation;


public class WenJuanSendSuccessedActivity extends RBActivity implements View.OnClickListener {

    private TextView titleTV;
    private TextView closeTV;
    private TextView feedbackSuccessTV;
    private TextView feedbackSuccessSubTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_successful_layout);

        titleTV = (TextView) findViewById(R.id.fd_success_title);
        closeTV = (TextView) findViewById(R.id.fd_success_close);
        feedbackSuccessTV = (TextView) findViewById(R.id.fd_success_t1);

        feedbackSuccessSubTV = (TextView) findViewById(R.id.fd_success_t2);

        Intent intent = getIntent();
        boolean isWenjuan = intent.getBooleanExtra("isWenjuan", false);

        if (isWenjuan) {
            titleTV.setVisibility(View.GONE);
            feedbackSuccessTV.setText(getResources().getString(R.string.commit_success));
            feedbackSuccessSubTV.setText(getResources().getString(R.string.wenjuan_commit_subtitle));
        }else {
            titleTV.setText(getResources().getString(R.string.feedback_text));
            closeTV.setText(getResources().getString(R.string.close));
            feedbackSuccessTV.setText(getResources().getString(R.string.feedback_success));
            String subtitle = String.format(getResources().getString(R.string.feedback_success_subtitle), KeyInformation.getInstance(this).getAppName());
            feedbackSuccessSubTV.setText(subtitle);
        }

    }

    @Override
    public void onClick(View v) {
        finish();
    }

}
