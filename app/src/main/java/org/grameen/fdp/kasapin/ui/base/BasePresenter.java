package org.grameen.fdp.kasapin.ui.base;


import androidx.appcompat.app.AppCompatActivity;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Logs;
import org.grameen.fdp.kasapin.data.db.entity.Mapping;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.PlotGpsPoint;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.Submission;
import org.grameen.fdp.kasapin.data.network.model.BaseModel;
import org.grameen.fdp.kasapin.syncManager.UploadDataManager;
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

public class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {
    public AppDataManager mAppDataManager;
    protected String TAG = "";
    int totalNoOfImagesToBeUploaded = 0;
    private V mView;


    @Inject
    public BasePresenter(AppDataManager appDataManager) {
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void takeView(V view) {
        TAG = "Presenter";
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
        getAppDataManager().setUserAsLoggedOut();
    }

    public boolean isViewAttached() {
        return mView != null;
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


    public void setFarmerAsUnSynced(Farmer farmer) {
        farmer.setSyncStatus(AppConstants.SYNC_NOT_OK);
        updateFarmerData(farmer);
    }

    public void updateFarmerData(Farmer realFarmer) {
        getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(realFarmer);
    }

    private void generateFamilyMembersJson(Farmer farmer, Map.Entry<String, ArrayList<Mapping>> mappingEntry, JSONArray arrayOfValues) {
        FormAndQuestions familyMembersFormAndQuestions = getAppDataManager().getDatabaseManager().formAndQuestionsDao()
                .getFormAndQuestionsByName(AppConstants.FAMILY_MEMBERS).blockingGet();
        if (familyMembersFormAndQuestions != null) {
            FormAnswerData answerData = getAppDataManager().getDatabaseManager().formAnswerDao()
                    .getFormAnswerData(farmer.getCode(), familyMembersFormAndQuestions
                            .getForm().getFormTranslationId());

            if (answerData != null) {
                try {
                    JSONArray familyMembersJsonArrayData = new JSONArray(answerData.getData());
                    for (int j = 0; j < familyMembersJsonArrayData.length(); j++) {
                        JSONObject answerJsonData = familyMembersJsonArrayData.getJSONObject(j);
                        JSONArray jsonArray = new JSONArray();
                        for (Mapping mapping : mappingEntry.getValue()) {
                            Question question = getAppDataManager().getDatabaseManager().questionDao().get(mapping.getQuestionId()).blockingGet();
                            if (answerJsonData.has(question.getLabelC())) {

                                String answerValue = answerJsonData.get(question.getLabelC()).toString();
                                if (!answerValue.isEmpty() && !answerValue.equalsIgnoreCase("null"))
                                    jsonArray.put(generateAnswerJSONObject(question.getTypeC(), mapping.getFieldName(), answerValue, null));
                            }
                        }
                        arrayOfValues.put(jsonArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generatePlotsPayloadData(List<Plot> farmersPlots, JSONArray arrayOfValues) {
        for (Plot plot : farmersPlots) {
            JSONArray plotArray = new JSONArray();
            formatPlotsData(plot, plotArray);
            arrayOfValues.put(plotArray);
        }
    }

    private void generateFarmersPlotsGPSPointsPayloadData(List<Plot> farmersPlots, JSONArray arrayOfValues) {
        for (Plot plot : farmersPlots) {
            JSONArray plotArray = new JSONArray();
            formatPlotsGpsData(plot, plotArray);
            arrayOfValues.put(plotArray);
        }
    }

    private void generateDiagnosticMonitoringPayloadData(Map.Entry<String, ArrayList<Mapping>> mappingEntry, List<Plot> farmersPlots, JSONArray arrayOfValues, boolean isObservationsData) {
        for (Plot plot : farmersPlots) {
            JSONArray diagnosticAndMonitoringPayloadArray = new JSONArray();

            //Generate JSON payload for Diagnostic Module

            //The first JSONObject (index 0) in the array should hold the data for the foreign key fields ie. type_c, and any external ids
            //At the server side, this index is checked to determine which data exists in that array and which table it belongs
            JSONArray diagnosticArray = new JSONArray();
            try {
                //JSONObject data which differentiates plot information answers based on whether its a diagnostic answer data
                //or monitoring answer data

                //Eg. Tree Age is an Adoption Observation value which can be obtained either from diagnostic mode or monitoring mode when
                //using the app. So we must differentiate which answer value is coming from which module (Diagnostic or Monitoring)

                //For plot AO data, plotExternalId == moduleExternalId
                diagnosticArray.put(generateModuleData(plot.getExternalId(), plot.getExternalId(), AppConstants.MODULE_TYPE_DIAGNOSTIC));
                JSONObject plotJsonObject = plot.getAOJsonData();

                for (Mapping mapping : mappingEntry.getValue()) {
                    String questionLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel(mapping.getQuestionId()).blockingGet();
                    if (plotJsonObject.has(questionLabel)) {
                        String answer = plotJsonObject.get(questionLabel).toString();
                        if (!answer.isEmpty() && !answer.equalsIgnoreCase("null"))
                            diagnosticArray.put(generateAnswerJSONObject(null, mapping.getFieldName(), answer, (isObservationsData) ? questionLabel : null));
                    }
                }

                if (!isObservationsData) {
                    JSONObject recommendationJson = new JSONObject();
                    recommendationJson.put("answer", plot.getRecommendationId());
                    recommendationJson.put("field_name", "recommendation_id");
                    diagnosticArray.put(recommendationJson);
                }

                diagnosticAndMonitoringPayloadArray.put(diagnosticArray);

                //Add recommendation id to the payload for diagnostic data only


                //Generate JSON payload for Monitoring Module for each plot's Monitorings
                List<Monitoring> plotMonitoringList = getAppDataManager().getDatabaseManager().monitoringDao().getAllMonitoringForPlot(plot.getExternalId())
                        .blockingGet();

                if (plotMonitoringList != null && plotMonitoringList.size() > 0) {
                    for (Monitoring monitoring : plotMonitoringList) {
                        JSONArray monitoringPayload = new JSONArray();
                        monitoringPayload.put(generateModuleData(plot.getExternalId(), monitoring.getExternalId(), AppConstants.MODULE_TYPE_MONITORING));

                        JSONObject monitoringJsonObject = monitoring.getMonitoringAOJsonData();
                        for (Mapping mapping : mappingEntry.getValue()) {
                            Question question = getAppDataManager().getDatabaseManager().questionDao().getQuestionById(mapping.getQuestionId());

                            if (question != null && monitoringJsonObject.has(question.getLabelC())) {
                                String answer = monitoringJsonObject.getString(question.getLabelC());
                                if (!answer.isEmpty() && !answer.equalsIgnoreCase("null")) {
                                    JSONObject answerJson = null;
                                    if (isObservationsData) {
                                        String[] relatedQuestions = question.splitRelatedQuestions();
                                        if (relatedQuestions != null && relatedQuestions.length > 1) {
                                            String competenceValue = (monitoringJsonObject.has(relatedQuestions[0]) ? monitoringJsonObject.get(relatedQuestions[0]).toString() : null);
                                            String failureValue = (monitoringJsonObject.has(relatedQuestions[1]) ? monitoringJsonObject.get(relatedQuestions[1]).toString() : null);
                                            //Extra information added to the answerJson for Observations mapping data

                                            answerJson = generateAnswerJSONObject(
                                                    question.getTypeC(), mapping.getFieldName(), answer, question.getLabelC());
                                            answerJson.put("competence_label", relatedQuestions[0]);
                                            answerJson.put("competence_c", competenceValue);
                                            answerJson.put("reason_for_failure_label", relatedQuestions[1]);
                                            answerJson.put("reason_for_failure", failureValue);
                                        }
                                    } else {
                                        answerJson = generateAnswerJSONObject(
                                                question.getTypeC(), mapping.getFieldName(), answer, null);
                                    }
                                    if (answerJson != null)
                                        monitoringPayload.put(answerJson);
                                }
                            }
                        }
                        diagnosticAndMonitoringPayloadArray.put(monitoringPayload);
                    }
                }
                arrayOfValues.put(diagnosticAndMonitoringPayloadArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void syncData(FdpCallbacks.UploadDataListener listener, boolean showProgress, List<Farmer> farmers) {
        /*
         * Callbacks can be declared at the global or local level
         * In this context, declaring the callback at the global level is okay since we'll be syncing all farmers data
         * when user clicks on Upload farmer data button in the MainActivity, the json object containing the json array of farmers plus some extra fields is
         * bundled and passed to the method uploadFarmersData(@param JSONObject object) in the UploadDataManager class
         * A scenario where we might declare a callback listener at the local level is in the FarmerProfileActivity where
         * we're only syncing one farmer*
         */


        /*
         * Generate the Json object here.
         * You can have access to the database via the @param AppDataManager.getAppDatabase().farmersDao().getAll()
         * Same for plots, answers, etc.
         *
         * First check if there are un synced data
         **/

        totalNoOfImagesToBeUploaded = 0;

        if (showProgress)
            getView().showLoading("Uploading data", "Please wait...", false, 0, false);


        JSONObject payloadData = new JSONObject();
        JSONObject imagesOnlyPayload = new JSONObject();
        List<Integer> photoTypeQuestions = getAppDataManager().getDatabaseManager().questionDao()
                .getQuestionsOfTypePhoto().blockingGet(new ArrayList<>());

        AppLogger.e(TAG, "photoTypeQuestions => " + getGson().toJson(photoTypeQuestions));

        Submission submission = new Submission();
        //Todo pass user id
        submission.setSurveyor__c(getAppDataManager().getUserId());

        try {
            JSONObject submissionData = new JSONObject(getGson().toJson(submission));
            payloadData.put("submission", submissionData);
            imagesOnlyPayload.put("submission", submissionData);
        } catch (JSONException ignored) {
        }

        JSONArray payloadDataArray = new JSONArray();
        //JSONArray imagesPayloadDataArray = new JSONArray();
        List<JSONObject> imagesPayloadDataList = new ArrayList<>();


        runSingleCall(getAppDataManager().getDatabaseManager().mappingDao().getAll()
                .subscribeOn(Schedulers.io())
                .map(mappings -> Observable.fromIterable(mappings)
                        .groupBy(Mapping::getObjectName)
                        .flatMapSingle(groups -> groups.collect(() -> Collections.singletonMap(groups.getKey(), new ArrayList<Mapping>()),
                                (m, mapping) -> Objects.requireNonNull(m.get(groups.getKey())).add(mapping)))
                        .toList()
                        .subscribe(groupedMappings -> {
                            /*
                             * Group mappings are in the form List<String> List<Mapping>> groupMappings;
                             * First parameter is the Object name (Object_c) eg. farmer_c and List<Mapping> has all mapping with object_c = farmer_c
                             * Since we're sending only farmer data first, lets loop through group mappings and get list of mappings which has object_c = farmer_c;
                             */
                            Observable.fromIterable(farmers)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new DisposableObserver<Farmer>() {
                                        @Override
                                        public void onNext(Farmer farmer) {
                                            /* Since the answers are separated by forms in the Answers table. ie. every Form filled has an answer data in the db
                                             * we should get all answers of the farmer and bundle it into one object where we can just get values to questions
                                             * using the question id
                                             * */

                                            Logs farmerLogs = getAppDataManager().getDatabaseManager().logsDao().getAllLogsForFarmer(farmer.getCode())
                                                    .blockingGet(new Logs(farmer.getCode()));


                                            JSONObject allAnswersJsonObject = buildAllAnswersJsonDataPerFarmer(farmer.getCode());
                                            JSONObject jsonObject = new JSONObject();
                                            JSONObject imagesJsonObject = new JSONObject();

                                            try {
                                                jsonObject.put("external_id", farmer.getCode());
                                                imagesJsonObject.put("external_id", farmer.getCode());

                                                for (int i = 0; i < groupedMappings.size(); i++) {
                                                    for (Map.Entry<String, ArrayList<Mapping>> mappingEntry : groupedMappings.get(i).entrySet()) {

                                                        List<Plot> farmersPlots = getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(farmer.getCode()).blockingGet();
                                                        JSONArray arrayOfValues = new JSONArray();
                                                        JSONArray imagesArrayOfValues = new JSONArray();
                                                        if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.FAMILY_MEMBERS_TABLE))
                                                            generateFamilyMembersJson(farmer, mappingEntry, arrayOfValues);

                                                        else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.PLOT_TABLE))
                                                            generatePlotsPayloadData(farmersPlots, arrayOfValues);

                                                        else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.PLOT_GPS_POINT))
                                                            generateFarmersPlotsGPSPointsPayloadData(farmersPlots, arrayOfValues);

                                                        else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.DIAGNOSTIC_MONITORING_TABLE))
                                                            generateDiagnosticMonitoringPayloadData(mappingEntry, farmersPlots, arrayOfValues, false);

                                                        else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.OBSERVATION_TABLE))
                                                            //Generate Observations JSON payload for Diagnostic Module
                                                            generateDiagnosticMonitoringPayloadData(mappingEntry, farmersPlots, arrayOfValues, true);
                                                        else {
                                                            //This formats the farmer basic info into the mapping payload since this data is not obtained from the form/answer survey module
                                                            if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.FARMER_TABLE)) {
                                                                formatFarmerObjectData(farmer, arrayOfValues);

                                                                imagesArrayOfValues = new JSONArray(arrayOfValues.toString());
                                                                if (farmerLogs.contains(AppConstants.FARMER_TABLE_PHOTO_FIELD)) {
                                                                    //add farmer profile image
                                                                    imagesArrayOfValues.put(generateAnswerJSONObject(null, AppConstants.FARMER_TABLE_PHOTO_FIELD,
                                                                            (farmer.getImageBase64() != null && !farmer.getImageBase64().isEmpty()) ? farmer.getImageBase64() : "", null));
                                                                }
                                                            }

                                                            //Map rest of data which don't have a specific format
                                                            for (Mapping mapping : mappingEntry.getValue()) {
                                                                Question question = getAppDataManager().getDatabaseManager().questionDao().get(mapping.getQuestionId()).blockingGet();
                                                                if (allAnswersJsonObject.has(question.getLabelC())) {

                                                                    //Separate normal string answers from imageData answers
                                                                    String answer = allAnswersJsonObject.get(question.getLabelC()).toString();
                                                                    if (!answer.equalsIgnoreCase("null")) {

                                                                        JSONObject dataToInsert =
                                                                                generateAnswerJSONObject(question.getTypeC(), mapping.getFieldName(), answer, null);
                                                                        if (dataToInsert.length() > 0)
                                                                            if (photoTypeQuestions.contains(question.getId())) {

                                                                                //Question is a photo type question
                                                                                // Build images payload data

                                                                                if (farmerLogs.contains(question.getLabelC())) {
                                                                                    imagesArrayOfValues.put(dataToInsert);
                                                                                }
                                                                            } else {
                                                                                //Question is not a photo type question
                                                                                if (!answer.isEmpty() && !answer.equals(question.getDefaultValueC()))
                                                                                    arrayOfValues.put(dataToInsert);
                                                                            }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (arrayOfValues.length() > 0)
                                                            jsonObject.put(mappingEntry.getKey(), arrayOfValues);

                                                        if (imagesArrayOfValues.length() > 0) {
                                                            imagesJsonObject.put(mappingEntry.getKey(), imagesArrayOfValues);
                                                            totalNoOfImagesToBeUploaded += imagesArrayOfValues.length();
                                                        }
                                                    }
                                                }
                                                payloadDataArray.put(jsonObject);


                                                //Only add the imagesJsonObject if indeed farmer had some updated image data
                                                //We can check if the farmerLogs.data is empty or not

                                                if (!farmerLogs.getData().isEmpty())
                                                    imagesPayloadDataList.add(imagesJsonObject);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                getView().showMessage(e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            e.printStackTrace();
                                            showGenericError(e);
                                        }

                                        @Override
                                        public void onComplete() {
                                            try {
                                                payloadData.put("data", payloadDataArray);

                                                AppLogger.e(TAG, "***********************************************************");
                                                AppLogger.e(TAG, "No. of images to upload => " + totalNoOfImagesToBeUploaded);
                                                System.out.println();
                                                AppLogger.e(TAG, "data without images => " + payloadData.toString());
                                                AppLogger.e(TAG, "***********************************************************");


                                                UploadDataManager.newInstance(getView(), getAppDataManager(), listener, true)
                                                        .uploadFarmersData(payloadData, imagesPayloadDataList);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                showGenericError(e);
                                            }
                                        }
                                    });
                        }, this::showGenericError))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable -> {
                }, this::showGenericError));
    }

    private void showGenericError(Throwable throwable) {
        getView().hideLoading();
        getView().showMessage(throwable.getLocalizedMessage());
    }

    protected void formatFarmerObjectData(Farmer farmer, JSONArray arrayOfValues) {
        //Country country = getGson().fromJson(getAppDataManager().getStringValue("country"), Country.class);

        //Generate VillageId json
        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.FARMER_TABLE_COUNTRY_ADMIN_LEVEL_FIELD,
                farmer.getVillageId(), null));
        //Generate farmer name json
        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.FARMER_TABLE_NAME_FIELD,
                farmer.getFarmerName(), null));
        //Generate farmer external id json
        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.FARMER_TABLE_EXTERNAL_ID_FIELD,
                farmer.getCode(), null));
        //Generate farmer code json
        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.FARMER_TABLE_CODE_FIELD,
                farmer.getCode(), null));
        //Generate farmer education level json
        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.FARMER_TABLE_EDUCATION_LEVEL_FIELD,
                farmer.getEducationLevel(), null));
        //Generate farmer gender json
        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.FARMER_TABLE_GENDER_FIELD,
                farmer.getGender(), null));
        //Generate farmer birth year json
        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.FARMER_TABLE_BIRTHDAY_FIELD,
                farmer.getBirthYear(), null));
    }

    protected void formatPlotsData(Plot plot, JSONArray arrayOfValues) {
        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.EXTERNAL_ID_FIELD,
                plot.getExternalId(), null));

        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.PLOT_NAME_FIELD,
                plot.getName(), null));

        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.PLOT_AREA_FIELD,
                plot.getArea(), null));

        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.PLOT_EST_PROD_FIELD,
                plot.getEstimatedProductionSize(), null));

        arrayOfValues.put(generateAnswerJSONObject(null, AppConstants.PLOT_AGE_FIELD,
                plot.getPlotAge(), null));
    }

    protected void formatPlotsGpsData(Plot plot, JSONArray arrayOfValues) {
        if (plot.getGpsPoints() != null) {
            List<PlotGpsPoint> gpsPoints = plot.getGpsPoints();
            for (PlotGpsPoint point : gpsPoints) {
                JSONArray pointJsonArray = new JSONArray();
                pointJsonArray.put(generateAnswerJSONObject(null, AppConstants.EXTERNAL_ID_FIELD,
                        plot.getExternalId(), null));

                pointJsonArray.put(generateAnswerJSONObject(null, AppConstants.PLOT_GPS_POINT_LAT_FIELD,
                        point.getLatitude(), null));

                pointJsonArray.put(generateAnswerJSONObject(null, AppConstants.PLOT_GPS_POINT_LNG_FIELD,
                        point.getLongitude(), null));

                pointJsonArray.put(generateAnswerJSONObject(null, AppConstants.PLOT_GPS_POINT_ALTITUDE_FIELD,
                        point.getAltitude(), null));

                pointJsonArray.put(generateAnswerJSONObject(null, AppConstants.PLOT_GPS_POINT_PRECISION_FIELD,
                        point.getPrecision(), null));

                arrayOfValues.put(pointJsonArray);
            }
        }
    }

    protected JSONObject buildAllAnswersJsonDataPerFarmer(String code) {
        JSONObject jsonObject = new JSONObject();
        for (FormAnswerData answerData : getAppDataManager().getDatabaseManager().formAnswerDao().getAll(code).blockingGet()) {
            Iterator<String> iterator = answerData.getJsonData().keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
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

    private JSONObject generateModuleData(String plotExternalId, String externalId, String moduleType) {
        JSONObject diagnosticInfoJSONObject = new JSONObject();
        try {
            diagnosticInfoJSONObject.put(AppConstants.TYPE_FIELD_NAME, moduleType);
            diagnosticInfoJSONObject.put(AppConstants.DIAGNOSTIC_MONITORING_EXTERNAL_ID_C, externalId);
            diagnosticInfoJSONObject.put(AppConstants.PLOT_EXTERNAL_ID_FIELD, plotExternalId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return diagnosticInfoJSONObject;
    }

    private JSONObject generateAnswerJSONObject(String questionType, String fieldName, Object answerValue, String variableLabel) {
        JSONObject answerJson;
        answerJson = new JSONObject();
        try {
            Object answer = answerValue;

            //For decimal values, add answer to payload as a decimal instead of as a string
            if (answerValue.toString().matches("-?\\d+(\\.\\d+)?"))
                answer = Double.parseDouble(answerValue.toString().trim().replace(",", ""));

            answerJson.put("answer", answer);
            answerJson.put("field_name", fieldName);

            if (variableLabel != null)
                answerJson.put("variable_c", variableLabel);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return answerJson;
    }

}