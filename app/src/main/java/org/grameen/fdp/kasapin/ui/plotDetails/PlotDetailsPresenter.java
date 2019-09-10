package org.grameen.fdp.kasapin.ui.plotDetails;


import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

public class PlotDetailsPresenter extends BasePresenter<PlotDetailsContract.View> implements PlotDetailsContract.Presenter {

    AppDataManager mAppDataManager;


    @Inject
    public PlotDetailsPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void getPlotQuestions() {

       /* List<FormAndQuestions> formAndQuestionsList = new ArrayList<>();

        AppLogger.e(TAG, "All Forms size is " + BaseActivity.FORM_AND_QUESTIONS.size());

        if (BaseActivity.FORM_AND_QUESTIONS != null)
            for (FormAndQuestions formAndQuestions : BaseActivity.FORM_AND_QUESTIONS) {
                if (formAndQuestions.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_PLOT_FORM))
                    formAndQuestionsList.add(formAndQuestions);
            }

        AppLogger.e(TAG, "Plot Forms size is " + formAndQuestionsList.size());
 */

        runSingleCall(getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByDisplayType(AppConstants.DISPLAY_TYPE_PLOT_FORM)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAndQuestions -> getView().showForm(formAndQuestions), throwable -> {
                    getView().showMessage(R.string.error_has_occurred);
                    throwable.printStackTrace();
                }));

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
    public void getRecommendations(int cropId) {

        runSingleCall(getAppDataManager().getDatabaseManager().recommendationsDao().getRecommendationsByCrop(cropId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendations -> {

                            if (recommendations != null)
                                getView().loadRecommendation(recommendations);
                        },
                        throwable -> {
                            getView().showMessage(R.string.error_has_occurred_loading_data);
                            throwable.printStackTrace();
                        }));

    }

    @Override
    public void openNextActivity() {

    }


    @Override
    public void saveData(Plot plot) {
        runSingleCall(Single.fromCallable(() -> getAppDataManager().getDatabaseManager().plotsDao().insertOne(plot))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {

                    getAppDataManager().setBooleanValue("reload", true);
                    getView().showRecommendation();

                }, throwable -> {
                    getView().showMessage("An error occurred loading recommendation. Please try again.");
                    throwable.printStackTrace();
                }));

    }


}
