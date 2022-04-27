package org.grameen.fdp.kasapin.ui.AddEditFarmerPlot;


import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class AddEditFarmerPlotPresenter extends BasePresenter<AddEditFarmerPlotContract.View> implements AddEditFarmerPlotContract.Presenter {
    AppDataManager mAppDataManager;


    @Inject
    public AddEditFarmerPlotPresenter(AppDataManager appDataManager) {
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
    public void saveData(Plot plot, String flag) {
        runSingleCall(Single.fromCallable(() -> getAppDataManager().getDatabaseManager().plotsDao().insertOne(plot))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {

                    Farmer farmer = getAppDataManager().getDatabaseManager().realFarmersDao().get(plot.getFarmerCode()).blockingGet();
                    if (farmer != null)
                        setFarmerAsUnSynced(farmer);

                    getView().showMessage("Plot data saved!");
                    getAppDataManager().setBooleanValue("reloadRecommendation", true);

                    getView().hideLoading();

                    if (flag != null && flag.equalsIgnoreCase("gpsPicker"))
                        getView().moveToMapActivity(plot);
                    else
                        getView().showPlotDetailsActivity(plot);

                }, throwable -> {

                    getView().showMessage("An error occurred saving plot data. Please try again.");
                    throwable.printStackTrace();
                }));

    }
}
