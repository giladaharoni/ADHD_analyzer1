package com.example.adhd_analyzer;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adhd_analyzer.logger_sensors.ModuleDB;
import com.example.adhd_analyzer.logger_sensors.SensorsRecordsService;
import com.example.adhd_analyzer.viewmodels.ButtonStateViewModel;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    private final int LOCATION_CODE =1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        buttonStateViewModel= new ViewModelProvider(this).get(ButtonStateViewModel.class);

    }
    ButtonStateViewModel  buttonStateViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        Button tracking = view.findViewById(R.id.truck_button);
        buttonStateViewModel.getButtonClickableState().observe(this.getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isClickable) {
                Context context = getContext();
                if (context != null) {
                    Resources.Theme theme = context.getTheme();
                    tracking.setEnabled(isClickable);
                    if (isClickable){
                        Drawable drawable = getResources().getDrawable(R.drawable.circular_button_background,theme);
                        tracking.setBackground(drawable);
                    } else {
                        Drawable drawable = getResources().getDrawable(R.drawable.circular_button_background_disable,theme);
                        tracking.setBackground(drawable);
                    }
                }
            }
        });
        initButton(tracking);

        TextView hello = view.findViewById(R.id.hello_user);
        //SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
        String fullname = ModuleDB.getUserDetailsDB(getContext()).userDao().index().get(0).getFullName();
        String s_hello = "hello " + fullname +"!";
        hello.setText(s_hello);

        return view;
    }

    /**
     * init the button of tracking. sets time handlers, color change etc.
     * @param tracking the tracking button.
     */
    private void initButton(Button tracking){
        if (isMyServiceRunning(SensorsRecordsService.class)){
            tracking.setText(R.string.stop_tracking);
        }
        tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMyServiceRunning(SensorsRecordsService.class)){
                    getActivity().stopService(new Intent(view.getContext(),SensorsRecordsService.class));
                    tracking.setText(R.string.start_tracking);

                } else{
                    checkRequest();
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(new Intent(view.getContext(), SensorsRecordsService.class));
                    }
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
    }

    /**
     * check for permission access to the location (GPS in background).
     * if it doesn't have this, it open a system dialog for setting the permission.
     */
    private void checkRequest() {
        if ((ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this.getContext()
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return;
        } else {
            int code = 0;
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                new AlertDialog.Builder(this.getContext()).setTitle("permission needed")
                        .setMessage("please accept location access")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},LOCATION_CODE);

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();

            } else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},LOCATION_CODE);

            }
        }
    }


    /***
     * Check if the chosen serviceClass is running as  a background service.
     * @param serviceClass the chosen class.
     * @return true if it run.
     */
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