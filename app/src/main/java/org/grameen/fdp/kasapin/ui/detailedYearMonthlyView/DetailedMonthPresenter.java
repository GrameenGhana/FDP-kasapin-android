package org.grameen.fdp.kasapin.ui.detailedYearMonthlyView;


import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class DetailedMonthPresenter extends BasePresenter<DetailedMonthContract.View> implements DetailedMonthContract.Presenter{

    private AppDataManager mAppDataManager;




    @Inject
    public DetailedMonthPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;

    }


    @Override
    public void getPlotsData(String farmerCode) {


        runSingleCall(getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(farmerCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(plots -> getView().setPlotsData(plots), throwable -> getView().showMessage(R.string.error_has_occurred_loading_data)));

    }
}
