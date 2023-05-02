package com.example.adhd_analyzer.user;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserDetails {
    @PrimaryKey()
    private long id;
    private String fullName;
    private String userName;
    private String password;

    public UserDetails(long id, String fullName, String userName, String password) {
        this.id = id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
