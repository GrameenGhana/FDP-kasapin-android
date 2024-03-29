package org.grameen.fdp.kasapin.ui.plotDetails;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PlotDetailsPresenter extends BasePresenter<PlotDetailsContract.View> implements PlotDetailsContract.Presenter {
    AppDataManager mAppDataManager;

    @Inject
    public PlotDetailsPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void getPlotQuestions() {
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
        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerDataOrNull(farmerCode, areaQuestion.getFormTranslationId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(answerData -> {
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
    public void saveData(Plot plot) {
        runSingleCall(Single.fromCallable(() -> getAppDataManager().getDatabaseManager().plotsDao().insertOne(plot))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong > 0) {
                        getAppDataManager().setBooleanValue("reload", true);
                        getAppDataManager().setBooleanValue("reloadPlotsData", true);
                        getAppDataManager().setBooleanValue("reloadRecommendation", false);
                        getView().showRecommendation();
                        getView().showMessage("Plot data updated!");
                    } else
                        getView().showMessage("Error occurred updating Plot data!");

                }, throwable -> {
                    getView().showMessage("An error occurred loading recommendation. Please try again.");
                    throwable.printStackTrace();
                }));
    }
}
