package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.headset.BoardingRemindEvent;
import com.example.myapplication.headset.HeadsetStatusInspector;
import com.example.myapplication.headset.RemindEventHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeadsetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeadsetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView mTvStatus;
    private HeadsetStatusInspector mHeadsetStatusInspector = new HeadsetStatusInspector();
    private RemindEventHandler mRemindEventHandler = new RemindEventHandler();

    public HeadsetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HeadsetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HeadsetFragment newInstance(String param1, String param2) {
        HeadsetFragment fragment = new HeadsetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_headset, container, false);
    }

    private Handler mHandler = new Handler();
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvStatus = view.findViewById(R.id.tv_check_status);
        view.findViewById(R.id.btn_headset).setOnClickListener(v -> {
            boolean isHeadsetInUse = mHeadsetStatusInspector.isHeadsetEnable();
            mTvStatus.setText("耳机状态:" + isHeadsetInUse);
        });
        view.findViewById(R.id.btn_call).setOnClickListener(v -> {
            int state = mHeadsetStatusInspector.checkPhoneState();
            mTvStatus.setText("通话状态：" + state);
        });
        view.findViewById(R.id.btn_net_call).setOnClickListener(v -> {
            int mode = mHeadsetStatusInspector.checkNetCall();
            mTvStatus.setText("网络电话mode:" + mode);
        });
        view.findViewById(R.id.btn_task_execute).setOnClickListener(v ->
            mHandler.postDelayed(() -> mRemindEventHandler.postRemindEvent(new BoardingRemindEvent()),8000)
        );
    }
}