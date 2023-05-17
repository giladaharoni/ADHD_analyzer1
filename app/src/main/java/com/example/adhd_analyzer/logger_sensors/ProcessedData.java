package com.example.adhd_analyzer.logger_sensors;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.chaquo.python.PyObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "processedData", primaryKeys = {"sessionId","timestamp"})
public class ProcessedData {
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isStayInPlace() {
        return stayInPlace;
    }

    public void setStayInPlace(boolean stayInPlace) {
        this.stayInPlace = stayInPlace;
    }

    public boolean isHighAdhd() {
        return highAdhd;
    }

    public void setHighAdhd(boolean highAdhd) {
        this.highAdhd = highAdhd;
    }

    @ColumnInfo
    private long timestamp;
    @ColumnInfo
    private int sessionId;
    @ColumnInfo
    private boolean stayInPlace;
    @ColumnInfo
    private boolean highAdhd;

    public ProcessedData(long timestamp, int sessionId, boolean stayInPlace, boolean highAdhd) {
        this.timestamp = timestamp;
        this.sessionId = sessionId;
        this.stayInPlace = stayInPlace;
        this.highAdhd = highAdhd;
    }

    public static List<ProcessedData> convertToProcessData(PyObject object, long sessionId){
        List<PyObject> obs = object.get("values").asList();
        ArrayList<ProcessedData> processedDataArrayList =  new ArrayList<ProcessedData>();
        for (PyObject ob: obs) {
            List<PyObject> row = ob.asList();
            ProcessedData data = new ProcessedData(row.get(0).toLong(), (int)sessionId, row.get(1).toBoolean(),row.get(2).toBoolean());
            processedDataArrayList.add(data);
        }
        return processedDataArrayList;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
