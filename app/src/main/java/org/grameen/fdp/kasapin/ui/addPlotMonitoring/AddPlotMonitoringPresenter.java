package org.grameen.fdp.kasapin.ui.addPlotMonitoring;


import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class AddPlotMonitoringPresenter extends BasePresenter<AddPlotMonitoringContract.View> implements AddPlotMonitoringContract.Presenter {

    AppDataManager mAppDataManager;

    FormAndQuestions monitotingPlotInformationFormAndQuestions = null;
    FormAndQuestions aoMonitoringFormAndQuestions = null;

    @Inject
    public AddPlotMonitoringPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void getAreaUnits(String farmerCode) {

        Question areaQuestion = getAppDataManager().getDatabaseManager().questionDao().get("farm_area_units");
        Question estProdQuestion = getAppDataManager().getDatabaseManager().questionDao().get("farm_weight_units");

        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerDataSingle(farmerCode, areaQuestion.getFormTranslationId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(answerData -> {
                    AppLogger.e(TAG, "ANSWER DATA >> " + getGson().toJson(answerData));


                    if (answerData != null) {
                        JSONObject jsonObject = new JSONObject(answerData.getData());
                        if (jsonObject.has(areaQuestion.getLabelC())) {
                            getView().setAreaUnits(jsonObject.getString(areaQuestion.getLabelC()));
                        }

                        if (jsonObject.has(estProdQuestion.getLabelC())) {
                            getView().setProductionUnit(jsonObject.getString(estProdQuestion.getLabelC()));
                        }
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                    getView().showMessage("Could not get area unit.");
                }));


    }


    @Override
    public void getMonitoringQuestions() {
        Completable.fromAction(() -> {
            monitotingPlotInformationFormAndQuestions = getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByName(AppConstants.MONITORING_PLOT_INFORMATION).blockingGet();
            aoMonitoringFormAndQuestions = getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByName(AppConstants.AO_MONITORING).blockingGet();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        getView().loadDynamicFragmentAndViews(monitotingPlotInformationFormAndQuestions, aoMonitoringFormAndQuestions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showMessage("Couldn't obtain Adoption Observation questions");
                        getView().showMessage(e.getMessage());
                    }
                });
    }

    @Override
    public void saveMonitoringData(Monitoring monitoring, Farmer farmer) {

        getAppDataManager().getDatabaseManager().monitoringDao().insertMonitoring(monitoring);
        setFarmerAsUnsynced(farmer);

        getAppDataManager().setBooleanValue("reload", true);

        getView().showMessage(R.string.data_saved);

        getView().openNextActivity();


    }
}
