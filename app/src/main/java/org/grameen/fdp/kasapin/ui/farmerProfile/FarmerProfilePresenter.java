package org.grameen.fdp.kasapin.ui.farmerProfile;


import android.view.ContextThemeWrapper;
import android.widget.Button;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FarmerProfilePresenter extends BasePresenter<FarmerProfileContract.View> implements FarmerProfileContract.Presenter {

    AppDataManager mAppDataManager;
    int count = 0;


    @Inject
    public FarmerProfilePresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void openNextActivity() {

    }


    @Override
    public void deletePlot(Plot plot) {

        getAppDataManager().getDatabaseManager().plotsDao().deleteOne(plot);
        getView().showMessage("Data deleted!");


    }

    @Override
    public void getFarmersPlots(String farmerCode) {

        AppLogger.i(TAG, "GETTING PLOTS DATA!. >>> FARMER CODE IS " + farmerCode);
        runSingleCall(getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(farmerCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(plots -> getView().setUpFarmersPlotsAdapter(plots), throwable -> {
                    getView().showMessage("Could not obtain plots data.");
                }));
    }


    public void loadDynamicButtons(List<FormAndQuestions> formAndQuestions) {

        count = 0;

        runSingleCall(Observable.fromIterable(formAndQuestions)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .filter(formAndQuestions1 -> !formAndQuestions1.getForm().shouldHide())
                .map(formAndQuestions1 -> {

                    //AppLogger.i(TAG, "LOADING DYNAMIC BUTTONS .....");


                    final Button btn;
                    if (getAppDataManager().isMonitoring())
                        btn = new Button(new ContextThemeWrapper(getContext(), R.style.PrimaryButton_Monitoring));
                    else
                        btn = new Button(new ContextThemeWrapper(getContext(), R.style.PrimaryButton));


                    btn.setTag(count);
                    btn.setText(formAndQuestions1.getForm().getTranslation());
                    btn.setContentDescription(formAndQuestions1.getForm().getTranslation());

                    // AppLogger.i(TAG, "BUTTON " + count);


                    count++;

                    return btn;

                }).toList().subscribe(buttons -> getView().addButtons(buttons)
                ));

    }


}
