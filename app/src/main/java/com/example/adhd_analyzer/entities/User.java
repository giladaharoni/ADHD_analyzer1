package com.example.adhd_analyzer.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    private String Password;
    private String userName;
    private String fullName;

    public User(String password, String userName, String fullName) {
        Password = password;
        this.userName = userName;
        this.fullName = fullName;
    }

    public String getPassword() {
        return Password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }
}
