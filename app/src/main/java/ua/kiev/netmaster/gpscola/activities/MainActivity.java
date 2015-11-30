package ua.kiev.netmaster.gpscola.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import ua.kiev.netmaster.gpscola.R;
import ua.kiev.netmaster.gpscola.service.MyService;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView curLat, curLong;
    private static String LOG_TAG ="myLogs";
    private Location mLastLocation;
    private MyService myService;
    private boolean bound = false, started = false;
    private ServiceConnection sConn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "MainActivity. onCreate()");
        setContentView(R.layout.activity_main);

        tvPrepare();

        servicePrepare();

    }

    public void locationUpdate(View v){
        Log.d(LOG_TAG, "MainActivity. locationUpdate()");
                mLastLocation = myService.getmLastLocation();

                if(mLastLocation!=null){
                    curLat.setText( String.valueOf(mLastLocation.getLatitude()));
                    curLong.setText( String.valueOf(mLastLocation.getLongitude()));
                }else {
                    curLat.setText("No lat");
                    curLong.setText("No long");
                    Toast.makeText(this, "No data", Toast.LENGTH_LONG).show();
                }
    }

    private void tvPrepare(){
        Log.d(LOG_TAG, "MainActivity. tvPrepare()");
        curLat = (TextView) findViewById(R.id.curLat);
        curLong = (TextView) findViewById(R.id.curLong);

    }

    private void servicePrepare(){
        Log.d(LOG_TAG, "MainActivity. servicePrepare()");
        //Service prepare.
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_TAG, "MainActivity onServiceConnected");
                myService = ((MyService.MyBinder) binder).getService();
                bound = true;

                locationUpdate(null);
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "MainActivity onServiceDisconnected");
                bound = false;
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "MainActivity. onResume()");
        if(!started) onClickStart(null);

        //locationUpdate(null);
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "MainActivity. onPause()");
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "MainActivity. onStart()");
        intent = new Intent(this, MyService.class);
        bindService(intent, sConn, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "MainActivity. onStop()");
        if (!bound) return;
        unbindService(sConn);
        bound = false;
    }


    public void toMap(View view){
        intent  = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    //Starts Phone Settings.
    public void onClickLocationSettings(View view) {
        Log.d(LOG_TAG, "MainActivity.  onClickLocationSettings()");
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public void onClickStart(View v) {
        Log.d(LOG_TAG, "MainActivity.  onClickStart()");
        if(!started)startService(new Intent(this, MyService.class));
        started=true;
    }

    public void onClickStop(View v) {
        Log.d(LOG_TAG, "MainActivity.  onClickStop()");
        if(started)stopService(new Intent(this, MyService.class));
        started=false;
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "MainActivity.  onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "MainActivity.  onConnected()");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "MainActivity.  onConnectionSuspended()");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "MainActivity.  onConnectionFailed()");

    }
}