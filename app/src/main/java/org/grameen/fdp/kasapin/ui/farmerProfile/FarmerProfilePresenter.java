package org.grameen.fdp.kasapin.ui.farmerProfile;


import android.view.ContextThemeWrapper;
import android.widget.Button;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Mapping;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Submission;
import org.grameen.fdp.kasapin.syncManager.UploadData;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.ui.main.MainPresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.FILTERED_FORMS;
import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FarmerProfilePresenter extends BasePresenter<FarmerProfileContract.View> implements FarmerProfileContract.Presenter, FdpCallbacks.UploadDataListener {

    AppDataManager mAppDataManager;
    int count = 0;


    @Inject
    public FarmerProfilePresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void openNextActivity() {

    }


    @Override
    public void deletePlot(Plot plot) {

        getAppDataManager().getDatabaseManager().plotsDao().deleteOne(plot);
        getView().showMessage("Data deleted!");


    }

    @Override
    public void getFarmersPlots(String farmerCode) {

        AppLogger.i(TAG, "GETTING PLOTS DATA!. >>> FARMER CODE IS " + farmerCode);
        runSingleCall(getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(farmerCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(plots -> getView().setUpFarmersPlotsAdapter(plots), throwable -> {
                    getView().showMessage("Could not obtain plots data.");
                }));
    }


    public void loadDynamicButtons(List<FormAndQuestions> formAndQuestions) {

        count = 0;

        if (getAppDataManager().isMonitoring()){

            runSingleCall(Observable.fromIterable(formAndQuestions)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .filter(formAndQuestions1 -> formAndQuestions1.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_FORM))
                    .filter(formAndQuestions1 -> (!formAndQuestions1.getForm().shouldHide()))
                    .map(formAndQuestions1 -> {

                        FILTERED_FORMS.add(formAndQuestions1);

                        AppLogger.e(TAG, getGson().toJson(formAndQuestions1.getForm()));


                        final Button btn = new Button(new ContextThemeWrapper(getContext(), R.style.PrimaryButton_Monitoring));

                        btn.setTag(count);
                        btn.setText(formAndQuestions1.getForm().getTranslation());
                        btn.setContentDescription(formAndQuestions1.getForm().getTranslation());


                        count++;

                        return btn;

                    }).toList().subscribe(buttons -> getView().addButtons(buttons)
                    ));


        }else {

            runSingleCall(Observable.fromIterable(formAndQuestions)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .filter(formAndQuestions1 -> formAndQuestions1.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_FORM))
                    .filter(formAndQuestions1 ->
                            (!formAndQuestions1.getForm().shouldHide() &&
                                    (formAndQuestions1.getForm().getTypeC().equalsIgnoreCase(AppConstants.DIAGNOSTIC) || formAndQuestions1.getForm().getTypeC().equalsIgnoreCase(AppConstants.DIAGNOSTIC_MONITORING))))
                    .map(formAndQuestions1 -> {

                        FILTERED_FORMS.add(formAndQuestions1);


                        final Button btn = new Button(new ContextThemeWrapper(getContext(), R.style.PrimaryButton));


                        btn.setTag(count);
                        btn.setText(formAndQuestions1.getForm().getTranslation());
                        btn.setContentDescription(formAndQuestions1.getForm().getTranslation());



                        count++;

                        return btn;

                    }).toList().subscribe(buttons -> getView().addButtons(buttons)
                    ));

        }

    }





    @Override
    public void syncFarmerData(RealFarmer farmer, boolean showProgress) {

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

        if (farmer.getSyncStatus() == AppConstants.SYNC_OK) {
            getView().showMessage(R.string.no_new_data);
            return;
        }


        if (showProgress)
            getView().showLoading("Uploading " + farmer.getFarmerName() + "'s data", "Please wait...", false, 0, false);


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

                            /**
                             * Group mappings are in the form List<String> List<Mapping>> groupMappings;
                             * First parameter is the Object name (Object_c) eg. farmer_c and List<Mapping> has all mapping with object_c = farmer_c
                             * Since we're sending only farmer data first, lets loop through group mappings and get list of mappings which has object_c = farmer_c;
                             *
                             */

                            Completable.fromAction(() -> {

                                /**
                                 *
                                 * Since the answers are separated by forms in the Answers table. ie. every Form filled has an answer data in the db
                                 * we should get all answers of the farmer and bundle it into one object where we can just get values to questions
                                 * using the question id
                                 *
                                 *
                                 **/

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

                                            }else if(mappingEntry.getKey().equalsIgnoreCase(AppConstants.PLOT_GPS_POINT)){

                                                for (Plot plot : farmersPlots) {
                                                    JSONArray plotArray = new JSONArray();

                                                    formatPlotsGpsData(plot, plotArray);
                                                    arrayOfValues.put(plotArray);
                                                }

                                            } else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.DIAGONOSTIC_MONITORING_TABLE)) {


                                                for (Plot plot : farmersPlots) {
                                                    JSONArray diagnosticMonitoringArray = new JSONArray();
                                                    JSONObject recommendationJson = new JSONObject();

                                                    for (Mapping mapping : mappingEntry.getValue()) {

                                                        String questionLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel(mapping.getQuestionId()).blockingGet();

                                                        if (plot.getAOJsonData().has(questionLabel)) {
                                                            JSONObject answerJson = new JSONObject();

                                                            answerJson.put("answer", plot.getAOJsonData().get(questionLabel));
                                                            answerJson.put("field_name", mapping.getFieldName());


                                                            diagnosticMonitoringArray.put(answerJson);
                                                        }
                                                    }

                                                    recommendationJson.put("answer", plot.getRecommendationId());
                                                    recommendationJson.put("field_name", "recommendation_id");
                                                    diagnosticMonitoringArray.put(recommendationJson);

                                                    arrayOfValues.put(diagnosticMonitoringArray);
                                                }

                                            } else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.OBSERVATION_TABLE)) {


                                                for (Plot plot : farmersPlots) {
                                                    JSONArray observationArray = new JSONArray();

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

                                                    arrayOfValues.put(observationArray);
                                                }

                                            }  else if(mappingEntry.getKey().equalsIgnoreCase(AppConstants.FARMER_TABLE))
                                                formatFarmerObjectData(farmer, arrayOfValues);

                                            else{
                                                for (Mapping mapping : mappingEntry.getValue()) {

                                                    //AppLogger.i(TAG, "MAPPING OBJECT NAME IS >>> " + mapping.getObjectName());


                                                    String questionLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel(mapping.getQuestionId()).blockingGet();

                                                    if (allAnswersJsonObject.has(questionLabel)) {
                                                        JSONObject answerJson = new JSONObject();


                                                        answerJson.put("answer", allAnswersJsonObject.get(questionLabel));
                                                        answerJson.put("field_name", mapping.getFieldName());

                                                        arrayOfValues.put(answerJson);
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


                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new DisposableCompletableObserver() {
                                        @Override
                                        public void onComplete() {
                                            try {
                                                payloadData.put("data", farmerData);

                                                AppLogger.e(TAG, "Payload data is " + payloadData.toString());
                                                getView().hideLoading();

                                                UploadData.newInstance(getView(), getAppDataManager(), FarmerProfilePresenter.this, true)
                                                        .uploadFarmersData(payloadData);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                getView().hideLoading();
                                                getView().showMessage(R.string.error_has_occurred_loading_data);
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            e.printStackTrace();
                                            getView().hideLoading();
                                            getView().showMessage(e.getMessage());
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


    @Override
    public void onUploadComplete(String message) {
        AppLogger.i(TAG, "**** ON SUCCESS");
        getView().hideLoading();
        getView().showMessage(message);

        getView().updateFarmerSyncStatus();

    }

    @Override
    public void onUploadError(Throwable throwable) {
        AppLogger.i(TAG, "**** ON ERROR");
        getView().hideLoading();
        getView().showMessage(throwable.getMessage());
        throwable.printStackTrace();

    }
}
