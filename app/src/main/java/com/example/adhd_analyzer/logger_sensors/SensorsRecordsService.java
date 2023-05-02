package com.example.adhd_analyzer.logger_sensors;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import com.example.adhd_analyzer.R;
import com.example.adhd_analyzer.home;

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
    private LogBinder logBinder = new LogBinder();


    public SensorsRecordsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

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
            Toast.makeText(this, "please enable permissions", Toast.LENGTH_SHORT).show();
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }



        db = Room.databaseBuilder(getApplicationContext(),SensorsDB.class,"sensorsDB").allowMainThreadQueries().build();
        logDao = db.sensorLogDao();
        logDao.nukeTable();


    }

    public class LogBinder extends Binder{

    }

    @Override
    public IBinder onBind(Intent intent) {
        return logBinder;
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
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}