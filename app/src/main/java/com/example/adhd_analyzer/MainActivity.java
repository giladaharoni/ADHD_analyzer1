package com.example.adhd_analyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.adhd_analyzer.entities.User;
import com.example.adhd_analyzer.logger_sensors.ModuleDB;
import com.example.adhd_analyzer.user.UserDao;
import com.example.adhd_analyzer.user.UserDetails;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login_button = findViewById(R.id.button_login);
        UserDao dao = ModuleDB.getUserDetailsDB(this).userDao();
        try {
            UserDetails savedUser = dao.index().get(0);
            login.theUser = new User(savedUser.getPassword(),savedUser.getUserName(),savedUser.getFullName());
            Intent intent = new Intent(MainActivity.this, home.class);
            startActivity(intent);

        } catch (Exception ignored){

        }
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,login.class);
                startActivity(intent);
            }
        });
        Button register_button = findViewById(R.id.button_register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this,activity_register.class);
                startActivity(intent2);
            }
        });
    }

}