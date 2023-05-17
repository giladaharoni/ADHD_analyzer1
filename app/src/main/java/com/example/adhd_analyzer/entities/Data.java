package com.example.adhd_analyzer.entities;

public class Data {
    private int sessionId;
    private long timestamp;
    private Boolean stayInPlace;
    private Boolean highAdhd;

    public Data(int sessionId, long timestamp, Boolean stayInPlace, Boolean highAdhd) {
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.stayInPlace = stayInPlace;
        this.highAdhd = highAdhd;
    }

    public Boolean getHighAdhd() {
        return highAdhd;
    }


    public Boolean getStayInPlace() {
        return stayInPlace;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getSessionId() {
        return sessionId;
    }
}
