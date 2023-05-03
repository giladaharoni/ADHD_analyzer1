package com.example.adhd_analyzer.logger_sensors;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.chaquo.python.PyObject;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ProcessedData {
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isStayInplace() {
        return stayInplace;
    }

    public void setStayInplace(boolean stayInplace) {
        this.stayInplace = stayInplace;
    }

    public boolean isHighADHD() {
        return highADHD;
    }

    public void setHighADHD(boolean highADHD) {
        this.highADHD = highADHD;
    }

    @PrimaryKey
    private long timestamp;
    private boolean stayInplace;
    private boolean highADHD;

    public ProcessedData(long timestamp, boolean stayInplace, boolean highADHD) {
        this.timestamp = timestamp;
        this.stayInplace = stayInplace;
        this.highADHD = highADHD;
    }

    public static List<ProcessedData> convertToProcessData(PyObject object){
        List<PyObject> obs = object.get("values").asList();
        ArrayList<ProcessedData> processedDataArrayList =  new ArrayList<ProcessedData>();
        for (PyObject ob: obs) {
            List<PyObject> row = ob.asList();
            ProcessedData data = new ProcessedData(row.get(0).toLong(),row.get(1).toBoolean(),row.get(2).toBoolean());
            processedDataArrayList.add(data);
        }
        return processedDataArrayList;
    }

}
