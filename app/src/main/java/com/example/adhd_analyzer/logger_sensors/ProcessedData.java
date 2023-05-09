package com.example.adhd_analyzer.logger_sensors;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    @ColumnInfo
    private long timestamp;
    @ColumnInfo
    private long sessionId;
    @ColumnInfo
    private boolean stayInplace;
    @ColumnInfo
    private boolean highADHD;

    public ProcessedData(long timestamp, long sessionId, boolean stayInplace, boolean highADHD) {
        this.timestamp = timestamp;
        this.sessionId = sessionId;
        this.stayInplace = stayInplace;
        this.highADHD = highADHD;
    }

    public static List<ProcessedData> convertToProcessData(PyObject object, long sessionId){
        List<PyObject> obs = object.get("values").asList();
        ArrayList<ProcessedData> processedDataArrayList =  new ArrayList<ProcessedData>();
        for (PyObject ob: obs) {
            List<PyObject> row = ob.asList();
            ProcessedData data = new ProcessedData(row.get(0).toLong(), sessionId, row.get(1).toBoolean(),row.get(2).toBoolean());
            processedDataArrayList.add(data);
        }
        return processedDataArrayList;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}
