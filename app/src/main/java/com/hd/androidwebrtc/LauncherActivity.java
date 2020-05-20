package com.hd.androidwebrtc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.myhexaville.androidwebrtc.R;
import com.hd.androidwebrtc.app_rtc_sample.main.AppRTCMainActivity;
import com.hd.androidwebrtc.rtc_test.CameraRenderActivity;
import com.hd.androidwebrtc.rtc_test.CompleteActivity;
import com.hd.androidwebrtc.rtc_test.DataChannelActivity;
import com.hd.androidwebrtc.rtc_test.MediaStreamActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void openAppRTCActivity(View view) {
        startActivity(new Intent(this, AppRTCMainActivity.class));
    }

    public void openSampleActivity(View view) {
        startActivity(new Intent(this, CameraRenderActivity.class));
    }

    public void openSamplePeerConnectionActivity(View view) {
        startActivity(new Intent(this, MediaStreamActivity.class));
    }

    public void openSampleDataChannelActivity(View view) {
        startActivity(new Intent(this, DataChannelActivity.class));
    }

    public void openSampleSocketActivity(View view) {
        startActivity(new Intent(this, CompleteActivity.class));

    }
}
