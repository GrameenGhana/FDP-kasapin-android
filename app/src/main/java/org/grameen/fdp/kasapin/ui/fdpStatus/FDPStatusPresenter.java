package org.grameen.fdp.kasapin.ui.fdpStatus;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FDPStatusPresenter extends BasePresenter<FDPStatusContract.View> implements FDPStatusContract.Presenter {

    @Inject
    FDPStatusPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void getAnswerData(String farmerCode, int formTranslationId) {
        AppLogger.e(TAG, "Farmer code is " + farmerCode + " and Form translation id is " + formTranslationId);
        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerDataSingle(farmerCode, formTranslationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAnswerData ->
                                getView().showFormFragment(formAnswerData)
                        , throwable ->
                            getView().showFormFragment(null)
                ));

    }

    @Override
    public void saveData(RealFarmer farmer, FormAnswerData formAnswerData) {
        getAppDataManager().getCompositeDisposable().add(Single.fromCallable(()
                -> getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(formAnswerData))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    setFarmerAsUnsynced(farmer);
                    getAppDataManager().setBooleanValue("reload", true);
                    getView().dismiss();
                }, throwable -> getView().showMessage("Could not save Labour and labour type data")));
    }
}
