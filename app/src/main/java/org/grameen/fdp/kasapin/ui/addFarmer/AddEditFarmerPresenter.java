package org.grameen.fdp.kasapin.ui.addFarmer;


import android.support.annotation.NonNull;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicFormFragment;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.TimeUtils;
import org.json.JSONObject;

import java.sql.Date;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class AddEditFarmerPresenter extends BasePresenter<AddEditFarmerContract.View> implements AddEditFarmerContract.Presenter{

    private AppDataManager mAppDataManager;

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


        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(farmerCode, formId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAnswerData -> getView().showFormFragment(formAnswerData)
                        , throwable -> {
                            AppLogger.e(TAG, throwable.getMessage());
                            getView().showFormFragment(null);
                        }
                ));

    }



    @Override
    public void saveData(RealFarmer farmer, FormAnswerData answerData, boolean exit) {

        answerData.setFarmerCode(farmer.getCode());


        getAppDataManager()
                .getCompositeDisposable()
                .add(Single.fromCallable(() -> getAppDataManager().getDatabaseManager().realFarmersDao().insertOne(farmer))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(id -> {

                            AppLogger.i(TAG, "Saving : " + getGson().toJson(answerData));

                            runSingleCall(Single.fromCallable(() ->  getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(answerData))
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(aLong -> {

                                        getView().showMessage("Farmer data saved!");

                                        if(!exit)
                                            getView().moveToNextForm();
                                        else
                                            getView().finishActivity();

                                    }, throwable -> {
                                        getView().showMessage("An error occurred saving farmer data. Please try again.");
                                        throwable.printStackTrace();
                                    }));
                        }, throwable ->{ AppLogger.e(TAG, throwable.getMessage());
                            throwable.printStackTrace();}));


    }


}
