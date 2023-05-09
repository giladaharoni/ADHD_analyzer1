package com.example.adhd_analyzer.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    private String Password;
    private String userName;
    private String fullName;
}
