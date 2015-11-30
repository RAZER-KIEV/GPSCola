package ua.kiev.netmaster.gpscola.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ua.kiev.netmaster.gpscola.R;
import ua.kiev.netmaster.gpscola.service.MyService;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String LOG_TAG ="myLogs";
    private Location mLastLocation;
    private MyService myService;
    private boolean bound = false;
    private ServiceConnection sConn;
    private Intent intent;
    private double curLat, curLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        servicePrepare();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private void servicePrepare(){
        Log.d(LOG_TAG, "MapsActivity. servicePrepare()");
        //Service prepare.
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_TAG, "MapsActivity onServiceConnected");
                myService = ((MyService.MyBinder) binder).getService();
                bound = true;

                locationUpdate(null);
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "MapsActivity onServiceDisconnected");
                bound = false;
            }
        };
    }
    public void locationUpdate(View v){
        Log.d(LOG_TAG, "MapsActivity. locationUpdate()");
        mLastLocation = myService.getmLastLocation();

        if(mLastLocation!=null){
            curLat=  mLastLocation.getLatitude();
            curLong = mLastLocation.getLongitude();

            LatLng sydney = new LatLng(curLat,curLong);
            mMap.addMarker(new MarkerOptions().position(sydney).title("It's me"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }else {
            Toast.makeText(this,"No data",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "MapsActivity. onStart()");
        intent = new Intent(this, MyService.class);
        bindService(intent, sConn, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "MapsActivity. onStop()");
        if (!bound) return;
        unbindService(sConn);
        bound = false;
    }
}
