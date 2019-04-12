package org.grameen.fdp.kasapin.ui.pandl;


import android.text.TextUtils;

import com.balsikandar.crashreporter.CrashReporter;
import com.balsikandar.crashreporter.utils.CrashUtil;
import com.balsikandar.crashreporter.utils.FileUtils;
import com.crashlytics.android.Crashlytics;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class ProfitAndLossPresenter extends BasePresenter<ProfitAndLossContract.View> implements ProfitAndLossContract.Presenter{

    private JSONObject ALL_DATA_JSON = new JSONObject();




    @Inject
    public ProfitAndLossPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;




    }


    @Override
    public void getAllAnswers(String farmerCode) {

        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getAll(farmerCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAnswerDatas -> {

                    if(formAnswerDatas != null) {
                        for (FormAnswerData formAnswerData : formAnswerDatas) {

                            JSONObject object = formAnswerData.getJsonData();
                            Iterator iterator = object.keys();

                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                try {
                                    if (ALL_DATA_JSON.has(key))
                                        ALL_DATA_JSON.remove(key);

                                    ALL_DATA_JSON.put(key, formAnswerData.getJsonData().get(key));

                                } catch (JSONException ignored) {
                                    ignored.printStackTrace();
                                }
                            }
                        }



                        getView().setAnswerData(ALL_DATA_JSON);
                    }


                }, throwable ->  getView().showMessage("Couldn't obtain data!")));




    }


    @Override
    public void updatePlotData(Plot plot, boolean reloadTable) {


        Completable.fromAction(()->
                getAppDataManager().getDatabaseManager().plotsDao().insertOne(plot))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                        getView().reloadTableData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().showMessage(e.getMessage());

                    }
                });




    }
}
