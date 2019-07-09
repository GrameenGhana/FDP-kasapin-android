package org.grameen.fdp.kasapin.syncManager;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.data.db.entity.Village;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.QuestionsAndSkipLogic;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.data.network.model.FarmerAndAnswers;
import org.grameen.fdp.kasapin.data.network.model.SyncDownData;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;


public class DownloadResources {

    String TAG = "DownloadResources";
    boolean showProgress;
    private FdpCallbacks.OnDownloadResourcesListener onDownloadResourcesListener;
    private BaseContract.View mView;
    private AppDataManager mAppDataManager;


    public DownloadResources(BaseContract.View view, AppDataManager appDataManager, FdpCallbacks.OnDownloadResourcesListener listener, boolean showProgress) {

        this.mAppDataManager = appDataManager;
        this.mView = view;
        this.showProgress = showProgress;
        this.onDownloadResourcesListener = listener;
    }

    public static DownloadResources newInstance(BaseContract.View view, AppDataManager appDataManager,
                                                FdpCallbacks.OnDownloadResourcesListener listener, boolean showProgress) {
        return new DownloadResources(view, appDataManager, listener, showProgress);
    }

    public AppDataManager getAppDataManager() {
        return mAppDataManager;
    }

    public BaseContract.View getView() {
        return mView;
    }


    public void getSurveyData() {

        if (showProgress)
            getView().setLoadingMessage("Getting survey data...");

        List<Village> villageList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {

            Village v = new Village();
            v.setId(i);
            v.setCountryId(1);
            v.setDistrict("District " + i);
            v.setName("Village " + i);
            villageList.add(v);
        }


        Country country = getGson().fromJson(getAppDataManager().getStringValue("country"), Country.class);


        getAppDataManager().getCompositeDisposable().add(mAppDataManager.getFdpApiService()
                .fetchSurveyData(country.getId(), mAppDataManager.getAccessToken())
                .subscribeWith(new DisposableSingleObserver<FormsDataWrapper>() {
                    @Override
                    public void onSuccess(FormsDataWrapper dataWrapper) {

                        Observable.fromIterable(dataWrapper.getData())
                                .subscribeOn(Schedulers.io())
                                .doOnNext(formTranslation -> {

                                    Form form = formTranslation.getForm();
                                    form.setTranslation(formTranslation.getName());
                                    form.setTranslationId(formTranslation.getId());

                                    getAppDataManager().getDatabaseManager().formsDao().insertForm(formTranslation.getForm());
                                })
                                .flatMap(formTranslation -> Observable.fromIterable(formTranslation.getQuestionsAndSkipLogics()))
                                .doOnNext(questionsAndSkipLogic -> {


                                    getAppDataManager().getDatabaseManager().questionDao().insertQuestion(questionsAndSkipLogic.getQuestion());
                                    getAppDataManager().getDatabaseManager().skipLogicsDao().insertAll(questionsAndSkipLogic.getSkiplogic());

                                    if (questionsAndSkipLogic.getMap() != null && !questionsAndSkipLogic.getMap().isEmpty())
                                        getAppDataManager().getDatabaseManager().mappingDao().insertAll(questionsAndSkipLogic.getMap());

                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableObserver<QuestionsAndSkipLogic>() {
                                    @Override
                                    public void onNext(QuestionsAndSkipLogic questionsAndSkipLogic) {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        onError(e);
                                    }

                                    @Override
                                    public void onComplete() {
                                        getAppDataManager().getDatabaseManager().villagesDao().insertAll(villageList);
                                        getRecommendationsData();
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {

                        showError(e);

                    }
                }));
    }


    private void getRecommendationsData() {
        if (showProgress)
            getView().setLoadingMessage("Getting recommendations, calculations and recommendations plus activities data...");


        Country country = getGson().fromJson(getAppDataManager().getStringValue("country"), Country.class);
        getAppDataManager().getFdpApiService()
                .fetchRecommendations(1, country.getId(), mAppDataManager.getAccessToken())
                .subscribe(new DisposableSingleObserver<RecommendationsDataWrapper>() {
                    @Override
                    public void onSuccess(RecommendationsDataWrapper recommendationsDataWrapper) {
                        Observable.fromIterable(recommendationsDataWrapper.getData())
                                .doOnNext(recommendation -> {
                                    getAppDataManager().getDatabaseManager().recommendationsDao().insertOne(recommendation);
                                    getAppDataManager().getDatabaseManager().calculationsDao().insertAll(recommendation.getCalculations());
                                    getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao().insertAll(recommendation.getRecommendationActivities());
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableObserver<Recommendation>() {
                                    @Override
                                    public void onNext(Recommendation recommendation) {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                        onError(e);
                                    }

                                    @Override
                                    public void onComplete() {
                                        getFarmersData();

                                       // showSuccess("Data download completed!");



                                    }
                                });

                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e);

                    }
                });
    }



    public void getFarmersData() {

        if (showProgress)
            getView().setLoadingMessage("Getting farmer and answers data...\nNB: This will not replace already existing farmer data!");

        Country country = getGson().fromJson(getAppDataManager().getStringValue("country"), Country.class);

        getAppDataManager().getFdpApiService()
                .fetchSyncDownData(mAppDataManager.getAccessToken(), country.getId(), getAppDataManager().getUserId(), 0, 0)
                .subscribe(new DisposableSingleObserver<SyncDownData>() {
                    @Override
                    public void onSuccess(SyncDownData syncDownData) {

                        AppLogger.e(TAG, "******************************************");

                        AppLogger.e(TAG, "OnSuccess " + syncDownData.getSuccess());
                        AppLogger.e(TAG, "SYNC DOWN DATA SIZE " + syncDownData.getData().size());
                        AppLogger.e(TAG, "******************************************");

                        if(syncDownData.getSuccess().trim().equalsIgnoreCase("true")) {

                            //check for total count here against pageDown/pageEnd and loop method getFarmersData

                            if(syncDownData.getData() != null && syncDownData.getData().size() > 0)
                            for (FarmerAndAnswers farmerAndAnswers1 : syncDownData.getData()) {

                                /**
                                 * Check if farmer exists or not
                                 * if farmer exists, skip else save
                                **/

                                if(getAppDataManager().getDatabaseManager().realFarmersDao().checkIfFarmerExists(farmerAndAnswers1.getFarmer().getCode()) == 0) {

                                    //Todo replace village id with country admin level fromm the server
                                    farmerAndAnswers1.getFarmer().setVillageId(1);

                                    getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(farmerAndAnswers1.getFarmer());
                                    getAppDataManager().getDatabaseManager().formAnswerDao().insertAll(farmerAndAnswers1.getAnswers());

                                    if (farmerAndAnswers1.getPlotDetails() != null && farmerAndAnswers1.getPlotDetails().size() > 0)
                                        for (List<Plot> plots : farmerAndAnswers1.getPlotDetails())
                                            getAppDataManager().getDatabaseManager().plotsDao().insertAll(plots);
                                }
                            }

                            showSuccess("Data download completed!");

                        }else onError(new Throwable("An error occurred!"));
                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e);

                    }
                });
    }




    void showError(Throwable e) {
        if (onDownloadResourcesListener != null)
            onDownloadResourcesListener.onError(e);

        onDownloadResourcesListener = null;
    }

    void showSuccess(String message) {
        if (onDownloadResourcesListener != null)
            onDownloadResourcesListener.onSuccess(message);
        onDownloadResourcesListener = null;

    }


}
