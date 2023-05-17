package com.example.adhd_analyzer.user;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserDetails {

    private String fullName;
    @PrimaryKey()
    @NonNull
    private String userName;
    private String password;

    public UserDetails(String fullName, @NonNull String userName, String password) {
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
