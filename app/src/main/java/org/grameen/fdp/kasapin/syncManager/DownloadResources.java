package org.grameen.fdp.kasapin.syncManager;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.District;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.data.db.model.CountryAdminLevelDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.QuestionsAndSkipLogic;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.data.network.model.FarmerAndAnswers;
import org.grameen.fdp.kasapin.data.network.model.SyncDownData;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;
public class DownloadResources {
    private int TOTAL_COUNT = 0;
    private int INDEX = 0;
    private String TAG = "DownloadResources";
    private boolean showProgress;
    private FdpCallbacks.OnDownloadResourcesListener onDownloadResourcesListener;
    private BaseContract.View mView;
    private AppDataManager mAppDataManager;

    private DownloadResources(BaseContract.View view, AppDataManager appDataManager, FdpCallbacks.OnDownloadResourcesListener listener, boolean showProgress) {

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

    public void getCommunitiesData() {
        if (showProgress)
            getView().setLoadingMessage("Getting communities data...");
        Country country = getGson().fromJson(getAppDataManager().getStringValue("country"), Country.class);
        getAppDataManager().getCompositeDisposable().add(mAppDataManager.getFdpApiService()
                .fetchCommunitiesData(country.getId(), mAppDataManager.getAccessToken())
                .subscribeWith(new DisposableSingleObserver<CountryAdminLevelDataWrapper>() {
                    @Override
                    public void onSuccess(CountryAdminLevelDataWrapper dataWrapper) {

                        if(dataWrapper.getData() != null)
                        Observable.fromIterable(dataWrapper.getData())
                                .subscribeOn(Schedulers.io())
                                .doOnNext(district -> {
                                    getAppDataManager().getDatabaseManager().districtsDao().insertOne(district);

                                    if(district.getCommunities() != null)
                                    getAppDataManager().getDatabaseManager().villagesDao().insertAll(district.getCommunities());
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableObserver<District>() {
                                    @Override
                                    public void onNext(District district) {}
                                    @Override
                                    public void onError(Throwable e) {
                                        showError(e);
                                    }
                                    @Override
                                    public void onComplete() {
                                        getSurveyData();
                                    }
                                });
                        else
                            getSurveyData();
                    }
                    @Override
                    public void onError(Throwable e) {
                        showError(e);
                    }
                }));
    }

    private void getSurveyData() {

        if (showProgress)
            getView().setLoadingMessage("Getting survey data...");


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
                .fetchSyncDownData(mAppDataManager.getAccessToken(), country.getId(), getAppDataManager().getUserId(), INDEX, AppConstants.BATCH_NO)
                .subscribe(new DisposableSingleObserver<SyncDownData>() {
                    @Override
                    public void onSuccess(SyncDownData syncDownData) {
                        if(syncDownData.getSuccess() != null && syncDownData.getSuccess().trim().equalsIgnoreCase("true")) {
                            TOTAL_COUNT = syncDownData.getTotal_count();
                            //check for total count here against pageDown/pageEnd and loop method getFarmersData
                            if(syncDownData.getData() != null && syncDownData.getData().size() > 0) {
                                INDEX += syncDownData.getData().size();
                                for (FarmerAndAnswers farmerAndAnswers1 : syncDownData.getData()) {
                                    if (getAppDataManager().getDatabaseManager().realFarmersDao().checkIfFarmerExists(farmerAndAnswers1.getFarmer().getCode()) == 0) {
                                        //Todo replace village id with country admin level fromm the server
                                        getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(farmerAndAnswers1.getFarmer());
                                        getAppDataManager().getDatabaseManager().formAnswerDao().insertAll(farmerAndAnswers1.getAnswers());
                                        if (farmerAndAnswers1.getPlotDetails() != null && farmerAndAnswers1.getPlotDetails().size() > 0)
                                            for (List<Plot> plots : farmerAndAnswers1.getPlotDetails()) {
                                                for (Plot p : plots) {
                                                    getAppDataManager().getDatabaseManager().plotsDao().insertOne(p);

                                                    if (p.getMonitoringList() != null)
                                                        getAppDataManager().getDatabaseManager().monitoringsDao().insertAll(p.getMonitoringList());
                                                }
                                            }
                                    }
                                }
                                if(TOTAL_COUNT != (INDEX - 1)) {
                                    showProgress = false;
                                    getView().setLoadingMessage("Downloading next batch (" + INDEX + "/" + TOTAL_COUNT  + ") of farmer and answers data...\nNB: This will not replace already existing farmer data!");
                                    getFarmersData();
                                }
                            }
                            showSuccess("Data download completed!");
                        }else onError(new Throwable("The download could not complete!"));
                    }
                    @Override
                    public void onError(Throwable e) {
                        showError(e);
                    }
                });
    }

    private void showError(Throwable e) {
        if (onDownloadResourcesListener != null)
            onDownloadResourcesListener.onError(e);
        onDownloadResourcesListener = null;
    }

    private void showSuccess(String message) {
        if (onDownloadResourcesListener != null)
            onDownloadResourcesListener.onSuccess(message);
        onDownloadResourcesListener = null;
    }
}
