package org.grameen.fdp.kasapin.syncManager;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Calculation;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.Mapping;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.data.db.entity.RecommendationActivity;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.data.db.entity.Village;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;


public class DownloadResourcesBak {

    String TAG = "DownloadResources";
    boolean showProgress;
    private FdpCallbacks.OnDownloadResourcesListener onDownloadResourcesListener;
    private BaseContract.View mView;
    private AppDataManager mAppDataManager;


    public DownloadResourcesBak(BaseContract.View view, AppDataManager appDataManager, FdpCallbacks.OnDownloadResourcesListener listener, boolean showProgress) {

        this.mAppDataManager = appDataManager;
        this.mView = view;
        this.showProgress = showProgress;
        this.onDownloadResourcesListener = listener;
    }

    public static DownloadResourcesBak newInstance(BaseContract.View view, AppDataManager appDataManager,
                                                   FdpCallbacks.OnDownloadResourcesListener listener, boolean showProgress) {
        return new DownloadResourcesBak(view, appDataManager, listener, showProgress);
    }

    public AppDataManager getAppDataManager() {
        return mAppDataManager;
    }

    public BaseContract.View getView() {
        return mView;
    }


    public void getSurveyData() {

        List<Village> villages = new ArrayList<>();
        List<Form> forms = new ArrayList<>();
        List<Question> questions = new ArrayList<>();
        List<SkipLogic> skipLogics = new ArrayList<>();
        List<Mapping> mappings = new ArrayList<>();


        if (showProgress)
            getView().setLoadingMessage("Getting survey data...");

        for (int i = 0; i < 10; i++) {

            Village v = new Village();
            v.setId(i);
            v.setCountryId(1);
            v.setDistrict("District " + i);
            v.setName("Village " + i);
            villages.add(v);
        }


        Country country = new Country();
        country.setId(1);
        country.setCurrency("GHS");
        country.setIsoCode("GHA");
        country.setName("Ghana");


        getAppDataManager().getCompositeDisposable().add(mAppDataManager.getFdpApiService()
                .fetchSurveyData(country.getId(), mAppDataManager.getAccessToken())
                .subscribeWith(new DisposableSingleObserver<FormsDataWrapper>() {
                    @Override
                    public void onSuccess(FormsDataWrapper dataWrapper) {

                        getAppDataManager().getCompositeDisposable().add(Observable.fromIterable(dataWrapper.getData())
                                .subscribeOn(Schedulers.io())
                                .doOnNext(formTranslation -> {

                                    forms.add(formTranslation.getForm());

                                })
                                .flatMap(formTranslation -> Observable.fromIterable(formTranslation.getQuestionsAndSkipLogics()))
                                .doOnNext(questionsAndSkipLogic -> {

                                    questions.add(questionsAndSkipLogic.getQuestion());

                                    skipLogics.addAll(questionsAndSkipLogic.getSkiplogic());

                                    if (questionsAndSkipLogic.getMap() != null && !questionsAndSkipLogic.getMap().isEmpty())
                                        mappings.addAll(questionsAndSkipLogic.getMap());


                                })
                                .subscribe(questionsAndSkipLogic -> {

                                    Completable one = Completable.fromAction(() -> {
                                        getAppDataManager().getDatabaseManager().villagesDao().insertAll(villages);
                                        getAppDataManager().getDatabaseManager().formsDao().insertAll(forms);
                                        getAppDataManager().getDatabaseManager().questionDao().insertAll(questions);
                                        getAppDataManager().getDatabaseManager().skipLogicsDao().insertAll(skipLogics);
                                        getAppDataManager().getDatabaseManager().mappingDao().insertAll(mappings);

                                    });

                                    Completable.concatArray(one)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.single())
                                            .subscribe(new CompletableObserver() {
                                                @Override
                                                public void onSubscribe(Disposable d) {
                                                    AppLogger.e(TAG, ">>>>>>>  ON SUBSCRIBE");

                                                    if (d.isDisposed())
                                                        getAppDataManager().getCompositeDisposable().add(d);
                                                }

                                                @Override
                                                public void onComplete() {
                                                    AppLogger.e(TAG, ">>>>>>>  ON COMPLETE");
                                                    getRecommendationsData();

                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    AppLogger.e(TAG, ">>>>>>>  ON ERROR");

                                                    showError(e);
                                                }
                                            });


                                }, this::onError));
                    }

                    @Override
                    public void onError(Throwable e) {

                        showError(e);

                    }
                }));
    }


    private void getRecommendationsData() {

        List<Recommendation> recommendations = new ArrayList<>();
        List<Calculation> calculations = new ArrayList<>();
        List<RecommendationActivity> recommendationActivities = new ArrayList<>();


        if (showProgress)
            getView().setLoadingMessage("Getting recommendations, calculations and recommendations plus activities data...");

        Country country = getGson().fromJson(getAppDataManager().getStringValue("country"), Country.class);


        getAppDataManager().getFdpApiService()
                .fetchRecommendations(1, country.getId(), mAppDataManager.getAccessToken())
                .subscribe(new DisposableSingleObserver<RecommendationsDataWrapper>() {
                    @Override
                    public void onSuccess(RecommendationsDataWrapper recommendationsDataWrapper) {

                        getAppDataManager().getCompositeDisposable().add(Observable.fromIterable(recommendationsDataWrapper.getData())
                                .doOnNext(recommendation -> {

                                    recommendations.add(recommendation);
                                    calculations.addAll(recommendation.getCalculations());
                                    recommendationActivities.addAll(recommendation.getRecommendationActivities());

                                })
                                .subscribe(questionsAndSkipLogic -> {


                                    Completable one = Completable.fromAction(() -> {
                                        getAppDataManager().getDatabaseManager().recommendationsDao().insertAll(recommendations);
                                        getAppDataManager().getDatabaseManager().calculationsDao().insertAll(calculations);
                                        getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao().insertAll(recommendationActivities);

                                    });

                                    Completable.concatArray(one)
                                            .observeOn(AndroidSchedulers.mainThread()) // OFF UI THREAD
                                            .subscribeOn(Schedulers.single())
                                            .subscribe(new CompletableObserver() {
                                                @Override
                                                public void onSubscribe(Disposable d) {
                                                    AppLogger.e(TAG, ">>>>>>>  ON SUBSCRIBE");

                                                    if (d.isDisposed())
                                                        getAppDataManager().getCompositeDisposable().add(d);
                                                }

                                                @Override
                                                public void onComplete() {
                                                    AppLogger.e(TAG, ">>>>>>>  ON COMPLETE");

                                                    showSuccess("Data download completed!");
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    AppLogger.e(TAG, ">>>>>>>  ON ERROR");
                                                    showError(e);
                                                }
                                            });
                                }, this::onError));

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
