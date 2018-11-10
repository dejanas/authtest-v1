package com.etf.ac.bg.rs.sd120456.authtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import static android.view.View.*;

public class StartFullscreenActivity extends Activity {

    private Context mContext;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_fullscreen);

        View mContentView = findViewById(R.id.fullscreen_content);
        mContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mContentView.setSystemUiVisibility(SYSTEM_UI_FLAG_LOW_PROFILE
                    | SYSTEM_UI_FLAG_FULLSCREEN
                    | SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | SYSTEM_UI_FLAG_IMMERSIVE
                    | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        mProgressBar = (ProgressBar)findViewById(R.id.startProgressBar);
        mProgressBar.setMax(3);
        mProgressBar.setVisibility(VISIBLE);

        CountDownTimer countDownTimer = new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                mProgressBar.incrementProgressBy(1);
            }

            public void onFinish() {

                finish();
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);

            }
        }.start();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


}
