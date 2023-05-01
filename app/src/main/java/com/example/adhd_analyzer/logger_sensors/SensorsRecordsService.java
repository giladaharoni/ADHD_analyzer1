package com.example.adhd_analyzer.logger_sensors;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import com.example.adhd_analyzer.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class SensorsRecordsService extends Service implements SensorEventListener, LocationListener {

    private static final int NOTIFICATION_ID = 1;
    private static final int DELAY_IN_MILLIS = 2 * 60 * 1000; // 23 minutes
    private Handler handler;
    private Runnable removeNotificationRunnable;

    private static final String CHANNEL_ID = "bgfhghfd";
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private float[] accelerometerData;
    private float[] gyroscopeData;
    private float[] magnetometerData;
    private Date startTime;
    private SensorsDB db;
    private SensorLogDao logDao;

    private File csvFile;

    public SensorsRecordsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(NOTIFICATION_ID, createNotification());
        removeNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                removeNotification();
            }
        };
        handler = new Handler();
        handler.postDelayed(removeNotificationRunnable, DELAY_IN_MILLIS);

        return START_STICKY;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        startTime = new Date();
        // Initialize sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerData = new float[3];
        gyroscopeData = new float[3];
        magnetometerData = new float[3];


        // Register sensors
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // Initialize location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Register location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Background Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        db = Room.databaseBuilder(getApplicationContext(),SensorsDB.class,"sensorsDB").allowMainThreadQueries().build();
        logDao = db.sensorLogDao();
        logDao.nukeTable();


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Date now = new Date();
        if (now.getTime()-startTime.getTime()>1000*60*60*24){
            return;
        }
        // Record sensor data
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeData = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetometerData = event.values.clone();
        }


        // Check if we have GPS data
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            // Format sensor data as CSV
            long timestamp = System.currentTimeMillis();
            float accelerometerX = accelerometerData[0];
            float accelerometerY = accelerometerData[1];
            float accelerometerZ = accelerometerData[2];
            float gyroscopeX = gyroscopeData[0];
            float gyroscopeY = gyroscopeData[1];
            float gyroscopeZ = gyroscopeData[2];
            float magnetometerX = magnetometerData[0];
            float magnetometerY = magnetometerData[1];
            float magnetometerZ = magnetometerData[2];
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double altitude = location.getAltitude();
            SensorLog log = new SensorLog(timestamp ,accelerometerX ,accelerometerY , accelerometerZ,gyroscopeX,gyroscopeY,gyroscopeZ,magnetometerX,magnetometerY,magnetometerZ,latitude,longitude,altitude);
            logDao.insert(log);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private Notification createNotification() {
        // Customize the notification according to your needs
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Permanent Notification")
                .setContentText("This notification cannot be removed.")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);

        return builder.build();
    }

    private void removeNotification() {
        // Cancel the existing notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);

        // Create and display a new notification
        Notification newNotification = createNotification();
        notificationManager.notify(NOTIFICATION_ID, newNotification);

        // Schedule the removal of the new notification after the specified delay
        handler.postDelayed(removeNotificationRunnable, DELAY_IN_MILLIS);
    }

    public void onDestroy() {
        super.onDestroy();
        if (handler != null && removeNotificationRunnable != null) {
            handler.removeCallbacks(removeNotificationRunnable);
        }

        // Stop recording
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

}