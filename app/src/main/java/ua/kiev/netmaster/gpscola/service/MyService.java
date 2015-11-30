package ua.kiev.netmaster.gpscola.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

import ua.kiev.netmaster.gpscola.R;
import ua.kiev.netmaster.gpscola.activities.MainActivity;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private NotificationManager notificationManager;
    private Notification notification2;
    private Timer timer;
    private TimerTask tTask;
    private long interval = 3000;

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private static String LOG_TAG ="myLogs";
    private Location mLastLocation;
    private MyBinder binder = new MyBinder();


    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "MyService onCreate");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        timer = new Timer();

        buildGoogleApiClient();


    }

    public void onDestroy() {
        Log.d(LOG_TAG, "MyService onDestroy");
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(LOG_TAG, "MyService onStartCommand");
        sendNotif(startId);
        startForeground(startId, notification2);

        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }

        schedule();

        return START_STICKY;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "MyService onBind");
        return binder;
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(LOG_TAG, "MyService onRebind");
    }

    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "MyService onUnbind");
        return super.onUnbind(intent);
    }


    void schedule() {
        Log.d(LOG_TAG, "MyService schedule");

        if (tTask != null) tTask.cancel();
        if (interval > 0) {
            tTask = new TimerTask() {
                public void run() {
                    testLocation();
                }
            };
            timer.schedule(tTask, 0, interval);
        }
    }

    void sendNotif(int startId) {
        Log.d(LOG_TAG, "MyService sendNotif()");
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // 1-я часть
        notification2 = new Notification.Builder(this)
                .setContentText("Text in status bar")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setWhen(System.currentTimeMillis())
                .setContentText("Big eye watch on you ;)")
                .setContentTitle("GPS Service works!")
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{300l, 100l})
                .build();

        notification2.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(startId, notification2);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "MyService.  onConnected()");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(LOG_TAG, String.valueOf(mLastLocation.getLatitude()));
            Log.d(LOG_TAG, String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "MyService.  onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "MyService.  onConnectionFailed()");
    }

    private void testLocation(){
        Log.d(LOG_TAG, "MyService.  testLocation()");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(LOG_TAG, String.valueOf(mLastLocation.getLatitude()));
            Log.d(LOG_TAG, String.valueOf(mLastLocation.getLongitude()));
        }
    }

    public Location getmLastLocation(){
        return mLastLocation;
    }


    public class MyBinder extends Binder {
       public MyService getService() {
            return MyService.this;
        }
    }
}
