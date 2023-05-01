package com.example.adhd_analyzer;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SensorLog.class}, version = 1)
public abstract class SensorsDB extends RoomDatabase{
    public abstract SensorLogDao sensorLogDao();
}
