package com.example.adhd_analyzer.user;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserDetails.class}, version = 1)
public abstract class UserDetailsDB extends RoomDatabase {
    public abstract UserDao userDao();
}
