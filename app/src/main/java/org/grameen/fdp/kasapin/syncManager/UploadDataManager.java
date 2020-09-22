package org.grameen.fdp.kasapin.syncManager;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.network.model.ServerResponse;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class UploadDataManager {
    String TAG = "Upload data";
    private FdpCallbacks.UploadDataListener uploadDataListener;
    private BaseContract.View mView;
    private AppDataManager mAppDataManager;
    private boolean showProgress;
    private String token;


    private UploadDataManager(BaseContract.View view, AppDataManager appDataManager, FdpCallbacks.UploadDataListener listener, boolean showProgress) {
        this.mAppDataManager = appDataManager;
        this.mView = view;
        this.uploadDataListener = listener;
        this.showProgress = showProgress;
        token = mAppDataManager.getAccessToken();
    }

    public static UploadDataManager newInstance(BaseContract.View view, AppDataManager appDataManager, FdpCallbacks.UploadDataListener listener, boolean showProgress) {
        return new UploadDataManager(view, appDataManager, listener, showProgress);
    }

    public AppDataManager getAppDataManager() {
        return mAppDataManager;
    }

    public BaseContract.View getView() {
        return mView;
    }

    public void uploadFarmersData(JSONObject farmersJsonObject) {
        if (showProgress)
            getView().setLoadingMessage("Syncing farmer data...");
        getAppDataManager().getFdpApiService()
                .uploadFarmersData(token, farmersJsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ServerResponse>() {
                    @Override
                    public void onSuccess(ServerResponse response) {
                        if (uploadDataListener != null)
                            uploadDataListener.onUploadComplete("Data upload successful.");
                        uploadDataListener = null;
                        getAppDataManager().setBooleanValue("reload", true);
                    }
                    @Override
                    public void onError(Throwable e) {
                        AppLogger.e("OnError " + (uploadDataListener != null));
                        if (uploadDataListener != null) {
                            uploadDataListener.onUploadError(e);
                            uploadDataListener = null;
                        }
                    }
                });
    }
}
