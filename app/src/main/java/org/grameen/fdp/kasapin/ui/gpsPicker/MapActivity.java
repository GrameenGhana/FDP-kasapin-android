package org.grameen.fdp.kasapin.ui.map;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.View;
import android.widget.Button;

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

/**
 * Created by aangjnr on 09/11/2017.
 */

public class MapActivity extends BaseActivity implements MapContract.View {
    public static Float DEFAULT_ZOOM = 17.0f;
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
    LocationManager locationManager;
    boolean hasCalculated = false;
    PointsListAdapter mAdapter;
    Double AREA_OF_PLOT;
    android.location.LocationListener locationListener;

    int MIN_NO_OF_POINTS = 6;

    double accuracy;
    double altitude;
    boolean hasGpsDataBeenSaved = false;

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



        if (plot != null) {
            setToolbar(plot.getName() + " " + getStringResources(R.string.title_area_calc));

        } else {
            setToolbar("Plot GPS Area Calculation");
        }

        AppLogger.i(TAG, "PLOT POINTS " + plot.getGpsPoints());

        if (plot.getGpsPoints() != null) {
                 for (PlotGpsPoint point : plot.getGpsPoints()) {
                    latLngs.add(new LatLng(point.getLatitude_c(), point.getLongitude_c()));
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
            //latLngs.remove(position);

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

                    Double inHectares = convertToHectres(AREA_OF_PLOT);
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

        addPoint.setOnClickListener(v -> {
            getCurrentLocation();

        });



        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                altitude = location.getAltitude();
                accuracy = CommonUtils.round(location.getAccuracy(), 2);


                AppLogger.e(TAG, "^^^^^^^^^^ LOCATION CHANGED ^^^^^^^^^^^^");


                String msg =
                                "Do you want to save this? \n\n" +
                                "Latitude    :   " +  location.getLatitude() + "\n" +
                                "Longitude  :   " + location.getLongitude() + "\n" +
                                "Accuracy   :   " + accuracy + " meters\n" +
                                "Altitude    :   " + altitude + " high";
                //Toast.makeText(CustomerMapActivity.this, msg, Toast.LENGTH_LONG).show();





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


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                AppLogger.i(TAG, "^^^^^^^^^^ PROVIDER ENABLED ^^^^^^^^^^^^");

            }

            @Override
            public void onProviderDisabled(String s) {
                AppLogger.i(TAG, "^^^^^^^^^^ PROVIDER DISABLED ^^^^^^^^^^^^");


            }
        };

        onBackClicked();
    }



    void computeAreaInSquareMeters() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();


        AREA_OF_PLOT = SphericalUtil.computeArea(latLngs);
        AppLogger.d(TAG, "computeAreaInSquareMeters " + AREA_OF_PLOT);

        Double inHectares = convertToHectres(AREA_OF_PLOT);
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


    double convertToHectres(Double valueInSquareMetres) {
        return valueInSquareMetres / 10000;

    }

    double convertToAcres(Double valueInSquareMetres) {
        return valueInSquareMetres / 4040.856;

    }


    private void getCurrentLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (GpsStatus) {
            CommonUtils.showLoadingDialog(progressDialog, "Please wait...", "", true, 0, false);



            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);


            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // This is the Best And IMPORTANT part
            final Looper looper = null;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                if (locationManager != null) {
                    locationManager.requestSingleUpdate(criteria, locationListener, looper);
                }
            }
        } else {
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;

            showDialog(true, "GPS disabled", "Do you want to open GPS settings?", (dialog, which) -> {
                dialog.dismiss();
                startActivity(new Intent(action));
            }, getStringResources(R.string.yes), (dialog, which) ->dialog.dismiss(), getStringResources(R.string.no), 0);

        }


    }


    void saveGpsPointsData(boolean shouldExit){

        plotGpsPoints.clear();

        //Todo save latLngs
        for (LatLng latLng : latLngs) {
            PlotGpsPoint points = new PlotGpsPoint();
            points.setLatitude_c(latLng.latitude);
            points.setLongitude_c(latLng.longitude);
            points.setPrecision_c(accuracy);
            points.setAltitude_c(altitude);

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
    public void openMainActivity() {

    }
}
