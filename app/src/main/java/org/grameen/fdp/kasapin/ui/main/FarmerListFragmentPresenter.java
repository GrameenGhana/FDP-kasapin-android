package org.grameen.fdp.kasapin.ui.main;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
    public void showDeleteFarmerDialog(Farmer farmer, int position) {
        getView().showDeleteFarmerDialog(farmer, position);
    }

    @Override
    public void deleteFarmer(Farmer farmer, int position) {}
}
