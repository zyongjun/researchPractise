package com.example.myapplication;

import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.download.DownloadProcessor;
import com.example.myapplication.download.InstallProcessor;

import java.io.File;

public class FirstFragment extends Fragment {
    private static final String TAG = "FirstFragment";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.d(TAG, "onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        Log.d(TAG, "onCreateView: ==");
        Log.d(TAG, "onCreateView: aaa");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//                int c = getTestA(10,2);
//                int d = getTestB(2,5);
                DownloadProcessor.getInstance().download("");
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        InstallProcessor installProcessor = new InstallProcessor();
//                        File parentFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                        File apkFile = new File(parentFile,DownloadProcessor.PACKAGE_NAME_UMETRIP+".apk");
//                        installProcessor.install(apkFile);
//                    }
//                }).start();
            }
        });
        view.findViewById(R.id.btn_notification_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    private int getTestA(int a, int b) {
        int c = a + b;
        return c + c * 2;
    }

    private int getTestB(int a, int b) {
        int c = a + b;
        return c + c * 2;
    }
}