package org.grameen.fdp.kasapin.ui.base;


import android.support.v7.app.AppCompatActivity;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Mapping;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.PlotGpsPoint;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Submission;
import org.grameen.fdp.kasapin.data.network.model.BaseModel;
import org.grameen.fdp.kasapin.syncManager.UploadData;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

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


    public void setFarmerAsUnsynced(RealFarmer realFarmer) {

        realFarmer.setSyncStatus(AppConstants.SYNC_NOT_OK);
        getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(realFarmer);
    }

    public void setFarmerAsSynced(RealFarmer realFarmer) {
        realFarmer.setSyncStatus(AppConstants.SYNC_OK);
        getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(realFarmer);
    }


    public void syncData(FdpCallbacks.UploadDataListener listener, boolean showProgress, List<RealFarmer> farmers) {

        /**Todo Sync all un synced data
         Callbacks can be declared at the global or local level
         In this context, declaring the callback at the global level is okay since we'll be syncing all farmers data
         when user clicks on Upload farmer data button in the MainActivity, the json object containing the json array of farmers plus some extra fields is
         bundled and passed to the method uploadFarmersData(@param JSONObject object) in the UploadDataManager class
         A scenario where we might declare a callback listener at the local level is in the FarmerProfileActivity where
         we're only syncing one farmer
         */


        /**
         * Generate the Json object here.
         * You can have access to the database via the @param AppDataManager.getAppDatabase().farmersDao().getAll()
         * Same for plots, answers, etc.
         *
         * First check if there are un synced data
         **/

        if (showProgress)
            getView().showLoading("Uploading data", "Please wait...", false, 0, false);

        JSONObject payloadData = new JSONObject();

        Submission submission = new Submission();
        //Todo pass user id
        submission.setSurveyor__c(getAppDataManager().getUserId());

        try {
            payloadData.put("submission", new JSONObject(getGson().toJson(submission)));
        } catch (JSONException ignored) {
        }


        JSONArray farmerData = new JSONArray();

        runSingleCall(getAppDataManager().getDatabaseManager().mappingDao().getAll()
                .subscribeOn(Schedulers.io())
                .map(mappings -> Observable.fromIterable(mappings)
                        .groupBy(Mapping::getObjectName)
                        .flatMapSingle(groups -> groups.collect(() -> Collections.singletonMap(groups.getKey(), new ArrayList<Mapping>()),
                                (m, mapping) -> Objects.requireNonNull(m.get(groups.getKey())).add(mapping)))
                        .toList()
                        .subscribe(groupedMappings -> {

                            //AppLogger.e("GROUPED MAPPINGS = " + new Gson().toJson(groupedMappings));
                            //ppLogger.e("GROUPED MAPPINGS SIZE IS = " + groupedMappings.size());

                            /**
                             * Group mappings are in the form List<String> List<Mapping>> groupMappings;
                             * First parameter is the Object name (Object_c) eg. farmer_c and List<Mapping> has all mapping with object_c = farmer_c
                             * Since we're sending only farmer data first, lets loop through group mappings and get list of mappings which has object_c = farmer_c;
                             */

                            Observable.fromIterable(farmers)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new DisposableObserver<RealFarmer>() {
                                        @Override
                                        public void onNext(RealFarmer farmer) {
                                            /**Since the answers are separated by forms in the Answers table. ie. every Form filled has an answer data in the db
                                             * we should get all answers of the farmer and bundle it into one object where we can just get values to questions
                                             * using the question id
                                             * */

                                            JSONObject allAnswersJsonObject = buildAllAnswersJsonDataPerFarmer(farmer.getCode());

                                            JSONObject jsonObject = new JSONObject();

                                            try {
                                                jsonObject.put("external_id", farmer.getCode());

                                                for (int i = 0; i < groupedMappings.size(); i++) {

                                                    for (Map.Entry<String, ArrayList<Mapping>> mappingEntry : groupedMappings.get(i).entrySet()) {


                                                        List<Plot> farmersPlots = getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(farmer.getCode()).blockingGet();

                                                        JSONArray arrayOfValues = new JSONArray();


                                                        if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.PLOT_TABLE)) {

                                                            for (Plot plot : farmersPlots) {
                                                                JSONArray plotArray = new JSONArray();

                                                                formatPlotsData(plot, plotArray);
                                                                arrayOfValues.put(plotArray);

                                                            }

                                                        } else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.PLOT_GPS_POINT)) {

                                                            for (Plot plot : farmersPlots) {
                                                                JSONArray plotArray = new JSONArray();

                                                                formatPlotsGpsData(plot, plotArray);
                                                                arrayOfValues.put(plotArray);
                                                            }

                                                        } else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.DIAGONOSTIC_MONITORING_TABLE)) {
                                                            for (Plot plot : farmersPlots) {
                                                                JSONArray diagnosticAndMonitoringObjectsArray = new JSONArray();

                                                                //Generate JSON payload for Diagnostic Module
                                                                //The first JSONObject (index 0) in the array should hold the data for the foreign key fields ie. type_c, and any external ids
                                                                //At the server side, this index is checked to determine which data exists in that array and which table it belongs
                                                                JSONArray diagnosticArray = new JSONArray();

                                                                JSONObject index0JSONObject = new JSONObject();
                                                                index0JSONObject.put(AppConstants.TYPE_FIELD_NAME, AppConstants.MODULE_TYPE_DIAGNOSTIC);
                                                                index0JSONObject.put(AppConstants.DIAGONOSTIC_MONITORING_EXTERNAL_ID_C, plot.getExternalId());
                                                                index0JSONObject.put(AppConstants.PLOT_EXTERNAL_ID_FIELD, plot.getExternalId());

                                                                diagnosticArray.put(index0JSONObject);


                                                                JSONObject plotJsonObject = plot.getAOJsonData();

                                                                for (Mapping mapping : mappingEntry.getValue()) {

                                                                    String questionLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel(mapping.getQuestionId()).blockingGet();

                                                                    if (plotJsonObject.has(questionLabel)) {

                                                                        String answer = plotJsonObject.get(questionLabel).toString();


                                                                        if (!answer.isEmpty() && !answer.equalsIgnoreCase("null")) {
                                                                            JSONObject answerJson = new JSONObject();

                                                                            answerJson.put("field_name", mapping.getFieldName());
                                                                            answerJson.put("answer", answer);

                                                                            diagnosticArray.put(answerJson);
                                                                        }
                                                                    }
                                                                }

                                                                JSONObject recommendationJson = new JSONObject();
                                                                recommendationJson.put("answer", plot.getRecommendationId());
                                                                recommendationJson.put("field_name", "recommendation_id");

                                                                diagnosticArray.put(recommendationJson);


                                                                diagnosticAndMonitoringObjectsArray.put(diagnosticArray);


                                                                //Generate JSON payload for Monitoring Module for the plot's Monitorings
                                                                List<Monitoring> plotMonitoringList = getAppDataManager().getDatabaseManager().monitoringsDao().getAllMonitoringForPlot(plot.getExternalId())
                                                                        .blockingGet();

                                                                if (plotMonitoringList != null && plotMonitoringList.size() > 0) {


                                                                    for (Monitoring monitoring : plotMonitoringList) {
                                                                        JSONArray monitoringArray = new JSONArray();

                                                                        JSONObject monitoringIndex0JSONObject = new JSONObject();
                                                                        monitoringIndex0JSONObject.put(AppConstants.TYPE_FIELD_NAME, AppConstants.MODULE_TYPE_MONITORING);
                                                                        monitoringIndex0JSONObject.put(AppConstants.DIAGONOSTIC_MONITORING_EXTERNAL_ID_C, monitoring.getExternalId());
                                                                        monitoringIndex0JSONObject.put(AppConstants.PLOT_EXTERNAL_ID_FIELD, plot.getExternalId());

                                                                        monitoringArray.put(monitoringIndex0JSONObject);


                                                                        JSONObject monitoringJsonObject = monitoring.getMonitoringAOJsonData();

                                                                        for (Mapping mapping : mappingEntry.getValue()) {

                                                                            String questionLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel(mapping.getQuestionId()).blockingGet();

                                                                            if (monitoringJsonObject.has(questionLabel)) {

                                                                                String answer = monitoringJsonObject.get(questionLabel).toString();

                                                                                if (!answer.isEmpty() && !answer.equalsIgnoreCase("null")) {
                                                                                    JSONObject answerJson = new JSONObject();

                                                                                    answerJson.put("field_name", mapping.getFieldName());
                                                                                    answerJson.put("answer", answer);

                                                                                    monitoringArray.put(answerJson);
                                                                                }
                                                                            }
                                                                        }
                                                                        diagnosticAndMonitoringObjectsArray.put(monitoringArray);


                                                                    }
                                                                }

                                                                arrayOfValues.put(diagnosticAndMonitoringObjectsArray);

                                                            }
                                                        } else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.OBSERVATION_TABLE)) {

                                                            //Generate Observations JSON payload for Diagnostic Module




                                                            for (Plot plot : farmersPlots) {

                                                                JSONArray diagnosticAndMonitoringObservationsArray = new JSONArray();
                                                                JSONArray observationArray = new JSONArray();

                                                                JSONObject index0JSONObject = new JSONObject();
                                                                index0JSONObject.put(AppConstants.TYPE_FIELD_NAME, AppConstants.MODULE_TYPE_DIAGNOSTIC);
                                                                index0JSONObject.put(AppConstants.DIAGONOSTIC_MONITORING_EXTERNAL_ID_C, plot.getExternalId());
                                                                index0JSONObject.put(AppConstants.PLOT_EXTERNAL_ID_FIELD, plot.getExternalId());

                                                                observationArray.put(index0JSONObject);

                                                                for (Mapping mapping : mappingEntry.getValue()) {

                                                                    // String questionLabel = getAppDataManager().getDatabaseManager().questionDao().getByRecommendationName(mapping.getQuestionId()).blockingGet();
                                                                    Question aoQuestion = getAppDataManager().getDatabaseManager().questionDao().getQuestionById(mapping.getQuestionId());

                                                                    if (plot.getAOJsonData().has(aoQuestion.getLabelC())) {
                                                                        JSONObject answerJson = new JSONObject();

                                                                        answerJson.put("answer", plot.getAOJsonData().get(aoQuestion.getLabelC()));
                                                                        answerJson.put("field_name", mapping.getFieldName());
                                                                        answerJson.put("variable_c", aoQuestion.getLabelC());

                                                                        observationArray.put(answerJson);
                                                                    }
                                                                }

                                                                diagnosticAndMonitoringObservationsArray.put(observationArray);


                                                                //Generate JSON payload for Monitoring Module for the plot's Monitorings
                                                                List<Monitoring> plotMonitoringList = getAppDataManager().getDatabaseManager().monitoringsDao().getAllMonitoringForPlot(plot.getExternalId())
                                                                        .blockingGet();

                                                                if (plotMonitoringList != null && plotMonitoringList.size() > 0) {

                                                                    for (Monitoring monitoring : plotMonitoringList) {
                                                                        JSONArray monitoringObservationsArray = new JSONArray();

                                                                        JSONObject monitoringIndex0JSONObject = new JSONObject();
                                                                        monitoringIndex0JSONObject.put(AppConstants.TYPE_FIELD_NAME, AppConstants.MODULE_TYPE_MONITORING);
                                                                        monitoringIndex0JSONObject.put(AppConstants.DIAGONOSTIC_MONITORING_EXTERNAL_ID_C, monitoring.getExternalId());
                                                                        monitoringIndex0JSONObject.put(AppConstants.PLOT_EXTERNAL_ID_FIELD, plot.getExternalId());

                                                                        monitoringObservationsArray.put(monitoringIndex0JSONObject);


                                                                        JSONObject monitoringJsonObject = monitoring.getMonitoringAOJsonData();

                                                                        for (Mapping mapping : mappingEntry.getValue()) {

                                                                            Question monitoringAOQuestion = getAppDataManager().getDatabaseManager().questionDao().getQuestionById(mapping.getQuestionId());


                                                                            if (monitoringAOQuestion != null && monitoringJsonObject.has(monitoringAOQuestion.getLabelC())) {

                                                                                String[] relatedQuestions = monitoringAOQuestion.splitRelatedQuestions();

                                                                                if (relatedQuestions != null && relatedQuestions.length > 1) {

                                                                                    String answer = monitoringJsonObject.get(monitoringAOQuestion.getLabelC()).toString();
                                                                                    String competenceValue = (monitoringJsonObject.has(relatedQuestions[0]) ? monitoringJsonObject.get(relatedQuestions[0]).toString() : null);
                                                                                    String failureValue = (monitoringJsonObject.has(relatedQuestions[1]) ? monitoringJsonObject.get(relatedQuestions[1]).toString() : null);


                                                                                    if (!answer.isEmpty() && !answer.equalsIgnoreCase("null")) {
                                                                                        JSONObject answerJson = new JSONObject();

                                                                                        answerJson.put("answer", answer);
                                                                                        answerJson.put("field_name", mapping.getFieldName());
                                                                                        answerJson.put("variable_c", monitoringAOQuestion.getLabelC());
                                                                                        answerJson.put("competence_label", relatedQuestions[0]);
                                                                                        answerJson.put("competence_c", competenceValue);
                                                                                        answerJson.put("reason_for_failure_label", relatedQuestions[1]);
                                                                                        answerJson.put("reason_for_failure", failureValue);

                                                                                        monitoringObservationsArray.put(answerJson);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        diagnosticAndMonitoringObservationsArray.put(monitoringObservationsArray);

                                                                    }
                                                                }

                                                                arrayOfValues.put(diagnosticAndMonitoringObservationsArray);
                                                            }

                                                        } else {

                                                            //This formats the farmer basic info into the mapping payload since this data is not obtained from the form/answer survey module

                                                            if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.FARMER_TABLE))
                                                                formatFarmerObjectData(farmer, arrayOfValues);

                                                            for (Mapping mapping : mappingEntry.getValue()) {

                                                                String questionLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel(mapping.getQuestionId()).blockingGet();


                                                                if (allAnswersJsonObject.has(questionLabel)) {

                                                                    String answer = allAnswersJsonObject.get(questionLabel).toString();

                                                                    if (!answer.isEmpty() && !answer.equalsIgnoreCase("null")) {

                                                                        JSONObject answerJson = new JSONObject();

                                                                        answerJson.put("answer", answer);
                                                                        answerJson.put("field_name", mapping.getFieldName());

                                                                        arrayOfValues.put(answerJson);
                                                                    }


                                                                }
                                                            }
                                                        }
                                                        jsonObject.put(mappingEntry.getKey(), arrayOfValues);


                                                    }
                                                }


                                                farmerData.put(jsonObject);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                getView().showMessage(e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            e.printStackTrace();
                                            getView().hideLoading();
                                            getView().showMessage(e.getMessage());
                                        }

                                        @Override
                                        public void onComplete() {
                                            try {
                                                payloadData.put("data", farmerData);

                                                AppLogger.e(TAG, "Payload data is " + payloadData.toString());
                                                getView().hideLoading();

                                                UploadData.newInstance(getView(), getAppDataManager(), listener, true).uploadFarmersData(payloadData);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                getView().hideLoading();
                                                getView().showMessage(R.string.error_has_occurred_loading_data);
                                            }
                                        }
                                    });

                        }, throwable -> {
                            getView().hideLoading();
                            getView().showMessage(R.string.error_has_occurred);
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable -> {

                }, throwable -> {
                    getView().hideLoading();
                    getView().showMessage(R.string.error_has_occurred_loading_data);

                }));
    }


    protected void formatFarmerObjectData(RealFarmer farmer, JSONArray arrayOfValues) {
        JSONObject farmerDataJson;
        try {
            farmerDataJson = new JSONObject();
            Country country = getGson().fromJson(getAppDataManager().getStringValue("country"), Country.class);


            farmerDataJson.put("answer", farmer.getVillageId());
            farmerDataJson.put("field_name", AppConstants.FARMER_TABLE_COUNTRY_ADMIN_LEVEL_FIELD);
            arrayOfValues.put(farmerDataJson);

            farmerDataJson = new JSONObject();
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
            farmerDataJson.put("answer", (farmer.getImageUrl() != null && !farmer.getImageUrl().isEmpty()) ? farmer.getImageUrl() : "");
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
            plotsJsonData.put("field_name", AppConstants.EXTERNAL_ID_FIELD);
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

        if (plot.getGpsPoints() != null) {
            List<PlotGpsPoint> gpsPoints = plot.getGpsPoints();
            for (PlotGpsPoint point : gpsPoints) {

                JSONArray pointJsonArray = new JSONArray();

                JSONObject plotsGPSData;

                try {
                    plotsGPSData = new JSONObject();
                    plotsGPSData.put("answer", plot.getExternalId());
                    plotsGPSData.put("field_name", AppConstants.EXTERNAL_ID_FIELD);
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