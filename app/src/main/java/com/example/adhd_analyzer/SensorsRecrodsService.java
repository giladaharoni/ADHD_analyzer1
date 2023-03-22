package com.example.adhd_analyzer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SensorsRecrodsService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private BufferedWriter accelerometerWriter;
    private BufferedWriter gyroscopeWriter;
    private BufferedWriter gpsWriter;

    public SensorsRecrodsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    gpsWriter.write(location.getLatitude()+","+location.getLongitude());
                    gpsWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };
        // Open file writers for each sensor
        try {
            accelerometerWriter = new BufferedWriter(new FileWriter(getExternalFilesDir(null) + "/accelerometer.csv"));
            gyroscopeWriter = new BufferedWriter(new FileWriter(getExternalFilesDir(null) + "/gyroscope.csv"));
            gpsWriter = new BufferedWriter(new FileWriter(getExternalFilesDir(null) + "/gps.csv"));

            // Write headers to files
            accelerometerWriter.write("timestamp,x,y,z\n");
            gyroscopeWriter.write("timestamp,x,y,z\n");
            gpsWriter.write("latitude,longitude\n");

            accelerometerWriter.flush();
            gyroscopeWriter.flush();
            gpsWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}