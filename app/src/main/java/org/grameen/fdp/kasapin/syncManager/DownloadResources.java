package org.grameen.fdp.kasapin.syncManager;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.data.db.entity.Village;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.QuestionsAndSkipLogic;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


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
        for (int i = 0; i < 10; i++) {

            Village v = new Village();
            v.setId(i);
            v.setCountryId(1);
            v.setDistrict("District " + i);
            v.setName("Village " + i);
            villageList.add(v);
        }


        Country country = new Country();
        country.setId(1);
        country.setCurrency("GHS");
        country.setIsoCode("GHA");
        country.setName("Ghana");


        getAppDataManager().getCompositeDisposable().add(mAppDataManager.getFdpApiService()
                .fetchSurveyData(1, mAppDataManager.getAccessToken())
                .subscribeWith(new DisposableSingleObserver<FormsDataWrapper>() {
                    @Override
                    public void onSuccess(FormsDataWrapper dataWrapper) {

                        Observable.fromIterable(dataWrapper.getData())
                                .subscribeOn(Schedulers.io())
                                .doOnNext(formTranslation -> {
                                    getAppDataManager().getDatabaseManager().formsDao().insertForm(formTranslation.getForm());
                                })
                                .flatMap(formTranslation -> Observable.fromIterable(formTranslation.getQuestionsAndSkipLogics()))
                                .doOnNext(questionsAndSkipLogic -> {

                                    getAppDataManager().getDatabaseManager().villagesDao().insertAll(villageList);
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


        getAppDataManager().getFdpApiService()
                .fetchRecommendations(1, 1, mAppDataManager.getAccessToken())
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
                                        showSuccess("Data download completed!");


                                    }
                                });

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
