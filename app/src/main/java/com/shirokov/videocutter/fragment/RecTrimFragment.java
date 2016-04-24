package com.shirokov.videocutter.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.shirokov.videocutter.R;
import com.shirokov.videocutter.util.DialogUtils;
import com.shirokov.videocutter.util.FFmpegUtils;
import com.shirokov.videocutter.util.FragmentUtils;

import java.io.File;

public class RecTrimFragment extends Fragment {
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final String TAG = "VideoCutterLastVideo";
    private static final int VIDEO_DURATION = 60;

    private Uri mPath;
    private ProgressDialog mProgressDialog;
    private boolean isTriming;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rec_trim, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);
        if (isTriming) {
            mProgressDialog.setMessage("Processing...");
            mProgressDialog.show();
        }
        View view = getView();

        view.findViewById(R.id.cut_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trimVideo(mPath);
            }
        });

        view.findViewById(R.id.rec_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void trimVideo(Uri path) {
        if (path == null) {
            DialogUtils.showAlert(getContext(), R.string.error, R.string.video_is_not_recorded);
            return;
        }
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.show();
        isTriming = true;
        FFmpegUtils.trimVideo(getContext(), path, VIDEO_DURATION, new ExecuteBinaryResponseHandler() {
            @Override
            public void onFailure(String s) {
                mProgressDialog.dismiss();
                DialogUtils.showAlert(getContext(), R.string.error, R.string.trim_error);
            }

            @Override
            public void onSuccess(String s) {
                mProgressDialog.dismiss();
                DialogUtils.showAlert(getContext(), R.string.app_name, R.string.trim_success);
                FragmentUtils.addFragment(getFragmentManager(), VideoListFragment.class);
            }

            @Override
            public void onFinish() {
                isTriming = false;
                mProgressDialog.dismiss();
                logLastVideo();
            }
        });
    }

    private void logLastVideo() {
        File dir = new File(FFmpegUtils.getVideoCutterOutputDir());
        if (dir.exists()) {
            File[] list = dir.listFiles();
            if (list != null && list.length > 0) {
                Log.d(TAG, "" + list[list.length - 1].getName());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            mPath = data.getData();
        }
    }
}
