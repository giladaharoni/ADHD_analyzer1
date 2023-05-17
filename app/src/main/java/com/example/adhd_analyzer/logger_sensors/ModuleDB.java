package com.example.adhd_analyzer.logger_sensors;

import android.content.Context;

import androidx.room.Room;

import com.example.adhd_analyzer.user.UserDetailsDB;

public class ModuleDB {
    private static ProcessedDataDB dataDB;
    private static UserDetailsDB userDB;

    public static synchronized ProcessedDataDB getProcessedDB(Context context){
        if (dataDB == null){
            dataDB = Room.databaseBuilder(context,ProcessedDataDB.class,"process_data").fallbackToDestructiveMigration().build();
        }
        return dataDB;
    }

    public static synchronized UserDetailsDB getUserDetailsDB(Context context){
        if (userDB == null){
            userDB = Room.databaseBuilder(context,UserDetailsDB.class,"single_user").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return userDB;
    }
}
