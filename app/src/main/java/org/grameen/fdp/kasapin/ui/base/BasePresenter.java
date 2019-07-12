package org.grameen.fdp.kasapin.ui.base;


import android.support.v7.app.AppCompatActivity;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.PlotGpsPoint;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.network.model.BaseModel;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 8:02 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {

    public AppDataManager mAppDataManager;
    protected String TAG = "";
    private V mView;


    @Inject
    public BasePresenter(AppDataManager appDataManager) {
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void takeView(V view) {
        TAG = view.getClass().getSimpleName() + "\t";
        mView = view;

    }


    public AppCompatActivity getContext() {
        return (AppCompatActivity) mView;
    }

    @Override
    public void dropView() {
        if (mView != null) mView = null;
    }

    @Override
    public void setUserAsLoggedOut() {

        //getAppDataManager().setAccessToken(null);
        getAppDataManager().setUserAsLoggedOut();

    }

    public boolean isViewAttached() {
        return mView != null;
    }

    @Override
    public void openNextActivity() {

    }


    @Override
    public void onTokenExpire() {

        getView().openLoginActivityOnTokenExpire();

    }


    public V getView() {
        return mView;
    }


    public AppDataManager getAppDataManager() {
        return mAppDataManager;
    }


    protected void runSingleCall(Single<BaseModel> call, DisposableSingleObserver<BaseModel> disposableSingleObserver) {

        getAppDataManager().getCompositeDisposable().add(call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableSingleObserver));


    }

    protected void runSingleCall(Disposable disposableSingleObserver) {
        getAppDataManager().getCompositeDisposable().add(disposableSingleObserver);

    }


    public void setFarmerAsUnsynced(RealFarmer realFarmer){

        realFarmer.setSyncStatus(AppConstants.SYNC_NOT_OK);
        getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(realFarmer);
    }

    public void setFarmerAsSynced(RealFarmer realFarmer){
        realFarmer.setSyncStatus(AppConstants.SYNC_OK);
        getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(realFarmer);
    }







    protected void formatFarmerObjectData(RealFarmer farmer, JSONArray arrayOfValues) {

        JSONObject farmerDataJson;

        try {
            farmerDataJson = new JSONObject();


            farmerDataJson.put("answer", farmer.getFarmerName());
            farmerDataJson.put("field_name", AppConstants.FARMER_TABLE_COUNTRY_ADMIN_LEVEL_FIELD);
            arrayOfValues.put(farmerDataJson);


            farmerDataJson.put("answer", farmer.getFarmerName());
            farmerDataJson.put("field_name", AppConstants.FARMER_TABLE_NAME_FIELD);
            arrayOfValues.put(farmerDataJson);

            farmerDataJson = new JSONObject();
            farmerDataJson.put("answer", farmer.getCode());
            farmerDataJson.put("field_name", AppConstants.FARMER_TABLE_EXTERNAL_ID_FIELD);
            arrayOfValues.put(farmerDataJson);

            farmerDataJson = new JSONObject();
            farmerDataJson.put("answer", farmer.getCode());
            farmerDataJson.put("field_name", AppConstants.FARMER_TABLE_CODE_FIELD);
            arrayOfValues.put(farmerDataJson);

            farmerDataJson = new JSONObject();
            farmerDataJson.put("answer", farmer.getEducationLevel());
            farmerDataJson.put("field_name", AppConstants.FARMER_TABLE_EDUCATION_LEVEL_FIELD);
            arrayOfValues.put(farmerDataJson);

            farmerDataJson = new JSONObject();
            farmerDataJson.put("answer", farmer.getImageUrl());
            farmerDataJson.put("field_name", AppConstants.FARMER_TABLE_PHOTO_FIELD);
            arrayOfValues.put(farmerDataJson);

            farmerDataJson = new JSONObject();
            farmerDataJson.put("answer", farmer.getGender());
            farmerDataJson.put("field_name", AppConstants.FARMER_TABLE_GENDER_FIELD);
            arrayOfValues.put(farmerDataJson);

            farmerDataJson = new JSONObject();
            farmerDataJson.put("answer", farmer.getBirthYear());
            farmerDataJson.put("field_name", AppConstants.FARMER_TABLE_BIRTHDAY_FIELD);
            arrayOfValues.put(farmerDataJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void formatPlotsData(Plot plot, JSONArray arrayOfValues) {

        JSONObject plotsJsonData;

        try {
            plotsJsonData = new JSONObject();
            plotsJsonData.put("answer", plot.getExternalId());
            plotsJsonData.put("field_name", AppConstants.PLOT_EXTERNAL_ID_FIELD);
            arrayOfValues.put(plotsJsonData);


            plotsJsonData = new JSONObject();
            plotsJsonData.put("answer", plot.getName());
            plotsJsonData.put("field_name", AppConstants.PLOT_NAME_FIELD);
            arrayOfValues.put(plotsJsonData);

            plotsJsonData = new JSONObject();
            plotsJsonData.put("answer", plot.getArea());
            plotsJsonData.put("field_name", AppConstants.PLOT_AREA_FIELD);
            arrayOfValues.put(plotsJsonData);


            plotsJsonData = new JSONObject();
            plotsJsonData.put("answer", plot.getEstimatedProductionSize());
            plotsJsonData.put("field_name", AppConstants.PLOT_EST_PROD_FIELD);
            arrayOfValues.put(plotsJsonData);


            plotsJsonData = new JSONObject();
            plotsJsonData.put("answer", plot.getPlotAge());
            plotsJsonData.put("field_name", AppConstants.PLOT_AGE_FIELD);
            arrayOfValues.put(plotsJsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected void formatPlotsGpsData(Plot plot, JSONArray arrayOfValues) {

        if(plot.getGpsPoints() != null) {
            List<PlotGpsPoint> gpsPoints = plot.getGpsPoints();
            for(PlotGpsPoint point : gpsPoints) {

                JSONArray pointJsonArray = new JSONArray();

                JSONObject plotsGPSData;

                try {
                    plotsGPSData = new JSONObject();
                    plotsGPSData.put("answer", plot.getExternalId());
                    plotsGPSData.put("field_name", AppConstants.PLOT_EXTERNAL_ID_FIELD);
                    pointJsonArray.put(plotsGPSData);


                    plotsGPSData = new JSONObject();
                    plotsGPSData.put("answer", point.getLatitude_c());
                    plotsGPSData.put("field_name", AppConstants.PLOT_GPS_POINT_LAT_FIELD);
                    pointJsonArray.put(plotsGPSData);

                    plotsGPSData = new JSONObject();
                    plotsGPSData.put("answer", point.getLongitude_c());
                    plotsGPSData.put("field_name", AppConstants.PLOT_GPS_POINT_LNG_FIELD);
                    pointJsonArray.put(plotsGPSData);


                    plotsGPSData = new JSONObject();
                    plotsGPSData.put("answer", point.getAltitude_c());
                    plotsGPSData.put("field_name", AppConstants.PLOT_GPS_POINT_ALTITUDE_FIELD);
                    pointJsonArray.put(plotsGPSData);


                    plotsGPSData = new JSONObject();
                    plotsGPSData.put("answer", point.getPrecision_c());
                    plotsGPSData.put("field_name", AppConstants.PLOT_GPS_POINT_PRECISION_FIELD);
                    pointJsonArray.put(plotsGPSData);


                    arrayOfValues.put(pointJsonArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    protected JSONObject buildAllAnswersJsonDataPerFarmer(String code) {
        JSONObject jsonObject = new JSONObject();
        for (FormAnswerData answerData : getAppDataManager().getDatabaseManager().formAnswerDao().getAll(code)
                .blockingGet()) {
            Iterator iterator = answerData.getJsonData().keys();

            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                try {
                    if (jsonObject.has(key))
                        jsonObject.remove(key);

                    jsonObject.put(key, answerData.getJsonData().get(key));

                } catch (JSONException ignored) {
                }
            }
        }
        return jsonObject;
    }










}