package com.example.adhd_analyzer.logger_sensors;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.DeleteTable;
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

    @Query("SELECT * FROM SensorLog WHERE timestamp > :threshold")
    List<SensorLog> getFromTime(long threshold);

    @Insert
    void insert(SensorLog... sensorLogs);
    @Update
    void update(SensorLog... sensorLogs);
    @Delete
    void delete(SensorLog... sensorLogs);

    @Query("DELETE FROM sensorlog")
    void nukeTable();

}