package org.grameen.fdp.kasapin.ui.farmerProfile;


import android.view.ContextThemeWrapper;
import android.widget.Button;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Mapping;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Submission;
import org.grameen.fdp.kasapin.syncManager.UploadData;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.ui.main.MainPresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.FILTERED_FORMS;
import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FarmerProfilePresenter extends BasePresenter<FarmerProfileContract.View> implements FarmerProfileContract.Presenter, FdpCallbacks.UploadDataListener {
    private AppDataManager mAppDataManager;
    private int count;

    @Inject
    FarmerProfilePresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
        count = 0;
    }


    @Override
    public void openNextActivity() {
    }


    @Override
    public void deletePlot(Plot plot) {
        getAppDataManager().getDatabaseManager().plotsDao().deleteOne(plot.getExternalId());
        getView().showMessage("Data deleted!");
    }

    @Override
    public void getFarmersPlots(String farmerCode) {
        runSingleCall(getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(farmerCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(plots -> getView().setUpFarmersPlotsAdapter(plots), throwable -> getView().showMessage("Could not obtain plots data.")));
    }


    public void loadDynamicButtons(List<FormAndQuestions> formAndQuestions) {
        count = 0;

        if (getAppDataManager().isMonitoring()){
            runSingleCall(Observable.fromIterable(formAndQuestions)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .filter(formAndQuestions1 -> formAndQuestions1.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_FORM)
                    ||  formAndQuestions1.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_TABLE)
                    || formAndQuestions1.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_HISTORICAL))
                    .filter(formAndQuestions1 -> (!formAndQuestions1.getForm().shouldHide()))
                    .map(formAndQuestions1 -> {
                        FILTERED_FORMS.add(formAndQuestions1);
                        final Button btn = new Button(new ContextThemeWrapper(getContext(), R.style.PrimaryButton_Monitoring));
                        btn.setTag(count);
                        btn.setText(formAndQuestions1.getForm().getTranslation());
                        btn.setContentDescription(formAndQuestions1.getForm().getTranslation());

                        //Temporary save the position of the family members Form and Questions in the array for later.
                        if(formAndQuestions1.getForm().getFormNameC().equalsIgnoreCase(AppConstants.FAMILY_MEMBERS))
                            FarmerProfileActivity.familyMembersFormPosition = count;

                        count += 1;
                        return btn;

                    }).toList().subscribe(buttons -> getView().addButtons(buttons)
                    ));
        }else {

            runSingleCall(Observable.fromIterable(formAndQuestions)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .filter(formAndQuestions1 -> formAndQuestions1.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_FORM)
                            ||  formAndQuestions1.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_TABLE) || formAndQuestions1.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_HISTORICAL))
                    .filter(formAndQuestions1 -> (!formAndQuestions1.getForm().shouldHide() &&
                                    (formAndQuestions1.getForm().getTypeC().equalsIgnoreCase(AppConstants.DIAGNOSTIC) || formAndQuestions1.getForm().getTypeC().equalsIgnoreCase(AppConstants.DIAGNOSTIC_MONITORING))))
                    .map(formAndQuestions1 -> {

                        FILTERED_FORMS.add(formAndQuestions1);

                        final Button btn = new Button(new ContextThemeWrapper(getContext(), R.style.PrimaryButton));
                        btn.setTag(count);
                        btn.setText(formAndQuestions1.getForm().getTranslation());
                        btn.setContentDescription(formAndQuestions1.getForm().getTranslation());

                        //Temporary save the position of the family members Form and Questions in the array for later.
                        if(formAndQuestions1.getForm().getFormNameC().equalsIgnoreCase(AppConstants.FAMILY_MEMBERS))
                            FarmerProfileActivity.familyMembersFormPosition = count;
                        count += 1;
                        return btn;

                    }).toList().subscribe(buttons -> getView().addButtons(buttons), throwable -> {
                        AppLogger.e(TAG, throwable);
                    }));
        }
    }

    @Override
    public void syncFarmerData(RealFarmer farmer, boolean showProgress) {
        syncData(this, showProgress, Collections.singletonList(farmer));
    }


    @Override
    public void onUploadComplete(String message) {
        AppLogger.i(TAG, "**** ON SUCCESS");
        getView().hideLoading();
        getView().showMessage(message);
        getView().updateFarmerSyncStatus();
    }

    @Override
    public void onUploadError(Throwable throwable) {
        AppLogger.i(TAG, "**** ON ERROR");
        getView().hideLoading();
        getView().showMessage(throwable.getMessage());
        throwable.printStackTrace();
    }
}
