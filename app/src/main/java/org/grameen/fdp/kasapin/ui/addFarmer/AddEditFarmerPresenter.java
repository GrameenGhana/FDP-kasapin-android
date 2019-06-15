package org.grameen.fdp.kasapin.ui.addFarmer;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class AddEditFarmerPresenter extends BasePresenter<AddEditFarmerContract.View> implements AddEditFarmerContract.Presenter {


    @Inject
    public AddEditFarmerPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void openNextActivity() {

    }


    @Override
    public void loadFormFragment(String farmerCode, int formId) {

        AppLogger.e(TAG, "Farmer code is " + farmerCode + " and Form translation id is " + formId);


        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerDataSingle(farmerCode, formId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAnswerData ->
                                getView().showFormFragment(formAnswerData)

                        , throwable -> {
                            //throwable.printStackTrace();
                            getView().showFormFragment(null);
                        }
                ));
    }


    @Override
    public void saveData(RealFarmer farmer, FormAnswerData answerData, boolean exit) {

        answerData.setFarmerCode(farmer.getCode());
        getAppDataManager().getCompositeDisposable().add(Single.fromCallable(() -> getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(farmer))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    AppLogger.i(TAG, "Saving : " + getGson().toJson(answerData));

                    //if(answerData.getId() == 0) {

                    runSingleCall(Single.fromCallable(() -> getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(answerData))
                            .subscribeOn(Schedulers.io())
                            .subscribe(aLong -> {

                                getView().showMessage("Farmer data saved!");

                                if (!exit)
                                    getView().moveToNextForm();
                                else {
                                    getAppDataManager().setBooleanValue("reload", true);

                                    getView().finishActivity();
                                }
                            }, throwable -> {
                                getView().showMessage("An error occurred saving farmer data. Please try again.");
                                throwable.printStackTrace();
                            }));
/*

                            }else{


                                runSingleCall(Single.fromCallable(() -> getAppDataManager().getDatabaseManager().formAnswerDao().updateOne(answerData))
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(aLong -> {

                                            getView().showMessage("Farmer data updated!");

                                            if (!exit)
                                                getView().moveToNextForm();
                                            else {
                                                getAppDataManager().setBooleanValue("reload", true);

                                                getView().finishActivity();
                                            }
                                        }, throwable -> {
                                            getView().showMessage("An error occurred saving farmer data. Please try again.");
                                            throwable.printStackTrace();
                                        }));




                            }
*/


                }, throwable -> {
                    AppLogger.e(TAG, throwable.getMessage());
                    throwable.printStackTrace();
                }));

    }


}
