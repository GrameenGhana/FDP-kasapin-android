package org.grameen.fdp.kasapin.ui.main;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.CommunitiesAndFarmers;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.syncManager.DownloadResources;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.ui.base.model.MySearchItem;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter,
        FdpCallbacks.OnDownloadResourcesListener, FdpCallbacks.UploadDataListener {
    AppDataManager mAppDataManager;
    int count = 0;

    @Inject
    public MainPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void getFarmerProfileFormAndQuestions() {
        runSingleCall(getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByName(AppConstants.FARMER_PROFILE)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(formAndQuestion -> getView().openAddNewFarmerActivity(formAndQuestion), throwable -> {
                    getView().showMessage(R.string.error_has_occurred);
                    throwable.printStackTrace();
                }));
    }

    @Override
    public void getFormsAndQuestionsData() {
        runSingleCall(getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByDisplayTypeOnly(
                new String[]{AppConstants.DISPLAY_TYPE_FORM.toLowerCase(), AppConstants.DISPLAY_TYPE_TABLE.toLowerCase(), AppConstants.DISPLAY_TYPE_HISTORICAL.toLowerCase()})
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAndQuestions -> getView().cacheFormsAndQuestionsData(formAndQuestions), throwable -> {
                    getView().onError(throwable.getLocalizedMessage());
                    throwable.printStackTrace();
                }));
    }

    @Override
    public void openSearchDialog() {
        //Todo get list of farmers and ids, populate into search dialog
        getView().showSearchDialog(null);
    }

    @Override
    public void getVillagesDataFromDatabase() {
        runSingleCall(getAppDataManager().getDatabaseManager().villageAndFarmersDao().getVillagesAndFarmers()
                .filter(villageAndFarmers -> villageAndFarmers != null && villageAndFarmers.size() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(villageAndFarmers -> {
                    if (villageAndFarmers.size() > 0) {
                        getView().setFragmentAdapter(villageAndFarmers);
                    }
                }, throwable -> getView().onError(throwable.getLocalizedMessage())));
    }

    @Override
    public void downloadResourcesData(boolean showProgress) {
        if (showProgress)
            getView().showLoading("Downloading data", "Please wait...", true, 0, false);
        DownloadResources.newInstance(getView(), mAppDataManager, this, showProgress).getCommunitiesData();
    }

    @Override
    public void downloadFarmersData(boolean showProgress) {
        if (showProgress)
            getView().showLoading("Downloading data", "Please wait...", true, 0, false);
        DownloadResources.newInstance(getView(), mAppDataManager, this, showProgress).getFarmersData();
    }

    @Override
    public void syncData(boolean showProgress) {
        /* Todo Sync all un synced data
         Callbacks can be declared at the global or local level
         In this context, declaring the callback at the global level is okay since we'll be syncing all farmers data
         when user clicks on Upload farmer data button in the MainActivity, the json object containing the json array of farmers plus some extra fields is
         bundled and passed to the method uploadFarmersData(@param JSONObject object) in the UploadDataManager class
         A scenario where we might declare a callback listener at the local level is in the FarmerProfileActivity where
         we're only syncing one farmer
         */

        /*
         * Generate the Json object here.
         * You can have access to the database via the @param AppDataManager.getAppDatabase().farmersDao().getAll()
         * Same for plots, answers, etc.
         *
         * First check if there are un synced data
         **/
        if (getAppDataManager().getDatabaseManager().realFarmersDao().checkIfUnsyncedFarmersAvailable().blockingGet() <= 0) {
            getView().showMessage(R.string.no_new_data);
            return;
        }
        List<Farmer> UN_SYNCED_FARMERS = getAppDataManager().getDatabaseManager().realFarmersDao().getAllNotSynced().blockingGet();
        syncData(this, showProgress, UN_SYNCED_FARMERS);
    }

    @Override
    public void initializeSearchDialog(List<CommunitiesAndFarmers> villageAndFarmers) {
        ArrayList<MySearchItem> farmerNames = new ArrayList<>();
        runSingleCall(Observable.fromIterable(villageAndFarmers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(villageAndFarmers1 -> {
                    count += 1;
                    if (villageAndFarmers1.getFarmerList().size() > 0)
                        Observable.fromIterable(villageAndFarmers1.getFarmerList())
                                .map(farmer -> new MySearchItem(farmer.getCode(), farmer.getFarmerName()))
                                .toList()
                                .subscribe(new DisposableSingleObserver<List<MySearchItem>>() {
                                    @Override
                                    public void onSuccess(List<MySearchItem> mySearchItems) {
                                        farmerNames.addAll(mySearchItems);
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                    }
                                });
                    return farmerNames;
                }).subscribe(items -> {
                    if (count == villageAndFarmers.size() - 1)
                        getView().instantiateSearchDialog(farmerNames);
                }));
    }

    @Override
    public void getFarmer(String farmerCode) {
        runSingleCall(getAppDataManager().getDatabaseManager().realFarmersDao().get(farmerCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(farmer -> {
                    if (farmer != null)
                        getView().viewFarmerProfile(farmer);
                    else
                        getView().showMessage("Could not load farmer data");
                }, throwable -> {
                    throwable.printStackTrace();
                    AppLogger.e(TAG, throwable.getMessage());
                }));
    }

    //Download Data Callbacks declared at the global level
    @Override
    public void onSuccess(String message) {
        //On download data success.
        getView().hideLoading();
        getView().showMessage(message);
        getView().restartUI();
    }

    //Download Data Callbacks declared at the global level
    @Override
    public void onError(Throwable throwable) {
        //On download data error
        getView().hideLoading();
        getView().showMessage(throwable.getMessage());
        throwable.printStackTrace();

        if (throwable.getMessage().contains("401")) {
            getView().openLoginActivityOnTokenExpire();
        }
    }

    //Upload Data Callbacks declared at the global level
    @Override
    public void onUploadComplete(String message) {
        getView().hideLoading();
        getView().showMessage(message);
        getView().restartUI();
    }

    //Upload Data Callbacks declared at the global level
    @Override
    public void onUploadError(Throwable throwable) {
        getView().hideLoading();
        getView().showMessage(throwable.getMessage());
        throwable.printStackTrace();
    }
}