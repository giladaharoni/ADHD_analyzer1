package com.example.adhd_analyzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.adhd_analyzer.logger_sensors.SensorsRecordsService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    QuizFragment quizFragment = new QuizFragment();
    SettingFragment settingFragment = new SettingFragment();
    ReportsFragment reportsFragment = new ReportsFragment();

    HomeFragment homeFragment = new HomeFragment();

    public int PermissionCode = 1;



    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment,homeFragment)
                        .commit();
                return true;
            case R.id.quiz:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, quizFragment)
                        .commit();
                return true;

            case R.id.reports:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, reportsFragment)
                        .commit();
                return true;

            case R.id.setting:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, settingFragment)
                        .commit();
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }
}