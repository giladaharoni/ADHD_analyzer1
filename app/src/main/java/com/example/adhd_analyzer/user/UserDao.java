package com.example.adhd_analyzer.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM UserDetails")
    List<UserDetails> index();

    @Insert
    void insert(UserDetails... userDetails);
    @SuppressWarnings("unused")
    @Update
    void update(UserDetails... userDetails);
    @Delete
    void delete(UserDetails... userDetails);
}
