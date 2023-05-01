package com.example.adhd_analyzer.logger_sensors;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SensorLog {
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getAccelerometerX() {
        return accelerometerX;
    }

    public void setAccelerometerX(float accelerometerX) {
        this.accelerometerX = accelerometerX;
    }

    public float getAccelerometerY() {
        return accelerometerY;
    }

    public void setAccelerometerY(float accelerometerY) {
        this.accelerometerY = accelerometerY;
    }

    public float getAccelerometerZ() {
        return accelerometerZ;
    }

    public void setAccelerometerZ(float accelerometerZ) {
        this.accelerometerZ = accelerometerZ;
    }

    public float getGyroscopeX() {
        return gyroscopeX;
    }

    public void setGyroscopeX(float gyroscopeX) {
        this.gyroscopeX = gyroscopeX;
    }

    public float getGyroscopeY() {
        return gyroscopeY;
    }

    public void setGyroscopeY(float gyroscopeY) {
        this.gyroscopeY = gyroscopeY;
    }

    public float getGyroscopeZ() {
        return gyroscopeZ;
    }

    public void setGyroscopeZ(float gyroscopeZ) {
        this.gyroscopeZ = gyroscopeZ;
    }

    public float getMagnetometerX() {
        return magnetometerX;
    }

    public void setMagnetometerX(float magnetometerX) {
        this.magnetometerX = magnetometerX;
    }

    public float getMagnetometerY() {
        return magnetometerY;
    }

    public void setMagnetometerY(float magnetometerY) {
        this.magnetometerY = magnetometerY;
    }

    public float getMagnetometerZ() {
        return magnetometerZ;
    }

    public void setMagnetometerZ(float magnetometerZ) {
        this.magnetometerZ = magnetometerZ;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @PrimaryKey(autoGenerate=false)
    private long timestamp;
    private float accelerometerX;
    private float accelerometerY;
    private float accelerometerZ;
    private float gyroscopeX;
    private float gyroscopeY;
    private float gyroscopeZ;
    private float magnetometerX;
    private float magnetometerY;
    private float magnetometerZ;
    private double latitude;
    private double longitude;
    private double altitude;

    public SensorLog(long timestamp, float accelerometerX, float accelerometerY, float accelerometerZ, float gyroscopeX, float gyroscopeY, float gyroscopeZ, float magnetometerX, float magnetometerY, float magnetometerZ, double latitude, double longitude, double altitude) {
        this.timestamp = timestamp;
        this.accelerometerX = accelerometerX;
        this.accelerometerY = accelerometerY;
        this.accelerometerZ = accelerometerZ;
        this.gyroscopeX = gyroscopeX;
        this.gyroscopeY = gyroscopeY;
        this.gyroscopeZ = gyroscopeZ;
        this.magnetometerX = magnetometerX;
        this.magnetometerY = magnetometerY;
        this.magnetometerZ = magnetometerZ;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
}