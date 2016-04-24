package com.shirokov.videocutter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.shirokov.videocutter.fragment.RecTrimFragment;
import com.shirokov.videocutter.util.FragmentUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFFMpegBinary();
        if (savedInstanceState == null) {
            FragmentUtils.setFragment(getSupportFragmentManager(), RecTrimFragment.class);
        }
    }


    private void loadFFMpegBinary() {
        try {
            FFmpeg.getInstance(this).loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
//                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
//            showUnsupportedExceptionDialog();
        }
    }


}
