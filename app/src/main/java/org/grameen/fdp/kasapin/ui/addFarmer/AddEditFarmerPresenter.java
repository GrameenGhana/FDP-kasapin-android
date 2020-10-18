package org.grameen.fdp.kasapin.ui.addFarmer;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

public class AddEditFarmerPresenter extends BasePresenter<AddEditFarmerContract.View> implements AddEditFarmerContract.Presenter {
    @Inject
    public AddEditFarmerPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void loadFormFragment(String farmerCode, int formTranslationId) {
//        AppLogger.e(TAG, "Farmer code is " + farmerCode + " and Form translation id is " + formTranslationId);
//        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerDataSingle(farmerCode, formTranslationId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(formAnswerData ->
//                                getView().showFormFragment()
//                        , throwable -> getView().showFormFragment()
//                ));
    }

    @Override
    public void getFarmerData(String code) {
        runSingleCall(getAppDataManager().getDatabaseManager().realFarmersDao().get(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(farmer -> {
                            if (getView() != null)
                                getView().setUpViews(farmer);
                        },
                        throwable -> getView().showMessage(R.string.error_getting_farmer_info)));
    }

    @Override
    public void saveData(Farmer farmer, FormAnswerData answerData, boolean exit, boolean wasProfileImageEdited) {
        answerData.setFarmerCode(farmer.getCode());

        getAppDataManager().getCompositeDisposable().add(Single.fromCallable(() -> getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(farmer))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    AppLogger.i(TAG, "Saving : " + getGson().toJson(answerData));
                    runSingleCall(Single.fromCallable(() -> getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(answerData))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> {
                                setFarmerAsUnSynced(farmer);
                                getView().showMessage("Farmer data saved!");

                                getAppDataManager().setBooleanValue("reload", true);

                                if (!exit)
                                    getView().moveToNextForm();
                                else
                                    getView().finishActivity();
                            }, throwable -> {
                                getView().showMessage("An error occurred saving farmer data. Please try again.");
                                throwable.printStackTrace();
                            }));
                }, throwable -> {
                    AppLogger.e(TAG, throwable.getMessage());
                    throwable.printStackTrace();
                }));
    }
}
