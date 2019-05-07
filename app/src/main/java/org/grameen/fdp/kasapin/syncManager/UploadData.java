package org.grameen.fdp.kasapin.syncManager;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.network.model.Response;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class UploadData {

    String TAG = "Upload data";
    private FdpCallbacks.UploadDataListener uploadDataListener;
    private BaseContract.View mView;
    private AppDataManager mAppDataManager;
    private boolean showProgress;
    private String token;


    private UploadData(BaseContract.View view, AppDataManager appDataManager, FdpCallbacks.UploadDataListener listener, boolean showProgress) {
        this.mAppDataManager = appDataManager;
        this.mView = view;
        this.uploadDataListener = listener;
        this.showProgress = showProgress;
        token = mAppDataManager.getAccessToken();
    }

    public static UploadData newInstance(BaseContract.View view, AppDataManager appDataManager, FdpCallbacks.UploadDataListener listener, boolean showProgress) {
        return new UploadData(view, appDataManager, listener, showProgress);
    }

    public AppDataManager getAppDataManager() {
        return mAppDataManager;
    }

    public BaseContract.View getView() {
        return mView;
    }

    public void uploadFarmersData(JSONObject farmersJsonObject) {

        if (showProgress)
            getView().setLoadingMessage("Syncing farmers data...");

        getAppDataManager().getFdpApiService()
                .pushFarmersData(token, farmersJsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Response>() {
                    @Override
                    public void onSuccess(Response response) {

                        //Do other network calls or complete


                        if (uploadDataListener != null)
                            uploadDataListener.onUploadComplete("Data upload successful.");
                        uploadDataListener = null;


                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e);
                    }
                });
    }


    void showError(Throwable throwable) {
        if (uploadDataListener != null) {
            uploadDataListener.onUploadError(throwable);
            uploadDataListener = null;
        }
    }

}
