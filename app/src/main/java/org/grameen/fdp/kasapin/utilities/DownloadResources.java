package org.grameen.fdp.kasapin.utilities;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.data.db.entity.Village;
import org.grameen.fdp.kasapin.data.db.model.DataWrapper;
import org.grameen.fdp.kasapin.data.db.model.QuestionsAndSkipLogic;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class DownloadResources {

    private FdpCallbacks.OnDownloadResourcesListener onDownloadResourcesListener;

    String TAG = "DownloadResources";
    private BaseContract.View mView;
    private AppDataManager mAppDataManager;
    boolean showProgress;


    public static DownloadResources newInstance(BaseContract.View view, AppDataManager appDataManager,
                                                FdpCallbacks.OnDownloadResourcesListener listener, boolean showProgress){
        return new DownloadResources(view, appDataManager, listener, showProgress);
    }

    public DownloadResources(BaseContract.View view, AppDataManager appDataManager, FdpCallbacks.OnDownloadResourcesListener listener, boolean showProgress){

        this.mAppDataManager = appDataManager;
        this.mView = view;
        this.showProgress = showProgress;
        this.onDownloadResourcesListener = listener;
    }


    public AppDataManager getAppDataManager() {
        return mAppDataManager;
    }

    public BaseContract.View getView() {
        return mView;
    }


    public void getData(){

        if(showProgress)
        getView().setLoadingMessage("Getting survey data...");

        List<Village> villageList = new ArrayList<>();
        for(int i =0; i < 10; i++){

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



        List<Question> QUESTIONS = new ArrayList<>();
        List<SkipLogic> SKIP_LOGICS = new ArrayList<>();
        List<Form> FORMS = new ArrayList<>();


        getAppDataManager().getCompositeDisposable().add(mAppDataManager.getFdpApiService()
                .fetchSurveyData(1, mAppDataManager.getAccessToken())
                .subscribeWith(new DisposableSingleObserver<DataWrapper>() {
                    @Override
                    public void onSuccess(DataWrapper dataWrapper) {

                        Observable.fromIterable(dataWrapper.getData())
                                .flatMap(formTranslation -> {

                                    FORMS.add(formTranslation.getForm());

                                    return Observable.fromIterable(formTranslation.getQuestionsAndSkipLogics());
                                })
                                .map(questionsAndSkipLogic -> {

                                    QUESTIONS.add(questionsAndSkipLogic.getQuestion());

                                    SKIP_LOGICS.addAll(questionsAndSkipLogic.getSkiplogic());
                                    return questionsAndSkipLogic;
                                }).subscribe(new DisposableObserver<QuestionsAndSkipLogic>() {
                            @Override
                            public void onNext(QuestionsAndSkipLogic questionsAndSkipLogic) {}

                            @Override
                            public void onError(Throwable e) {

                                if(onDownloadResourcesListener != null)
                                    onDownloadResourcesListener.onError(e);


                            }

                            @Override
                            public void onComplete() {

                                AppLogger.i(TAG, "ON COMPLETE");
                                AppLogger.i(TAG, "FORMS SIZE = " + FORMS.size());
                                AppLogger.i(TAG, "QUESTIONS SIZE = " + QUESTIONS.size());
                                AppLogger.i(TAG, "SKIP LOGIC SIZE = " + SKIP_LOGICS.size());
                                AppLogger.i(TAG, "FORM TRANSLATIONS = " + dataWrapper.getData().size());
                                AppLogger.i(TAG, "VILLAGE LIST = " + villageList.size());


                                Completable one = Completable.fromAction(() -> {
                                    getAppDataManager().getDatabaseManager()
                                            .formsDao().insertAll(FORMS);


                                    getAppDataManager().getDatabaseManager().formTranslationDao()
                                            .insertAll(dataWrapper.getData());

                                    getAppDataManager().getDatabaseManager()
                                            .questionDao().insertAll(QUESTIONS);

                                    getAppDataManager().getDatabaseManager()
                                            .skipLogicsDao().insertAll(SKIP_LOGICS);

                                    getAppDataManager().getDatabaseManager()
                                            .countryDao().insertOne(country);

                                    getAppDataManager().getDatabaseManager().villagesDao()
                                            .insertAll(villageList);



                                });



                                Completable.concatArray(one)
                                        .observeOn(Schedulers.io()) // OFF UI THREAD
                                        .subscribeOn(Schedulers.single())
                                        .subscribe(new CompletableObserver() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                AppLogger.i(TAG, "**** On Subscribe");

                                                if(d.isDisposed())
                                                    getAppDataManager().getCompositeDisposable().add(d);
                                            }
                                            @Override
                                            public void onComplete() {

                                                if(onDownloadResourcesListener != null)
                                                    onDownloadResourcesListener.onSuccess();
                                            }
                                            @Override
                                            public void onError(Throwable e) {
                                                if(onDownloadResourcesListener != null)
                                                    onDownloadResourcesListener.onError(e);
                                                }
                                        });
                            }
                        });
                    }
                    @Override
                    public void onError(Throwable e) {

                        if(onDownloadResourcesListener != null)
                            onDownloadResourcesListener.onError(e);

                    }
                }));
    }




}
