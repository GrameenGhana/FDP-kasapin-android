package org.grameen.fdp.kasapin.ui.plotDetails;


import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class PlotDetailsPresenter extends BasePresenter<PlotDetailsContract.View> implements PlotDetailsContract.Presenter{

    AppDataManager mAppDataManager;



    @Inject
    public PlotDetailsPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }



    @Override
    public void getPlotQuestions() {

        AppLogger.e(TAG, "All Forms size is " + BaseActivity.FORM_AND_QUESTIONS.size());

        runSingleCall(getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByDisplayType("Plot form")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAndQuestions -> getView().showForm(formAndQuestions), throwable -> {
                    getView().showMessage(R.string.error_has_occurred);
                    throwable.printStackTrace();
                }));

    }


    @Override
    public void getAreaUnits(String farmerCode) {
        runSingleCall(getAppDataManager().getDatabaseManager().questionDao().get("Farm_area_units")
                .subscribeOn(Schedulers.io())
                .subscribe(question -> {
                    if(question != null){
                        //Todo get answer data using form_id and farmerCode in question object and obtain value

                        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(farmerCode, question.getFormTranslationId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(answerData -> {

                            if(answerData != null){
                                JSONObject jsonObject = new JSONObject(answerData.getData());
                                if(jsonObject.has(question.getLabelC())){
                                    getView().setAreaUnits(jsonObject.getString(question.getLabelC()));
                                }
                            }

                        }, throwable -> {
                            throwable.printStackTrace();
                            getView().showMessage("Could not get area unit.");
                        }));

                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    AppLogger.e(TAG, throwable.getMessage());
                }));
    }

    @Override
    public void openNextActivity() {

    }


}
