package org.grameen.fdp.kasapin.ui.main;


import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Mapping;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Submission;
import org.grameen.fdp.kasapin.data.db.entity.VillageAndFarmers;
import org.grameen.fdp.kasapin.syncManager.DownloadResources;
import org.grameen.fdp.kasapin.syncManager.UploadData;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.ui.base.model.MySearchItem;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter,
        FdpCallbacks.OnDownloadResourcesListener, FdpCallbacks.UploadDataListener {

    AppDataManager mAppDataManager;
    int count = 0;


    @Inject
    public MainPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }

    @Override
    public void getFarmerProfileFormAndQuestions() {

        runSingleCall(getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByName(AppConstants.FARMER_PROFILE)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(formAndQuestion -> getView().openAddNewFarmerActivity(formAndQuestion), throwable -> {
                    getView().showMessage(R.string.error_has_occurred);
                    throwable.printStackTrace();
                }));
    }

    @Override
    public void getFormsAndQuestionsData() {

        runSingleCall(getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByType(AppConstants.DIAGNOSTIC, AppConstants.DISPLAY_TYPE_FORM)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAndQuestions -> getView().cacheFormsAndQuestionsData(formAndQuestions), throwable -> {
                    getView().showMessage(R.string.error_has_occurred);
                    throwable.printStackTrace();
                }));


    }

    @Override
    public void startDelay(long delayTime) {

    }

    @Override
    public void openSearchDialog() {
        //Todo get list of farmers and ids, populate into search dialog

        getView().showSearchDialog(null);

    }

    @Override
    public void toggleDrawer() {
        getView().toggleDrawer();
    }

    @Override
    public void getVillagesData() {

        AppLogger.e(TAG, "Getting villages data!");


        runSingleCall(getAppDataManager().getDatabaseManager().villageAndFarmersDao().getVillagesAndFarmers()
                .filter(villageAndFarmers -> villageAndFarmers != null && villageAndFarmers.size() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(villageAndFarmers -> {

                    if (villageAndFarmers.size() > 0) {
                        AppLogger.i(TAG, "VILLAGES & FARMERS SIZE IS " + villageAndFarmers.size());
                        getView().setFragmentAdapter(villageAndFarmers);

                    } else {
                        AppLogger.i(TAG, "VILLAGES & FARMERS SIZE IS EMPTY " + villageAndFarmers.size());

                    }
                }, Throwable::printStackTrace));

    }

    @Override
    public void downloadData(boolean showProgress) {

        if (showProgress)
            getView().showLoading("Syncing data", "Please wait...", true, 0, false);

        DownloadResources.newInstance(getView(), mAppDataManager, this, showProgress).getSurveyData();

    }

    @Override
    public void syncData(boolean showProgress) {

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

        if (getAppDataManager().getDatabaseManager().realFarmersDao().checkIfUnsyncedAvailable().blockingGet() <= 0) {
            getView().showMessage(R.string.no_new_data);
            return;
        }


        if (showProgress)
            getView().showLoading("Uploading data", "Please wait...", false, 0, false);


        JSONObject payloadData = new JSONObject();

        Submission submission = new Submission();
        //Todo pass user id
        submission.setSurveyor__c(1);

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

                            Observable.fromIterable(getAppDataManager().getDatabaseManager().realFarmersDao().getAllNotSynced().blockingGet())
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

<<<<<<< HEAD


                                                                JSONObject recommendationAnswerJson = new JSONObject();
                                                                recommendationAnswerJson.put("answer", plot.getRecommendationId());
                                                                recommendationAnswerJson.put("field_name", "recommendation_id");
                                                                diagnosticMonitoringArray.put(recommendationAnswerJson);


=======
                                                                recommendationJson.put("answer",plot.getRecommendationId());
                                                                recommendationJson.put("field_name","recommendation_id");
                                                                diagnosticMonitoringArray.put(recommendationJson);
>>>>>>> 23e6f44cbd4a489cb7a2a3679c1df746d21ae7a2

                                                                arrayOfValues.put(diagnosticMonitoringArray);
                                                            }

                                                        } else if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.OBSERVATION_TABLE)) {



                                                            for (Plot plot : farmersPlots) {
                                                                JSONArray observationArray = new JSONArray();

                                                                for (Mapping mapping : mappingEntry.getValue()) {

<<<<<<< HEAD

                                                                    Question ao_question = getAppDataManager().getDatabaseManager().questionDao().getQuestionById(mapping.getQuestionId());

                                                                   // String questionLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel(mapping.getQuestionId()).blockingGet();

                                                                    if (plot.getAOJsonData().has(ao_question.getLabelC())) {
                                                                        JSONObject answerJson = new JSONObject();

                                                                        answerJson.put("answer", plot.getAOJsonData().get(ao_question.getLabelC()));
                                                                        answerJson.put("field_name", mapping.getFieldName());
                                                                        answerJson.put("variable_c", ao_question.getCaptionC());

=======
                                                                   // String questionLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel(mapping.getQuestionId()).blockingGet();
                                                                    Question aoQuestion = getAppDataManager().getDatabaseManager().questionDao().getQuestionById(mapping.getQuestionId());

                                                                    if (plot.getAOJsonData().has(aoQuestion.getLabelC())) {
                                                                        JSONObject answerJson = new JSONObject();

                                                                        answerJson.put("answer", plot.getAOJsonData().get(aoQuestion.getLabelC()));
                                                                        answerJson.put("field_name", mapping.getFieldName());
                                                                        answerJson.put("variable_c",aoQuestion.getCaptionC());
>>>>>>> 23e6f44cbd4a489cb7a2a3679c1df746d21ae7a2

                                                                        observationArray.put(answerJson);
                                                                    }
                                                                }

                                                                arrayOfValues.put(observationArray);
                                                            }

                                                        } else {


                                                            if (mappingEntry.getKey().equalsIgnoreCase(AppConstants.FARMER_TABLE))
                                                                formatFarmerObjectData(farmer, arrayOfValues);

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

                                             UploadData.newInstance(getView(), getAppDataManager(), MainPresenter.this, true)
                                                       .uploadFarmersData(payloadData);

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

    private void formatFarmerObjectData(RealFarmer farmer, JSONArray arrayOfValues) {

        JSONObject farmerDataJson;

        try {
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


    private void formatPlotsData(Plot plot, JSONArray arrayOfValues) {

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


    private JSONObject buildAllAnswersJsonDataPerFarmer(String code) {
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

    @Override
    public void initializeSearchDialog(List<VillageAndFarmers> villageAndFarmers) {
        ArrayList<MySearchItem> farmerNames = new ArrayList<>();
        AppLogger.e(TAG, "Initializing search dialog!");


        runSingleCall(Observable.fromIterable(villageAndFarmers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(villageAndFarmers1 -> {

                    count++;

                    if (villageAndFarmers1.getFarmerList().size() > 0)
                        Observable.fromIterable(villageAndFarmers1.getFarmerList())
                                .map(farmer -> new MySearchItem(farmer.getCode(), farmer.getFarmerName()))
                                .toList()
                                .subscribe(new DisposableSingleObserver<List<MySearchItem>>() {
                                    @Override
                                    public void onSuccess(List<MySearchItem> mySearchItems) {

                                        farmerNames.addAll(mySearchItems);
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                });
                    return farmerNames;
                })
                .subscribe(items -> {

                    if (count == villageAndFarmers.size() - 1)
                        getView().instantiateSearchDialog(farmerNames);

                }));
    }


    @Override
    public void getFarmer(String farmerCode) {

        runSingleCall(getAppDataManager().getDatabaseManager().realFarmersDao().get(farmerCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(farmer -> {

                    if (farmer != null)
                        getView().viewFarmerProfile(farmer);

                    else
                        getView().showMessage("Could not load farmer data");

                }, throwable -> {

                    throwable.printStackTrace();
                    AppLogger.e(TAG, throwable.getMessage());


                }));


    }


    //Download Data Callbacks declared at the global level
    @Override
    public void onSuccess(String message) {
        //On download data success.
        AppLogger.i(TAG, "**** ON SUCCESS");
        getView().hideLoading();
        getView().showMessage(message);
    }

    //Download Data Callbacks declared at the global level
    @Override
    public void onError(Throwable throwable) {
        //On download data error
        AppLogger.i(TAG, "**** ON ERROR");

        getView().hideLoading();
        getView().showMessage(throwable.getMessage());

        throwable.printStackTrace();

        if (throwable.getMessage().contains("401")) {
            getView().openLoginActivityOnTokenExpire();
        }
    }


    //Upload Data Callbacks declared at the global level
    @Override
    public void onUploadComplete(String message) {
        AppLogger.i(TAG, "**** ON SUCCESS");
        getView().hideLoading();
        getView().showMessage(message);
    }

    //Upload Data Callbacks declared at the global level
    @Override
    public void onUploadError(Throwable throwable) {
        AppLogger.i(TAG, "**** ON ERROR");

        getView().hideLoading();
        getView().showMessage(throwable.getMessage());
        throwable.printStackTrace();

    }

}
