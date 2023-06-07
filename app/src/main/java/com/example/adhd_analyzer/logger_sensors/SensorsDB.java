package com.example.adhd_analyzer.logger_sensors;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SensorLog.class}, version = 2)
public abstract class SensorsDB extends RoomDatabase{
    public abstract SensorLogDao sensorLogDao();
}