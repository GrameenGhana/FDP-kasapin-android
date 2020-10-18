package org.grameen.fdp.kasapin.ui.main;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public void getFarmerData(List<String> farmerCodes) {
        getView().showLoading();
        getAppDataManager().getCompositeDisposable().add(getAppDataManager().getDatabaseManager().realFarmersDao().getAll(farmerCodes)
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realFarmers -> {
                            if (realFarmers != null)
                                getView().setListAdapter(realFarmers);
                        },
                        throwable -> {
                        }));
    }

    @Override
    public void showDeleteFarmerDialog(Farmer farmer, int position) {
        getView().showDeleteFarmerDialog(farmer, position);
    }

    @Override
    public void deleteFarmer(Farmer farmer, int position) {
    }
}
