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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * the main tracking and processing background service takes place here.
 * @author gilad
 */
public class SensorsRecordsService extends Service implements SensorEventListener, LocationListener {
    private static final int PROCESS_TIME_CHUNK = 3;
    private int sessionId;

    private long lastTimeStamp = 0;
    private long latProcessTimestamp = 0;
    private static final int NOTIFICATION_ID = 1;
    private static final int DELAY_IN_MILLIS = 24 *60* 60 * 1000; // 23 minutes
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

    /**
     * When starting the service, a permanent notification should start.
     * @param intent The Intent supplied to {@link android.content.Context#startService},
     * as given.  This may be null if the service is being restarted after
     * its process has gone away, and it had previously returned anything
     * except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to
     * start.  Use with {@link #stopSelfResult(int)}.
     *
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //startForeground();
        handler = new Handler();
        //should finish the service
        handler.postDelayed(this::finishAndProcess, DELAY_IN_MILLIS);
        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel;
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

    /**
     * generate the tracking session ID, instead of long timestamp.
     * @return session id for the current tracking.
     */
    private int makeSessionId(){
        //hope you get it
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        return hour+day*100+month*10000+year*1000000;
    }

    /**
     * when the tracking is over. we took the remained process data and uploading it to the web.
     */
    private void finishAndProcess(){
        process(latProcessTimestamp);
        List<ProcessedData> dataList = ModuleDB.getProcessedDB(getApplicationContext()).processedDataDao().index();
        String username = ModuleDB.getUserDetailsDB(getApplicationContext()).userDao().index().get(0).getUserName();
        WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
        api.uploadData(username,dataList).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                api.uploadData(username,dataList).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(getApplicationContext(), "cannot upload your data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /**
     * process the data chunk by python, insert it to the DB.
     * @param timestampThreshold sensors logs from this timestamp would be fetched and processed.
     */
    private void process(long timestampThreshold){
        Context context = getApplicationContext();
        List<SensorLog> logList = logDao.getFromTime(timestampThreshold);
        if (logList == null || logList.isEmpty()) {
            return;
        }
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
        Python py = Python.getInstance();
        Gson gson = new Gson();
        String jsons = gson.toJson(logList);
        PyObject pyObject = py.getModule("data_process").callAttr("process", jsons);
        processedDataDao dataDao = ModuleDB.getProcessedDB(context).processedDataDao();
        List<ProcessedData> dataList = ProcessedData.convertToProcessData(pyObject,sessionId);
        try {
            ModuleDB.getProcessedDB(context).processedDataDao().insertList(dataList);
        } catch (android.database.sqlite.SQLiteConstraintException exception){
            ProcessedData firstElement = dataList.stream().min(Comparator.comparingLong(ProcessedData::getTimestamp)).orElse(null);
            if (firstElement != null) {
                ModuleDB.getProcessedDB(context).processedDataDao().update(firstElement);
                dataList.remove(firstElement);
                ModuleDB.getProcessedDB(context).processedDataDao().insertList(dataList);
            }
        }


    }


    /**
     * Init some of the properties. reset the DBs of the process Data and sensors logs. create
     * listeners for the sensors (if permitted).
     */
    @Override
    public void onCreate() {
        super.onCreate();
        startTime = new Date();
        sessionId = makeSessionId();
        db = Room.databaseBuilder(getApplicationContext(), SensorsDB.class, "sensorsDB").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        logDao = db.sensorLogDao();
        Thread thread = new Thread(){
            @Override
            public void run() {
                logDao.nukeTable();

                ModuleDB.getProcessedDB(getApplicationContext()).processedDataDao().nukeTable();
            }
        };
        thread.start();


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
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }



    }

    public class LogBinder extends Binder {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return logBinder;
    }


    /**
     * create permanent notification (necessary for background process in android)
     * @return the permanent notification.
     */
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

    /**
     * when the tracking is end, we can remove the notification.
     */
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

    /**
     * process the remain data, close the sensors listeners and print to the user about that.
     */
    public void onDestroy() {
        Thread thread = new Thread(() -> finishAndProcess());
        thread.start();
        while (thread.isAlive()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }
        }

        if (handler != null && removeNotificationRunnable != null) {
            handler.removeCallbacks(removeNotificationRunnable);
        }

        // Stop recording
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        Toast.makeText(this, "finish", Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }


    /**
     * listeners of the sensors update the sensor log table. in addition it process the bunch of data.
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Date now = new Date();
        if (now.getTime() - startTime.getTime() > 1000 * 60 * 60 * 24) {
            finishAndProcess();
            stopSelf();
            return;
        }
        if (now.getTime() - latProcessTimestamp > 1000*60*PROCESS_TIME_CHUNK){
            long threshold = latProcessTimestamp;
            latProcessTimestamp = System.currentTimeMillis();
            new Thread(() -> process(threshold)).start();
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
        if ((location != null) && (lastTimeStamp < System.currentTimeMillis())) {
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
            lastTimeStamp = timestamp;
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