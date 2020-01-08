package org.grameen.fdp.kasapin.ui.gpsPicker;

/*
 * Created by aangjnr on 09/11/2017.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
    Button addPoint;
    @BindView(R.id.calculate)
    Button calculateArea;
    Plot plot;
    List<PlotGpsPoint> plotGpsPoints = new ArrayList<>();
    List<LatLng> latLngs = new ArrayList<>();
    RecyclerView recyclerView;
    boolean GpsStatus = false;
    boolean hasCalculated = false;
    PointsListAdapter mAdapter;
    Double AREA_OF_PLOT;
    int MIN_NO_OF_POINTS = 6;
    double accuracy;
    double altitude;
    boolean hasGpsDataBeenSaved = false;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        progressDialog = new ProgressDialog(this, R.style.AppDialog);
        recyclerView = findViewById(R.id.recycler_view);
        plot = new Gson().fromJson(getIntent().getStringExtra("plot"), Plot.class);

        setToolbar((plot != null) ? plot.getName() + " " + getStringResources(R.string.title_area_calc) : "Plot GPS Area Calculation");
        if (plot.getGpsPoints() != null) {
                 for (PlotGpsPoint point : plot.getGpsPoints()) {
                    latLngs.add(new LatLng(point.getLatitude(), point.getLongitude()));
                }
        }

        if (latLngs.size() > 0)
            findViewById(R.id.placeHolder).setVisibility(View.GONE);

        mAdapter = new PointsListAdapter(this, latLngs);
        mAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, position) -> {
            mAdapter.removePoint(position);
            calculateArea.setEnabled((latLngs.size() >= MIN_NO_OF_POINTS ));
            if (latLngs.size() > 0) {
                if (findViewById(R.id.placeHolder).getVisibility() == View.VISIBLE)
                    findViewById(R.id.placeHolder).setVisibility(View.GONE);
            } else {
                if (findViewById(R.id.placeHolder).getVisibility() == View.GONE)
                    findViewById(R.id.placeHolder).setVisibility(View.VISIBLE);
            }
        });

        calculateArea.setEnabled((latLngs.size() >= MIN_NO_OF_POINTS));
        calculateArea.setOnClickListener(view -> {
            if (latLngs != null && latLngs.size() >= MIN_NO_OF_POINTS ) {
                if (!hasCalculated) {
                    computeAreaInSquareMeters();
                } else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    Double inHectares = convertToHectares(AREA_OF_PLOT);
                    Double inAcres = convertToAcres(AREA_OF_PLOT);
                    String message = "Area in Hectares is " + new DecimalFormat("0.00").format(inHectares) +
                            "\nArea in Acres is " + new DecimalFormat("0.00").format(inAcres) +
                            "\nArea in Square Meters is " + new DecimalFormat("0.00").format(AREA_OF_PLOT);

                    showDialog(false, "Area of plot " + plot.getName(), message, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        saveGpsPointsData(false);
                        }, getStringResources(R.string.save), (dialog, which) -> dialog.dismiss(), getStringResources(R.string.cancel), 0);
                    hasCalculated = true;
                }
            } else
                showMessage("Please add "+ MIN_NO_OF_POINTS + " or more points to calculate the area of " + plot.getName());
        });
        addPoint.setOnClickListener(v ->getLocationUpdates());

        onBackClicked();
    }

    void computeAreaInSquareMeters() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        AREA_OF_PLOT = SphericalUtil.computeArea(latLngs);
        AppLogger.d(TAG, "computeAreaInSquareMeters " + AREA_OF_PLOT);

        Double inHectares = convertToHectares(AREA_OF_PLOT);
        Double inAcres = convertToAcres(AREA_OF_PLOT);
        String message = "Area in Hectares is " + new DecimalFormat("0.00").format(inHectares) +
                "\nArea in Acres is " + new DecimalFormat("0.00").format(inAcres) +
                "\nArea in Square Meters is " + new DecimalFormat("0.00").format(AREA_OF_PLOT);

        showDialog(false, "Area of plot " + plot.getName(), message, (dialogInterface, i) -> {
            dialogInterface.dismiss();
                    //Todo save latLngs
                    saveGpsPointsData(false);
                },
                getStringResources(R.string.save), (dialog, which) -> dialog.dismiss(), getStringResources(R.string.cancel), 0);
        hasCalculated = true;
    }

    double convertToHectares(Double valueInSquareMetres) {
        return valueInSquareMetres / 10000;
    }

    double convertToAcres(Double valueInSquareMetres) {
        return valueInSquareMetres / 4040.856;
    }

    private void getLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (GpsStatus)
           startLocationListener();
           else{
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            showDialog(true, "GPS disabled", "Do you want to open GPS settings?", (dialog, which) -> {
                dialog.dismiss();
                startActivity(new Intent(action));
            }, getStringResources(R.string.yes), (dialog, which) ->dialog.dismiss(), getStringResources(R.string.no), 0);
        }
    }

    void startLocationListener(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

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

    void removeLocationListener(){
        //fusedLocationClient.removeLocationUpdates(locationCallback);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            altitude = location.getAltitude();
            accuracy = CommonUtils.round(location.getAccuracy(), 2);
            AppLogger.e(TAG, "^^^^^^^^^^ LOCATION CHANGED ^^^^^^^^^^^^");
            String msg =
                    "Do you want to save this? \n\n" +
                            "Latitude    :   " +  location.getLatitude() + "\n" +
                            "Longitude  :   " + location.getLongitude() + "\n" +
                            "Accuracy   :   " + accuracy + " meters\n" +
                            "Altitude    :   " + altitude + " high";

            if (progressDialog.isShowing())
                progressDialog.dismiss();
            showDialog(true, "Location update!", msg, (dialog, which) -> {
                dialog.dismiss();
                LatLng newLL = new LatLng(location.getLatitude(), location.getLongitude());
                mAdapter.addPoint(newLL);
                addPoint.setEnabled(true);
                progressDialog.dismiss();
                hasCalculated = false;
                if (latLngs.size() > 0) {
                    if (findViewById(R.id.placeHolder).getVisibility() == View.VISIBLE)
                        findViewById(R.id.placeHolder).setVisibility(View.GONE);
                }
                if (latLngs.size() >= MIN_NO_OF_POINTS ) {
                    calculateArea.setEnabled(true);
                } else {
                    calculateArea.setEnabled(false);
                }
                hasGpsDataBeenSaved = false;
            }, "YES, CONTINUE", (dialog, which) -> dialog.dismiss(), "NO, CANCEL", 0);

            removeLocationListener();
        }
    };


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            if(locationResult.getLocations().size() > 0) {
                Location location = locationResult.getLocations().get(0);

                altitude = location.getAltitude();
                accuracy = CommonUtils.round(location.getAccuracy(), 2);
                AppLogger.e(TAG, "^^^^^^^^^^ LOCATION CHANGED ^^^^^^^^^^^^");
                String msg =
                        "Do you want to save this? \n\n" +
                                "Latitude    :   " +  location.getLatitude() + "\n" +
                                "Longitude  :   " + location.getLongitude() + "\n" +
                                "Accuracy   :   " + accuracy + " meters\n" +
                                "Altitude    :   " + altitude + " high";

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                showDialog(true, "Location update!", msg, (dialog, which) -> {
                    dialog.dismiss();
                    LatLng newLL = new LatLng(location.getLatitude(), location.getLongitude());
                    mAdapter.addPoint(newLL);
                    addPoint.setEnabled(true);
                    progressDialog.dismiss();
                    hasCalculated = false;
                    if (latLngs.size() > 0) {
                        if (findViewById(R.id.placeHolder).getVisibility() == View.VISIBLE)
                            findViewById(R.id.placeHolder).setVisibility(View.GONE);
                    }
                    if (latLngs.size() >= MIN_NO_OF_POINTS ) {
                        calculateArea.setEnabled(true);
                    } else {
                        calculateArea.setEnabled(false);
                    }
                    hasGpsDataBeenSaved = false;
                }, "YES, CONTINUE", (dialog, which) -> dialog.dismiss(), "NO, CANCEL", 0);

                removeLocationListener();
            }else
                showDialog(true, "No location obtained", "Location could not be obtained. Please check and ensure that the GPS is enabled then try again.",
                        (d, v)->d.dismiss(), getString(R.string.ok), null, null, 0);
        }
    };

    void saveGpsPointsData(boolean shouldExit){
        plotGpsPoints.clear();
        //Todo save latLngs
        for (LatLng latLng : latLngs) {
            PlotGpsPoint points = new PlotGpsPoint();
            points.setLatitude(latLng.latitude);
            points.setLongitude(latLng.longitude);
            points.setPrecision(accuracy);
            points.setAltitude(altitude);
            plotGpsPoints.add(points);
        }

        plot.setPlotPoints(getGson().toJson(plotGpsPoints));
        saveData(shouldExit);
    }

    @Override
    protected void onPause() {
        AppLogger.e(TAG, "********************* ON PAUSE");
        saveGpsPointsData(false);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(!hasGpsDataBeenSaved)
        showDialog(true, "Save GPS data", "Do you want to save the GPS points?", (d, w)->{
            saveGpsPointsData(true);
        }, "YES", (d, w)-> moveToPlotDetailsActivity(), "NO", 0);
        else
           moveToPlotDetailsActivity();
    }

    void moveToPlotDetailsActivity(){
        Intent intent = new Intent(this, PlotDetailsActivity.class);
        intent.putExtra("plot", new Gson().toJson(plot));
        startActivity(intent);
        supportFinishAfterTransition();
    }

    void saveData(boolean shouldExit) {
        if(getAppDataManager().getDatabaseManager().plotsDao().updateOne(plot) > 0) {
            hasGpsDataBeenSaved = true;
            showMessage(R.string.new_data_updated);
            if (shouldExit)
                moveToPlotDetailsActivity();
        } else
            showMessage(R.string.data_not_saved);
    }

    @Override
    public void openMainActivity() {}

    @Override
    protected void onStart() {
        super.onStart();
        AppLogger.e(TAG, "ON START");
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLogger.e(TAG, "ON RESUME");

        if (!checkPlayServices()) {
            showDialog(false, "Missing Google Play Services","You need to install Google Play Services to use the App properly", (w1, v)->{
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")));
                }
            }, "INSTALL", (w2, v)->finish(), "EXIT", 0);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
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
        AppLogger.e(TAG, "GOOGLE API CLIENT FAILED");
        AppLogger.e(TAG, connectionResult.getErrorMessage());


    }
}
