package org.grameen.fdp.kasapin.ui.gpsPicker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.PlotGpsPoint;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.plotDetails.PlotDetailsActivity;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CommonUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends BaseActivity implements MapContract.View, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @Inject
    MapPresenter mPresenter;
    ProgressDialog progressDialog;
    String TAG = getClass().getSimpleName();
    @BindView(R.id.addPoint)
    Button addPointButton;
    @BindView(R.id.calculate)
    Button calculateArea;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Plot plot;
    private List<PlotGpsPoint> plotGpsPoints = new ArrayList<>();
    private List<LatLng> latLngList = new ArrayList<>();

    private PointsListAdapter mAdapter;
    private double accuracy;
    private double altitude;
    private boolean hasGpsDataBeenSaved = true;
    private String action = "";

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            altitude = location.getAltitude();
            accuracy = CommonUtils.round(location.getAccuracy(), 2);

            if(accuracy < 8){
                hideProgress();
                AppLogger.e(TAG, "^^^^^^^^^^ LOCATION CHANGED ^^^^^^^^^^^^");
                String msg =
                        "Do you want to add this point? \n\n" +
                                "Latitude    :   " + location.getLatitude() + "\n" +
                                "Longitude  :   " + location.getLongitude() + "\n" +
                                "Accuracy   :   " + accuracy + " meters\n" +
                                "Altitude    :   " + altitude + " high";

                showDialog(true, "Location update!", msg, (dialog, which) -> {
                    dialog.dismiss();
                    LatLng newLL = new LatLng(location.getLatitude(), location.getLongitude());
                    mAdapter.addPoint(newLL);

                    if (latLngList.size() > 0) {
                        if (findViewById(R.id.placeHolder).getVisibility() == View.VISIBLE)
                            findViewById(R.id.placeHolder).setVisibility(View.GONE);
                    }
                    hasGpsDataBeenSaved = false;
                }, "ADD POINT", (dialog, which) -> dialog.dismiss(), "CANCEL", 0);

                removeLocationListener();
            }
            else{
//                startLocationListener();
//                Toast.makeText(MapActivity.this, "Accuracy: " + String.valueOf(accuracy) + " not enough.", Toast.LENGTH_SHORT).show();
//                Toast.makeText(MapActivity.this, "Location accuracy not high enough.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        mPresenter.takeView(this);

        //Todo replace with di instance
        progressDialog = new ProgressDialog(this, R.style.AppDialog);

        if(getIntent() != null)
        mPresenter.getPlotData(getIntent().getStringExtra("plotExternalId"));

        calculateArea.setOnClickListener(view -> {
            action = "calculate area of ";
            if (checkNoGPSPointsAdded())
                computeAreaInSquareMeters();
        });

        addPointButton.setOnClickListener(v -> getLocationUpdates());

        findViewById(R.id.save).setOnClickListener(v -> {
            action = "save data of ";
            if (checkNoGPSPointsAdded())
                saveGpsPointsData();
        });
        onBackClicked();
    }

    @Override
    public void setPlotData(Plot plot) {
        this.plot = plot;
        setToolbar(plot.getName() + " " + getString(R.string.title_area_calc));

        if (plot.getGpsPoints() != null) {
            for (PlotGpsPoint point : plot.getGpsPoints()) {
                latLngList.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
        }

        if (latLngList.size() > 0)
            findViewById(R.id.placeHolder).setVisibility(View.GONE);

        mAdapter = new PointsListAdapter(this, latLngList);
        mAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, position) -> {
            mAdapter.removePoint(position);

            if (latLngList.size() > 0) {
                if (findViewById(R.id.placeHolder).getVisibility() == View.VISIBLE)
                    findViewById(R.id.placeHolder).setVisibility(View.GONE);
            } else {
                if (findViewById(R.id.placeHolder).getVisibility() == View.GONE)
                    findViewById(R.id.placeHolder).setVisibility(View.VISIBLE);
            }
            hasGpsDataBeenSaved = false;
        });
    }

    private boolean checkNoGPSPointsAdded() {
        int minNoOfPoints = 6;
        if (latLngList.size() < minNoOfPoints) {
            showMessage("Please add " + minNoOfPoints + " or more points to " + action + plot.getName());
            return false;
        }
        return true;
    }

    private void computeAreaInSquareMeters() {
        hideProgress();
        Double areaOfPlot = SphericalUtil.computeArea(latLngList);
        Double inHectares = convertToHectares(areaOfPlot);
        Double inAcres = convertToAcres(areaOfPlot);
        String message = "Area in Hectares is " + new DecimalFormat("0.00").format(inHectares) +
                "\nArea in Acres is " + new DecimalFormat("0.00").format(inAcres) +
                "\nArea in Square Meters is " + new DecimalFormat("0.00").format(areaOfPlot);
        showDialog(false, "Area of plot " + plot.getName(), message, (dialogInterface, i) ->
                        dialogInterface.dismiss(),
                getString(R.string.ok), null, null, 0);
    }

    private double convertToHectares(Double valueInSquareMetres) {
        return valueInSquareMetres / 10000;
    }

    private double convertToAcres(Double valueInSquareMetres) {
        return valueInSquareMetres / 4040.856;
    }

    private void getLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gpsStatus)
            startLocationListener();
        else {
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            showDialog(true, "GPS disabled", "Do you want to open GPS settings?", (dialog, which) -> {
                dialog.dismiss();
                startActivity(new Intent(action));
            }, getString(R.string.yes), (dialog, which) -> dialog.dismiss(), getString(R.string.no), 0);
        }
    }

    private void startLocationListener() {
        CommonUtils.showLoadingDialog(progressDialog, "Please wait...", "", true, 0, false);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setNumUpdates(15);
        locationRequest.setSmallestDisplacement(1.5f);
        locationRequest.setFastestInterval(5000);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showMessage("You need to enable permissions to display location!");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        //fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void removeLocationListener() {
        //fusedLocationClient.removeLocationUpdates(locationCallback);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
    }

    private void saveGpsPointsData() {
        plotGpsPoints.clear();

        for (LatLng latLng : latLngList) {
            PlotGpsPoint points = new PlotGpsPoint();
            points.setLatitude(latLng.latitude);
            points.setLongitude(latLng.longitude);
            points.setPrecision(accuracy);
            points.setAltitude(altitude);
            plotGpsPoints.add(points);
        }
        plot.setPlotPoints(getGson().toJson(plotGpsPoints));

        mPresenter.updatePlotData(plot);
    }

    @Override
    public void onPlotUpdateComplete(boolean didUpdate) {
        hasGpsDataBeenSaved = didUpdate;

        if(didUpdate)
          showMessage(R.string.gps_data_saved);
        else
            showMessage(R.string.data_not_saved);
    }

    @Override
    protected void onPause() {
        if (!hasGpsDataBeenSaved)
            saveGpsPointsData();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (!hasGpsDataBeenSaved)
            showDialog(true, getString(R.string.save_gps), getString(R.string.save_gps_rational), (d, w) -> {
                if (checkNoGPSPointsAdded())
                    saveGpsPointsData();
            }, getString(R.string.yes), (d, w) -> moveToPlotDetailsActivity(), getString(R.string.no), 0);
        else
            moveToPlotDetailsActivity();
    }

    private void moveToPlotDetailsActivity() {
        Intent intent = new Intent(this, PlotDetailsActivity.class);
        intent.putExtra("plot", new Gson().toJson(plot));
        startActivity(intent);
        supportFinishAfterTransition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            showDialog(false, "Missing Google Play Services", "You need to install Google Play Services to use the App properly", (w1, v) -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")));
                }
            }, "INSTALL", (w2, v) -> finish(), "EXIT", 0);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void hideProgress() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
}