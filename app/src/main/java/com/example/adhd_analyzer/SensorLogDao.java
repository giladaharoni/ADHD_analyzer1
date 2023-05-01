package com.example.adhd_analyzer;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SensorLogDao {
    @Query("SELECT * FROM sensorlog")
    List<SensorLog> index();
    @Query("SELECT * FROM sensorlog WHERE timestamp = :timestamp")
    SensorLog get(long timestamp);

    @Insert
    void insert(SensorLog... sensorLogs);
    @Update
    void update(SensorLog... sensorLogs);
    @Delete
    void delete(SensorLog... sensorLogs);

}
