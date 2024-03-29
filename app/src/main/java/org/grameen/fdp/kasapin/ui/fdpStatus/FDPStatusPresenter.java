package org.grameen.fdp.kasapin.ui.fdpStatus;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

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
//        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerDataSingle(farmerCode, formTranslationId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(formAnswerData ->
//                                getView().showFormFragment(formAnswerData)
//                        , throwable ->
//                                getView().showFormFragment(null)
//                ));

    }

    @Override
    public void saveData(Farmer farmer, FormAnswerData formAnswerData) {
        getAppDataManager().getCompositeDisposable().add(Single.fromCallable(()
                -> getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(formAnswerData))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    setFarmerAsUnSynced(farmer);
                    getAppDataManager().setBooleanValue("reload", true);
                    getView().dismiss();
                }, throwable -> getView().showMessage("Could not save Labour and labour type data")));
    }
}
