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
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.adhd_analyzer.R;
import com.example.adhd_analyzer.api.UserApi;
import com.example.adhd_analyzer.api.WebServiceApi;
import com.example.adhd_analyzer.home;
import com.example.adhd_analyzer.login;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorsRecordsService extends Service implements SensorEventListener, LocationListener {
    private int PERMISSION_LOCATION_CODE = 1;

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
        //startForeground();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAndProcess();
                //should finish the service

            }
        }, DELAY_IN_MILLIS);
        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Intent notificationIntent = new Intent(this, home.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                    .setContentText("your tracking is active")
                    .setContentTitle("Analyze ADHD")
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_home_foreground);
            startForeground(1001, notification.build());
        }

        return START_STICKY;
    }

    private void startForeground(){
        Intent notificationIntent = new Intent(this, home.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        startForeground(1, new NotificationCompat.Builder(this,
                CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());

    }

    private int makeSessionId(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        return minutes+hour*100+day*1000+month*100000;
    }


    private void finishAndProcess(){
        Context context = getApplicationContext();

        List<SensorLog> logList = logDao.index();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
        Python py = Python.getInstance();
        Gson gson = new Gson();
        String jsons = gson.toJson(logList);
        PyObject pyObject = py.getModule("data_process").callAttr("process", jsons);
        ProcessedDataDB dataDB = ModuleDB.getProcessedDB(context);
        processedDataDao dataDao = dataDB.processedDataDao();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<ProcessedData> dataList = ProcessedData.convertToProcessData(pyObject,makeSessionId());
                dataDao.insertList(dataList);
                WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
                api.uploadData(login.theUser.getUserName(),dataList).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        int i = 1;
                        i++;
                        i++;

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        int i = 1;
                        i++;
                        i++;
                    }
                });



            }
        });
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
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // Initialize location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Register location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "please enable permissions", Toast.LENGTH_SHORT).show();
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }


        db = Room.databaseBuilder(getApplicationContext(), SensorsDB.class, "sensorsDB").allowMainThreadQueries().build();
        logDao = db.sensorLogDao();
        logDao.nukeTable();


    }

    public class LogBinder extends Binder {

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
        finishAndProcess();
        if (handler != null && removeNotificationRunnable != null) {
            handler.removeCallbacks(removeNotificationRunnable);
        }

        // Stop recording
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        Toast.makeText(this, "finish", Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Date now = new Date();
        if (now.getTime() - startTime.getTime() > 1000 * 60 * 60 * 24) {
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
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
            SensorLog log = new SensorLog(timestamp, accelerometerX, accelerometerY, accelerometerZ, gyroscopeX, gyroscopeY, gyroscopeZ, magnetometerX, magnetometerY, magnetometerZ, latitude, longitude, altitude);
            logDao.insert(log);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}