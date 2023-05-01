package com.example.adhd_analyzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.adhd_analyzer.logger_sensors.SensorsRecordsService;

public class home extends AppCompatActivity {
    boolean isTracked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        isTracked = false;
        Button startTrack = findViewById(R.id.truck_button);
        startTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTracked){
                    startService(new Intent(view.getContext(),SensorsRecordsService.class));
                }

            }
        });

    }


}