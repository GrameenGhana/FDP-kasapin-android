package org.grameen.fdp.kasapin.ui.main;


import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.DataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.ui.base.model.MySearchItem;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.DownloadResources;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter,
        FdpCallbacks.OnDownloadResourcesListener{

    AppDataManager mAppDataManager;
    public List<RealFarmer> FARMERS = new ArrayList<>();



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


      /*  runSingleCall(getAppDataManager().getDatabaseManager().formsDao().getAllForms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(forms -> Observable.fromIterable(forms)
                        .filter(form -> form.getTypeC().equalsIgnoreCase(AppConstants.DIAGNOSTIC) && form.getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_FORM))
                        .map(form -> {
                            if(form.getFormNameC().equalsIgnoreCase(AppConstants.FARMER_PROFILE)){
                                runSingleCall(getAppDataManager().getDatabaseManager().questionDao()
                                        .getQuestionsByForm(form.getId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(questions -> {

                                            form.setQuestionList(questions);
                                            AppLogger.i(TAG, "QUESTIONS FOR FORM Farmer Profile FOUND with size " + form.getQuestionList().size() );
                                            AppLogger.i(TAG, "DATA " + new Gson().toJson(form.getQuestionList()) );


                                        }));
                                return form;
                            }else
                                return form;
                        })
                        .toList()
                    ).subscribe(newForms -> getView().openFarmerDetailsActivity(newForms), throwable -> {
                        getView().showMessage(R.string.error_has_occurred);
                        throwable.printStackTrace();
                }));
*/
/*
        runSingleCall(getAppDataManager().getDatabaseManager().formsDao().getAllForms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(forms -> Observable.fromIterable(forms)
                        .filter(form -> form.getTypeC().equalsIgnoreCase(AppConstants.DIAGNOSTIC) && form.getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_FORM))
                        .doOnNext(form -> {

                            if(form.getFormNameC().equalsIgnoreCase(AppConstants.FARMER_PROFILE)) {
                                runSingleCall(getAppDataManager().getDatabaseManager().questionDao()
                                        .getQuestionsByForm(form.getId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(questions -> {

                                            form.setQuestionList(questions);

                                            AppLogger.i(TAG, "QUESTIONS FOR FORM Farmer Profile FOUND with size " + questions.size() );
                                            AppLogger.i(TAG, "DATA " + new Gson().toJson(questions) );


                                        }));
                            }
                        })
                        .toList()
                ).subscribe(newForms -> getView().openFarmerDetailsActivity(newForms), throwable -> {
                    getView().showMessage(R.string.error_has_occurred);
                    AppLogger.e(TAG, throwable);
                }));*/


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


       ArrayList<MySearchItem> farmerNames = new ArrayList<>();

        runSingleCall(getAppDataManager().getDatabaseManager().villageAndFarmersDao().getVillagesAndFarmers()
                .filter(villageAndFarmers -> villageAndFarmers != null && villageAndFarmers.size() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(villageAndFarmers -> {

                     if(villageAndFarmers.size() > 0) {
                         AppLogger.i(TAG, "VILLAGES & FARMERS SIZE IS " + villageAndFarmers.size());


                         getView().setFragmentAdapter(villageAndFarmers);


                         runSingleCall(Observable.fromIterable(villageAndFarmers)
                                 .subscribeOn(Schedulers.io())
                                 .observeOn(AndroidSchedulers.mainThread())
                                 .map(villageAndFarmers1 -> {

                                     if(villageAndFarmers1.getFarmerList().size() > 0)
                                         Observable.fromIterable(villageAndFarmers1.getFarmerList())
                                                 .map(farmer -> new MySearchItem(farmer.getCode(), farmer.getFarmerName()))
                                                 .toList()
                                         .subscribe(new SingleObserver<List<MySearchItem>>() {
                                             @Override
                                             public void onSubscribe(Disposable d) {

                                                 if(d.isDisposed())
                                                 getAppDataManager().getCompositeDisposable().add(d);

                                             }

                                             @Override
                                             public void onSuccess(List<MySearchItem> mySearchItems) {
                                                 farmerNames.addAll(mySearchItems);
                                             }

                                             @Override
                                             public void onError(Throwable e) {

                                             }
                                         });
                                     return farmerNames;
                                 }).subscribe(items -> getView().instantiateSearchDialog(farmerNames)));

                     }else{

                         AppLogger.i(TAG, "VILLAGES & FARMERS SIZE IS EMPTY " + villageAndFarmers.size());

                     }}));

    }


    @Override
    public void syncData(boolean showProgress) {

        if(showProgress)
            getView().showLoading("Syncing data","Please wait...", true, 0,false);

        DownloadResources.newInstance(getView(), mAppDataManager, this, showProgress).getData();

    }


    @Override
    public void onSuccess() {
        AppLogger.i(TAG, "**** ON SUCCESS");
        getView().hideLoading();
        getView().showMessage("Data download complete!");

    }

    @Override
    public void onError(Throwable throwable) {
        AppLogger.i(TAG, "**** ON ERROR");

        getView().hideLoading();
        getView().showMessage(throwable.getMessage());

        throwable.printStackTrace();

        if(throwable.getMessage().contains("401")) {
            getView().openLoginActivityOnTokenExpire();
        }


    }



 }
