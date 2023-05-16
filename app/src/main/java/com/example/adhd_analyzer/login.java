package com.example.adhd_analyzer;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adhd_analyzer.api.UserApi;
import com.example.adhd_analyzer.api.WebServiceApi;
import com.example.adhd_analyzer.entities.User;

public class login extends AppCompatActivity {

    public static User theUser = new User("", "", "");
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;

//    public String getUserName(){
//        return this.theUser.getUserName();
//    }
//    public String getPassword(){
//        return this.theUser.getPassword();
//    }
//    public String getFullName(){
//        return this.theUser.getFullName();
//    }
//    private void setTheUser(String password, String userName, String fullName){
//        this.theUser.setUserName(userName);
//        this.theUser.setPassword(password);
//        this.theUser.setFullName(fullName);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsernameEditText = findViewById(R.id.username_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mLoginButton = findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                //this is just for see the next page without logic
//                Intent intent = new Intent(login.this, home.class);
//                startActivity(intent);

                // Make HTTP GET request to login
                WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
                Call<User> call = api.login(username, password);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            // User registered successfully
                            User user = response.body();
                            Intent intent = new Intent(login.this, home.class);
                            String fullName = user.getFullName();

                            theUser.setFullName(fullName);
                            theUser.setUserName(username);
                            theUser.setPassword(password);


                            if(response.code() == 400){
                                Toast.makeText(login.this, "wrong username or password", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(intent);
                            }
                        } else {
                            // Registration failed
                            Toast.makeText(login.this, "login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // Error occurred while making HTTP request
                        Toast.makeText(login.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //back button
        Button back_button = findViewById(R.id.back_from_login_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this ,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
