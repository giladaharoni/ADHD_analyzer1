package com.example.adhd_analyzer.logger_sensors;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface processedDataDao {
    @Query("SELECT * FROM ProcessedData")
    List<ProcessedData> index();
    @Query("SELECT * FROM ProcessedData WHERE timestamp = :timestamp")
    ProcessedData get(long timestamp);

    @Insert
    void insert(ProcessedData... processedData);
    @Insert
    void insertList(List<ProcessedData> list);
    @Update
    void update(ProcessedData... processedData);
    @Delete
    void delete(ProcessedData... processedData);

    @Query("DELETE FROM ProcessedData")
    void nukeTable();
}
