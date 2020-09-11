package org.grameen.fdp.kasapin.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CommonUtils;

public class LocationPrepareService extends Service {

    private Handler h;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Double currLat, currLong, currAccuracy,currAlt;

    @Override
    public void onCreate() {
        super.onCreate();

        AppLogger.e("LOC_SERVICE_CREATE","Service started");

        h = new Handler(this.getMainLooper());

        buildGoogleApiClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(googleApiClient.isConnected())
        startLocationUpdates();
        return START_STICKY;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        AppLogger.e("LOC_SERVICE_CREATE", "Connected");
                        startLocationUpdates();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                        AppLogger.e("LOC_SERVICE", "Connection suspended==");
                        googleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {
                })
                .build();

        createLocationRequest();
    }

    private void createLocationRequest() {
        googleApiClient.connect();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setSmallestDisplacement(1.5f);
        locationRequest.setFastestInterval(1000);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Right now we quietly fail
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        //fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            AppLogger.e("LOC_SERVICE","Location changed : accuracy == " + location.getAccuracy());

            currAccuracy = CommonUtils.round(location.getAccuracy(), 2);

            currLat = location.getLatitude();
            currLong = location.getLongitude();
            currAlt = location.getAltitude();

            broadcastCoordinates();
        }
    };

    private void stopLocationUpdates() {
         LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
    }

    public void broadcastCoordinates() {
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
        AppLogger.e("LOC_SERVICE","onDestroy");

        stopLocationUpdates();

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
