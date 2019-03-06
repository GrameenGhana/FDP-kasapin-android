package org.grameen.fdp.kasapin.ui.map;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.map.PointsListAdapter.OnItemClickListener;
import org.grameen.fdp.kasapin.ui.plotDetails.PlotDetailsActivity;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CommonUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by aangjnr on 09/11/2017.
 */

public class MapActivity extends BaseActivity implements MapContract.View{

    @Inject
    MapPresenter mPresenter;


    public static Float DEFAULT_ZOOM = 17.0f;
    ProgressDialog progressDialog;
    String TAG = getClass().getSimpleName();
    Button addPoint;
    Button calculateArea;
    List<LatLng> latLngs = new ArrayList<>();
    Plot plot;
    RecyclerView recyclerView;
    boolean GpsStatus = false;
    LocationManager locationManager;
    boolean hasCalculated = false;
    PointsListAdapter mAdapter;
    Double AREA_OF_PLOT;
    android.location.LocationListener locationListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        mPresenter.takeView(this);



        progressDialog = new ProgressDialog(this, R.style.DialogTheme);



        recyclerView = findViewById(R.id.recycler_view);


        plot = new Gson().fromJson(getIntent().getStringExtra("plot"), Plot.class);



        if (plot != null) {
            setToolbar(plot.getName() +" " + getStringResources(R.string.title_area_calc));

        } else {
           setToolbar("Plot GPS Area Calculation");
        }

        AppLogger.i(TAG, "PLOT POINTS " + plot.getGpsPoints());

        if (plot.getGpsPoints() != null && !plot.getGpsPoints().equalsIgnoreCase("")) {

            try {
                String llgs[] = plot.getGpsPoints().split("_");
                for (String llg : llgs) {

                    String values[] = llg.split(",");

                    latLngs.add(new LatLng(Double.parseDouble(values[0]), Double.parseDouble(values[1])));
                }


            } catch (Exception e) {
                e.printStackTrace();
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

            if(latLngs.size() > 2)
                calculateArea.setEnabled(true);
                else
                    calculateArea.setEnabled(false);


            if(latLngs.size() > 0) {
                if(findViewById(R.id.placeHolder).getVisibility() == View.VISIBLE)
                    findViewById(R.id.placeHolder).setVisibility(View.GONE);



            }else{

                if(findViewById(R.id.placeHolder).getVisibility() == View.GONE)
                    findViewById(R.id.placeHolder).setVisibility(View.VISIBLE);

            }

        });


        calculateArea = (Button) findViewById(R.id.calculate);
        calculateArea.setEnabled(false);

        calculateArea.setOnClickListener(view -> {

            if (latLngs != null && latLngs.size() > 2) {

            if (!hasCalculated) {
                computeAreaInSquareMeters();

            }else{

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Double inHectares = convertToHectres(AREA_OF_PLOT);
                Double inAcres = convertToAcres(AREA_OF_PLOT);


                String message = "Area in Hectares is " + new DecimalFormat("0.00").format(inHectares) +
                        "\nArea in Acres is " + new DecimalFormat("0.00").format(inAcres) +
                        "\nArea in Square Meters is " + new DecimalFormat("0.00").format(AREA_OF_PLOT);

                showDialog(false, "Area of plot " + plot.getName(), message, (dialogInterface, i) -> dialogInterface.dismiss(), getStringResources(R.string.ok), (dialog, which) -> {

                    StringBuilder builder = new StringBuilder();

                    //Todo save latLngs
                    for (LatLng latLng : latLngs) {

                        if (!Objects.equals(latLng, latLngs.get(latLngs.size() - 1)))
                            builder.append(latLng.latitude).append(",").append(latLng.longitude).append("_");
                        else
                            builder.append(latLng.latitude).append(",").append(latLng.longitude);
                    }

                    plot.setGpsPoints(builder.toString());
                    dialog.dismiss();
                    saveData();

                }, getStringResources(R.string.save), 0);


                    hasCalculated = true;

            }

            } else
                showMessage("Please add 3 or more points to calculate the area of " + plot.getName());




        });

        addPoint =  findViewById(R.id.addPoint);
        addPoint.setOnClickListener(v -> {

           // progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle("Please wait");
            //progressDialog.setIndeterminate(true);


           progressDialog = CommonUtils.showLoadingDialog(progressDialog, "Please wait...", "", true, 0, false);


            getCurrentLocation();

        });


        onBackClicked();


        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                AppLogger.e(TAG, "^^^^^^^^^^ LOCATION CHANGED ^^^^^^^^^^^^");


                String msg = "Updated Location " + "\n" +
                        "Latitude : " + Double.toString(location.getLatitude()) + "\n" +
                        "Longitude : " + Double.toString(location.getLongitude()) + "\n" +
                        "Accuracy : " + location.getAccuracy() + " meters ";
                //"Altitude : "+mCurrentLocation.getAltitude()+" high ";
                //Toast.makeText(CustomerMapActivity.this, msg, Toast.LENGTH_LONG).show();


                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                showDialog(true, "Are you sure?", msg, (dialog, which) -> {

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

                    if (latLngs.size() > 2) {
                        calculateArea.setEnabled(true);

                    } else {
                        calculateArea.setEnabled(false);

                    }


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


        showDialog(false, "Area of plot " + plot.getName(), message, (dialogInterface, i) -> dialogInterface.dismiss(),
                getStringResources(R.string.ok),(dialog, which) -> {

            StringBuilder builder = new StringBuilder();

            //Todo save latLngs
            for (LatLng latLng : latLngs) {
                if (!Objects.equals(latLng, latLngs.get(latLngs.size() - 1)))
                    builder.append(latLng.latitude).append(",").append(latLng.longitude).append("_");
                else
                    builder.append(latLng.latitude).append(",").append(latLng.longitude);
            }

            plot.setGpsPoints(builder.toString());

            AppLogger.i(TAG, "STRING ARRAY OF LatLngs = " + builder);

            dialog.dismiss();

            saveData();
        }, getStringResources(R.string.save), 0);


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

            showMessage("Please Enable GPS First");

        }


    }


    @Override
    protected void onStop() {

        StringBuilder builder = new StringBuilder();

        //Todo save latLngs
        for (LatLng latLng : latLngs) {

            if (!Objects.equals(latLng, latLngs.get(latLngs.size() - 1)))
                builder.append(latLng.latitude).append(",").append(latLng.longitude).append("_");
            else
                builder.append(latLng.latitude).append(",").append(latLng.longitude);
        }

        plot.setGpsPoints(builder.toString());

        saveData();

        plot.setGpsPoints(builder.toString());

        super.onStop();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PlotDetailsActivity.class);
        intent.putExtra("plot", new Gson().toJson(plot));
        startActivity(intent);
        supportFinishAfterTransition();
    }


    void saveData(){

        getAppDataManager().getCompositeDisposable().add(Single.fromCallable(() ->  getAppDataManager().getDatabaseManager().plotsDao().insertOne(plot))
                .subscribeOn(Schedulers.io())
                .subscribe(aLong -> {

                    if(aLong > 0)
                    showMessage(R.string.new_data_updated);

                    else
                    showMessage(R.string.data_not_saved);

                }, throwable -> {
                    showMessage("An error occurred saving plot data. Please try again.");
                    throwable.printStackTrace();
                }));


    }


    @Override
    public void openMainActivity() {

    }
}
