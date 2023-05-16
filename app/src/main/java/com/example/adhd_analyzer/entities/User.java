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
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public void setPassword(String password){
        this.Password = password;
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
