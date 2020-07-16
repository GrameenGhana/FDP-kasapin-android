package org.grameen.fdp.kasapin.ui.pandl;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class ProfitAndLossPresenter extends BasePresenter<ProfitAndLossContract.View> implements ProfitAndLossContract.Presenter {
    @Inject
    ProfitAndLossPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void getFarmerData(String farmerCode) {
        getView().showLoading("Getting farmer data", "Please wait...", true, 0, false);
        getAppDataManager().getDatabaseManager().realFarmersDao().get(farmerCode)
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableMaybeObserver<Farmer>() {
            @Override
            public void onSuccess(Farmer farmer) {
                getView().setUpViews(farmer);
            }
            @Override
            public void onError(Throwable e){
                getView().showMessage("Could not fetch farmer data. Please report this issue.");
            }
            @Override
            public void onComplete() {
                getView().hideLoading();
            }
        });
    }

    @Override
    public void getAllAnswers(String farmerCode) {
        JSONObject ALL_DATA_JSON = new JSONObject();
        runSingleCall(getAppDataManager().getDatabaseManager().formAnswerDao().getAll(farmerCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAnswerDatas -> {
                    if (formAnswerDatas != null) {
                        for (FormAnswerData formAnswerData : formAnswerDatas) {
                            JSONObject object = formAnswerData.getJsonData();
                            Iterator<String> iterator = object.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                try {
                                    ALL_DATA_JSON.put(key, formAnswerData.getJsonData().get(key));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        getView().setAnswerData(ALL_DATA_JSON);
                    }
                }, throwable -> getView().showMessage("Couldn't obtain data!")));
    }

    @Override
    public void updatePlotData(Plot plot, boolean reloadTable) {
        Completable.fromAction(() ->
                getAppDataManager().getDatabaseManager().plotsDao().insertOne(plot))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        setFarmerAsUnsynced(mAppDataManager.getDatabaseManager().realFarmersDao().get(plot.getFarmerCode()).blockingGet());
                        getAppDataManager().setBooleanValue("reload", true);
                        getView().loadTableData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().showMessage(e.getMessage());
                    }
                });
    }

    @Override
    public void saveLabourValues(FormAnswerData formAnswerData, Farmer farmer) {

        FormAnswerData oldData = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(farmer.getCode(), formAnswerData.getFormId());
        if (oldData != null)
            formAnswerData.setId(oldData.getId());

        getAppDataManager().getCompositeDisposable().add(Single.fromCallable(()
                -> getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(formAnswerData))
                .subscribeOn(Schedulers.io())
                .subscribe(longValue -> {
                    setFarmerAsUnsynced(farmer);
                    getAppDataManager().setBooleanValue("reload", true);
                    getView().showMessage("Labour values updated!");
                }, throwable -> {
                    getView().showMessage("Could not save Labour and labour type data");
                }));

    }
}
