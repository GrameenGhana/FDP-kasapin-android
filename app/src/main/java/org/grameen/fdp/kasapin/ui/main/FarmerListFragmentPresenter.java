package org.grameen.fdp.kasapin.ui.main;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FarmerListFragmentPresenter extends BasePresenter<MainContract.FragmentView> implements MainContract.FragmentPresenter {

    AppDataManager mAppDataManager;


    @Inject
    public FarmerListFragmentPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;

    }

    @Override
    public void getFarmerData() {

        getAppDataManager().getCompositeDisposable().add(getAppDataManager().getDatabaseManager().realFarmersDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realFarmers -> getView().setListAdapter(realFarmers), throwable -> getView().showMessage("An error occurred obtaining farmer data!")));

    }

    @Override
    public void showDeleteFarmerDialog(RealFarmer farmer, int position) {
        getView().showDeleteFarmerDialog(farmer, position);

    }

    @Override
    public void deleteFarmer(RealFarmer farmer, int position) {
        /*runSingleCall(getAppDataManager().getDatabaseManager().realFarmersDao().deleteFarmerById(farmer.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> {
                            if (i > 0)
                                getView().showFarmerDeletedMessage(farmer.getFarmerName(), position);
                            else
                                getView().showMessage(R.string.could_not_delete_data);
                        }
                )); */

    }
}
