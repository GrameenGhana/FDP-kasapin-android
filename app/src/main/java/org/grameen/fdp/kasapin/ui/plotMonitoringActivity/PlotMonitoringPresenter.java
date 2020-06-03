package org.grameen.fdp.kasapin.ui.plotMonitoringActivity;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class PlotMonitoringPresenter extends BasePresenter<PlotMonitoringContract.View> implements PlotMonitoringContract.Presenter, FdpCallbacks.UploadDataListener  {

    AppDataManager mAppDataManager;

    FormAndQuestions aoFormAndQuestions = null;
    FormAndQuestions aoMonitoringFormAndQuestions = null;


    @Inject
    public PlotMonitoringPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void getAOQuestions() {

        Completable.fromAction(() -> {
            aoFormAndQuestions = getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByName(AppConstants.ADOPTION_OBSERVATIONS).blockingGet();
            aoMonitoringFormAndQuestions = getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByName(AppConstants.AO_MONITORING).blockingGet();
        }) .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        getView().setupAdoptionObservationsTableView(aoFormAndQuestions, aoMonitoringFormAndQuestions);
                    }
                    @Override
                    public void onError(Throwable e) {
                     getView().showMessage("Couldn't obtain Adoption Observation questions");
                        getView().showMessage(e.getMessage());
                    }
                });

    }


    @Override
    public void getMonitoringForSelectedYear(Plot plot, int selectedYear) {

        AppLogger.e(TAG, "###########  Getting monitoring for year " + selectedYear + " with external id " + plot.getExternalId());

        getAppDataManager().getDatabaseManager().monitoringsDao().getAllMonitoringForSelectedYear(plot.getExternalId(), selectedYear)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Monitoring>>() {
                    @Override
                    public void onSuccess(List<Monitoring> monitorings) {
                        AppLogger.e(TAG, "########### Monitoring size is " +monitorings.size());

                        getView().updateTableData(monitorings);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showMessage(e.getMessage());
                        e.printStackTrace();
                    }
                });


    }

    @Override
    public void syncFarmerData(RealFarmer farmer, boolean showProgress) {
        syncData(this, showProgress, Collections.singletonList(farmer));
    }




    //Upload Data Callbacks declared at the global level
    @Override
    public void onUploadComplete(String message) {
        AppLogger.i(TAG, "**** ON SUCCESS");
        getView().hideLoading();
        getView().showMessage(message);


    }

    //Upload Data Callbacks declared at the global level
    @Override
    public void onUploadError(Throwable throwable) {
        AppLogger.i(TAG, "**** ON ERROR");

        getView().hideLoading();
        getView().showMessage(throwable.getMessage());
        throwable.printStackTrace();

    }
}
