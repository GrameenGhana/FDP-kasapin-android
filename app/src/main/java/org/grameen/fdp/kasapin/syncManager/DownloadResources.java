package org.grameen.fdp.kasapin.syncManager;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.AppDatabase;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.District;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.data.db.model.CountryAdminLevelDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.QuestionsAndSkipLogic;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.data.network.model.ServerResponse;
import org.grameen.fdp.kasapin.data.network.model.FarmerAndAnswers;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

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

    private AppDataManager getAppDataManager() {
        return mAppDataManager;
    }

    private AppDatabase getAppDatabase() {
        return getAppDataManager().getDatabaseManager();
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
                        if (dataWrapper.getData() != null)
                            Observable.fromIterable(dataWrapper.getData())
                                    .subscribeOn(Schedulers.io())
                                    .doOnNext(district -> {
                                        getAppDatabase().districtsDao().insertOne(district);

                                        if (district.getCommunities() != null)
                                            getAppDatabase().villagesDao().insertAll(district.getCommunities());
                                    })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new DisposableObserver<District>() {
                                        @Override
                                        public void onNext(District district) {
                                        }

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
                                    getAppDatabase().formsDao().insertForm(formTranslation.getForm());
                                })
                                .flatMap(formTranslation -> Observable.fromIterable(formTranslation.getQuestionsAndSkipLogic()))
                                .doOnNext(questionsAndSkipLogic -> {
                                    getAppDatabase().questionDao().insertQuestion(questionsAndSkipLogic.getQuestion());
                                    getAppDatabase().skipLogicsDao().insertAll(questionsAndSkipLogic.getSkipLogic());

                                    if (questionsAndSkipLogic.getMap() != null && !questionsAndSkipLogic.getMap().isEmpty())
                                        getAppDatabase().mappingDao().insertAll(questionsAndSkipLogic.getMap());
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableObserver<QuestionsAndSkipLogic>() {
                                    @Override
                                    public void onNext(QuestionsAndSkipLogic questionsAndSkipLogic) {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        showError(e);
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
                                    getAppDatabase().recommendationsDao().insertOne(recommendation);
                                    getAppDatabase().calculationsDao().insertAll(recommendation.getCalculations());
                                    getAppDatabase().recommendationPlusActivitiesDao().insertAll(recommendation.getRecommendationActivities());
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableObserver<Recommendation>() {
                                    @Override
                                    public void onNext(Recommendation recommendation) {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        showError(e);
                                    }

                                    @Override
                                    public void onComplete() {
                                        getFarmersData();
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
        int REQUEST_SIZE = 2;

        //Since farmer data from the server might be potentially large, we need to download the data in batches
        //We send 2 requests simultaneously to retrieve 20 records per request
        if (showProgress) {
            getView().setLoadingMessage("Getting farmer and answers data...\nNB: This will not replace already existing farmer data!");
            showProgress = false;
        }


        Country country = getGson().fromJson(getAppDataManager().getStringValue("country"), Country.class);


        AppLogger.e(TAG, "TOTAL COUNT ==> " + TOTAL_COUNT);
        AppLogger.e(TAG, "INDEX ==> " + INDEX);

        //Make a batch request of 2 for 20 records per request
        List<Single<ServerResponse>> singleList = new ArrayList<>();
        for (int i = 0; i < REQUEST_SIZE; i++) {
           singleList.add(getAppDataManager().getFdpApiService()
                    .fetchFarmersData(mAppDataManager.getAccessToken(), country.getId(),
                            getAppDataManager().getUserId(), INDEX, AppConstants.BATCH_NO));
            INDEX += AppConstants.BATCH_NO;
        }

        //This function merges the 2 requests and calls them simultaneously awaiting for their results.
        //The onNext function is called when the results for each request is returned.
        //
        Single.merge(singleList).timeout(90, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSubscriber<ServerResponse>() {
                    @Override
                    public void onNext(ServerResponse serverResponse) {
                        if(TOTAL_COUNT == 0)
                            TOTAL_COUNT = serverResponse.getTotal_count();

                        if(TOTAL_COUNT > INDEX)
                        getView().setLoadingMessage(String.format("Fetching %s out of %s records...", INDEX, TOTAL_COUNT));

                        AppLogger.e(TAG, "Server response size => " + serverResponse.getData().size());
                        processFarmerData(serverResponse);
                    }
                    @Override
                    public void onError(Throwable t) {
                        showError(t);
                    }
                    @Override
                    public void onComplete() {
                        AppLogger.e(TAG, "OnComplete");

                        if (TOTAL_COUNT <= (INDEX - 1)) {
                            showProgress = false;
                            showSuccess("Data download completed!");
                            getAppDataManager().setBooleanValue(AppConstants.IS_FARMER_IMAGES_CACHED, false);
                        } else
                             getFarmersData();
                    }
                });
    }


    private void processFarmerData( ServerResponse syncDownData) {
            //check for total count here against pageDown/pageEnd and loop method getFarmersData
            if (syncDownData.getData() != null && syncDownData.getData().size() > 0) {
                for (FarmerAndAnswers farmerAndAnswers1 : syncDownData.getData()) {
                    if (getAppDatabase().realFarmersDao().checkIfFarmerExists(farmerAndAnswers1.getFarmer().getCode()) == 0) {
                        //Todo replace village id with country admin level fromm the server
                        getAppDatabase().realFarmersDao().insertOne(farmerAndAnswers1.getFarmer());
                        getAppDatabase().formAnswerDao().insertAll(farmerAndAnswers1.getAnswers());
                        if (farmerAndAnswers1.getPlotDetails() != null && farmerAndAnswers1.getPlotDetails().size() > 0)
                            for (List<Plot> plots : farmerAndAnswers1.getPlotDetails()) {
                                for (Plot p : plots) {
                                    getAppDatabase().plotsDao().insertOne(p);
                                    if (p.getMonitoringList() != null)
                                        getAppDatabase().monitoringDao().insertAll(p.getMonitoringList());

                                }
                            }
                    }
                }
            }
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
