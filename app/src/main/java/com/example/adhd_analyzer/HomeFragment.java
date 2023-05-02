package com.example.adhd_analyzer;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.adhd_analyzer.logger_sensors.SensorsRecordsService;
import com.example.adhd_analyzer.viewmodels.ButtonStateViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        Button tracking = view.findViewById(R.id.truck_button);
        ButtonStateViewModel  buttonStateViewModel= new ViewModelProvider(this).get(ButtonStateViewModel.class);
        buttonStateViewModel.getButtonClickableState().observe(this.getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isClickable) {
                tracking.setEnabled(isClickable);
            }
        });
        tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMyServiceRunning(SensorsRecordsService.class)){
                    getActivity().stopService(new Intent(view.getContext(),SensorsRecordsService.class));
                    tracking.setText(R.string.start_tracking);

                } else{
                    getActivity().startService(new Intent(view.getContext(), SensorsRecordsService.class));
                    tracking.setText(R.string.stop_tracking);
                    buttonStateViewModel.setButtonClickableState(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            buttonStateViewModel.setButtonClickableState(true);
                        }
                    },10*1000);
                }
            }
        });
        return view;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}