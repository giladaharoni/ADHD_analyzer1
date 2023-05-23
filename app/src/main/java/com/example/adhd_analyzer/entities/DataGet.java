package com.example.adhd_analyzer.entities;

public class DataGet {

    String createdByUser;
    int sessionId;
    long timestamp;
    boolean stayInPlace;
    boolean highAdhd;


    public DataGet(String createdByUser, int sessionId, long timestamp, boolean stayInPlace, boolean highAdhd) {
        this.createdByUser = createdByUser;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.stayInPlace = stayInPlace;
        this.highAdhd = highAdhd;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setStayInPlace(boolean stayInPlace) {
        this.stayInPlace = stayInPlace;
    }

    public void setHighAdhd(boolean highAdhd) {
        this.highAdhd = highAdhd;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public int getSessionId() {
        return sessionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isStayInPlace() {
        return stayInPlace;
    }

    public boolean isHighAdhd() {
        return highAdhd;
    }
}
