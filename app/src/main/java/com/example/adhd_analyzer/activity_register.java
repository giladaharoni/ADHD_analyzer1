package com.example.adhd_analyzer;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adhd_analyzer.api.UserApi;
import com.example.adhd_analyzer.api.WebServiceApi;
import com.example.adhd_analyzer.entities.User;

public class activity_register extends AppCompatActivity {
    private EditText etUsername, etFullName, etPassword, etPasswordVerify;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //the details the user writing for registering
        etUsername = findViewById(R.id.username_edit_text_user_name);
        etFullName = findViewById(R.id.username_edit_text_full_name);
        etPassword = findViewById(R.id.username_edit_text_password);
        etPasswordVerify = findViewById(R.id.password_edit_text_verify_password);
        btnRegister = findViewById(R.id.register_button);

        //back button
        Button back_button = findViewById(R.id.back_from_register_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_register.this ,MainActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String fullName = etFullName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Make HTTP GET request to register new user
                WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
                Call<User> call = api.register(username, fullName, password);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            // User registered successfully
                            User user = response.body();
                            Intent intent = new Intent(activity_register.this, home.class);
                            intent.putExtra("username", username);
                            intent.putExtra("fullname", fullName);
                            intent.putExtra("password", password);
                            if(response.code() == 400){
                                Toast.makeText(activity_register.this, "This username is already use", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(intent);
                            }
                        } else {
                            // Registration failed
                            Toast.makeText(activity_register.this, "Registration failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // Error occurred while making HTTP request
                        Toast.makeText(activity_register.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}


