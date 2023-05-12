package com.example.adhd_analyzer.entities;

public class Data {
    private int SessionId;
    private long Timestamp;
    private Boolean StayInPlace;
    private Boolean HighAdhd;

    public Data(int sessionId, long timestamp, Boolean stayInPlace, Boolean highAdhd) {
        SessionId = sessionId;
        Timestamp = timestamp;
        StayInPlace = stayInPlace;
        HighAdhd = highAdhd;
    }

    public Boolean getHighAdhd() {
        return HighAdhd;
    }


    public Boolean getStayInPlace() {
        return StayInPlace;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public int getSessionId() {
        return SessionId;
    }
}
