package org.grameen.fdp.kasapin.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CommonUtils;

public class LocationPrepareService extends Service {

    private Handler h;

    private GoogleApiClient googleApiClient;

    private Double currLat, currLong, currAccuracy,currAlt;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("LOC_SERVICE_CREATE","Service started");

        h = new Handler(this.getMainLooper());

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
        googleApiClient.connect();

        //After 3 seconds, we start the location listener
        h.postDelayed(runner,1500);
    }

    Runnable runner = new Runnable() {
        @Override
        public void run() {
            h.postDelayed(runner,1500);
            stopLocationListener();
            startLocationListener();
        }
    };

    private void startLocationListener(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setSmallestDisplacement(1.5f);
        locationRequest.setFastestInterval(1000);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            showMessage("You need to enable permissions to display location!");
            //Right now we quietly fail
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        //fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

//            altitude = location.getAltitude();
            currAccuracy = CommonUtils.round(location.getAccuracy(), 2);

            currLat = location.getLatitude();
            currLong = location.getLongitude();
            currAlt = location.getAltitude();

            fetchCoordinates();

        }
    };

    private void stopLocationListener(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
    }

    public void fetchCoordinates() {
        Intent i = new Intent();
        i.putExtra("lat", currLat);
        i.putExtra("lng", currLong);
        i.putExtra("alt", currAlt);
        i.putExtra("accuracy",currAccuracy);
        i.setAction("GET_CAPTURED_LOCATION");
        sendBroadcast(i);

    }

    @Override
    public void onDestroy() {
        h.removeCallbacks(runner);
        stopLocationListener();

        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
