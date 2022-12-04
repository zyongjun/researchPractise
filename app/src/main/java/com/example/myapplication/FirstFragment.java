package com.example.myapplication;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.download.DownloadProcessor;
import com.example.myapplication.hook.Hooker;
import com.example.myapplication.plugin.impl.IBaseImpl;

public class FirstFragment extends Fragment {
    private static final String TAG = "FirstFragment";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_download).setOnClickListener(view1 -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_downloadManagerFragment);
//            DownloadProcessor.getInstance().download("");
        });
        view.findViewById(R.id.btn_notification_test).setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment));
        view.findViewById(R.id.btn_headset).setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_headsetFragment);
        });
        IBaseImpl base = new IBaseImpl();
        view.findViewById(R.id.btn_hook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = base.getFolderIndex();
                Log.i(TAG, "onClick: index" + index);
            }
        });
    }
}