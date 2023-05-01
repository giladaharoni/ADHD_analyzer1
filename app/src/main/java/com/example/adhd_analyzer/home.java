package com.example.adhd_analyzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    QuizFragment quizFragment = new QuizFragment();
    SettingFragment settingFragment = new SettingFragment();
    ReportsFragment reportsFragment = new ReportsFragment();

    HomeFragment homeFragment = new HomeFragment();



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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.bottomNavigationView:
//                // Show the popup menu
//                View view = findViewById(R.id.bottomNavigationView);
//                PopupMenu popupMenu = new PopupMenu(this, view);
//                popupMenu.getMenuInflater().inflate(R.menu.navigation_menu, popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        // Handle menu item click
//                        return true;
//                    }
//                });
//                popupMenu.show();
//                return true;
//            case R.id.item0:
//                Toast.makeText(this, "item 0 selected", Toast.LENGTH_SHORT);
//                Intent intent0 = new Intent(home.this, home.class);
//                startActivity(intent0);
//                return true;
//            case R.id.item1:
//                Toast.makeText(this, "item 1 selected", Toast.LENGTH_SHORT);
//                Intent intent1 = new Intent(home.this, reports.class);
//                startActivity(intent1);
//                return true;
//            case R.id.item2:
//                Toast.makeText(this, "item 2 selected", Toast.LENGTH_SHORT);
//                Intent intent2 = new Intent(home.this, setting.class);
//                startActivity(intent2);
//                return true;
//            case R.id.item3:
//                Toast.makeText(this, "item 3 selected", Toast.LENGTH_SHORT);
//                Intent intent3 = new Intent(home.this, quiz.class);
//                startActivity(intent3);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }
}