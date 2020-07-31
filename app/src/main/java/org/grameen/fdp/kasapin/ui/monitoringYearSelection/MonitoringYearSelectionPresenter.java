package org.grameen.fdp.kasapin.ui.monitoringYearSelection;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MonitoringYearSelectionPresenter extends BasePresenter<MonitoringYearSelectionContract.View> implements MonitoringYearSelectionContract.Presenter {
    AppDataManager mAppDataManager;

    @Inject
    public MonitoringYearSelectionPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void getFarmerAndPlotData(String farmerCode, String plotExternalId) {
       runSingleCall(getAppDataManager().getDatabaseManager().realFarmersDao().getOne(farmerCode)
               .subscribeOn(Schedulers.io())
               .subscribe(farmer-> getAppDataManager().getDatabaseManager().plotsDao().getPlot(farmerCode, plotExternalId)
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(new DisposableSingleObserver<Plot>() {
                           @Override
                           public void onSuccess(Plot plot) {
                               getView().setFarmerAndPlotData(farmer, plot);
                           }
                           @Override
                           public void onError(Throwable e) {
                               getView().showError();
                           }
                       }), throwable -> getView().showError()));

    }
}
