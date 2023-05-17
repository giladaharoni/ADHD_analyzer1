package com.example.adhd_analyzer.logger_sensors;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ProcessedData.class}, version = 5)
public abstract class ProcessedDataDB extends RoomDatabase {
    public abstract processedDataDao processedDataDao();

}
