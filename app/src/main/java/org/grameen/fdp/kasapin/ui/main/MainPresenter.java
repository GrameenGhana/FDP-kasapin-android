package org.grameen.fdp.kasapin.ui.main;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.VillageAndFarmers;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter{

    AppDataManager mAppDataManager;



    @Inject
    public MainPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }




    @Override
    public void openNextActivity() {

    }

   /* @Override
    public void showDialog(Boolean cancelable, @Nullable String title, @Nullable String message,
                           @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                           @NonNull String positiveText,
                           @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                           @NonNull String negativeText, @Nullable int icon_drawable) {

        getView().showDialog(cancelable, title, message, onPositiveButtonClickListener, positiveText, onNegativeButtonClickListener,
                negativeText, icon_drawable);





    }*/

    @Override
    public void startDelay(long delayTime) {

    }

    @Override
    public void openSearchDialog() {
        //Todo get list of farmers and ids, populate into search dialog

        getView().showSearchDialog(null);

    }

    @Override
    public void toggleDrawer() {
        getView().toggleDrawer();
    }

    @Override
    public void getFarmersData() {

        getAppDataManager().getDatabaseManager().realFarmersDao().getAll()
                .observe(getContext(),
                        farmers -> {
                if(farmers != null && farmers.size() > 0)
                    getView().instantiateSearchDialog(farmers);

    });
        }


    @Override
    public void getVillagesData() {

        getAppDataManager().getDatabaseManager().villageAndFarmersDao().getVillagesAndFarmers()
                .observe(getContext(), villagesAndFarmers -> {

                    if(villagesAndFarmers != null && villagesAndFarmers.size() > 0)
                        getView().setFragmentAdapter(villagesAndFarmers);
                    else
                        getView().togglePlaceHolder(true);
                });


    }
}
