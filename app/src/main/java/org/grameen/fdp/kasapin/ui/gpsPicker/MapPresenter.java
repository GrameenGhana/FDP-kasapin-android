package org.grameen.fdp.kasapin.ui.gpsPicker;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapPresenter extends BasePresenter<MapContract.View> implements MapContract.Presenter {
    @Inject
    public MapPresenter(AppDataManager appDataManager) {
        super(appDataManager);
    }


    @Override
    public void getPlotData(String externalId) {
        runSingleCall(getAppDataManager().getDatabaseManager().plotsDao().getPlot(externalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((plot, throwable) -> getView().setPlotData(plot)
        ));
    }

    @Override
    public void updatePlotData(Plot plot) {
        //insertOne returns autogen id of plot which is > 0 if the transaction was successful and 0 if unsuccessful
        runSingleCall(Single.just(getAppDataManager().getDatabaseManager().plotsDao().insertOne(plot))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> getView().onPlotUpdateComplete(value > 0)
        ));
    }
}
