package com.example.adhd_analyzer.logger_sensors;

import android.content.Context;

import androidx.room.Room;

public class ModuleDB {
    private static ProcessedDataDB dataDB;
    public static synchronized ProcessedDataDB getProcessedDB(Context context){
        if (dataDB == null){
            dataDB = Room.databaseBuilder(context,ProcessedDataDB.class,"process_data").build();
        }
        return dataDB;
    }
}
