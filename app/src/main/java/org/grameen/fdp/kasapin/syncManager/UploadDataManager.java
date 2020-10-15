package org.grameen.fdp.kasapin.syncManager;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.network.model.ServerResponse;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;


public class UploadDataManager {
    String TAG = "Upload data";
    private FdpCallbacks.UploadDataListener uploadDataListener;
    private BaseContract.View mView;
    private AppDataManager mAppDataManager;
    private boolean showProgress;
    private String token;

    int REQUEST_SIZE = 2;
    int INDEX = 0;
    int BATCH_SIZE = 1;


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

    public void uploadFarmersData(JSONObject farmersJsonObject, List<JSONObject> imagesArray) {

        AppLogger.e(TAG, "Images array size is ==> " + imagesArray.size());
        if (showProgress)
            getView().setLoadingMessage("Uploading farmer data...");
        getAppDataManager().getFdpApiService()
                .uploadFarmersData(token, farmersJsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ServerResponse>() {
                    @Override
                    public void onSuccess(ServerResponse response) {

                        if(imagesArray.isEmpty()){
                            success();
                            return;
                        }

                        JSONObject submissionJson;
                        try {
                             submissionJson = farmersJsonObject.getJSONObject("submission");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            submissionJson = new JSONObject();
                        }

                        getView().setLoadingMessage("Farmer data uploaded.\nUploading images now. This might take longer...");
                        sendImagesInBatches(submissionJson, imagesArray);
                    }
                    @Override
                    public void onError(Throwable e) {
                             error(e);
                    }
                });
    }


    void sendImagesInBatches(JSONObject submissionData, List<JSONObject> imagesArray) {
        int imagesArraySize = imagesArray.size();
        List<Single<ServerResponse>> singleList = new ArrayList<>();

        //Build payload data for (REQUEST_SIZE) requests to be sent simultaneously
        for (int i = 0; i < REQUEST_SIZE; i++) {
            JSONObject payload = new JSONObject();
            try {
                payload.put("submission", submissionData);
                List<JSONObject> subList;
                if (imagesArraySize <= BATCH_SIZE) {
                    subList = imagesArray;
                } else {
                    if (imagesArraySize - INDEX >= BATCH_SIZE) {
                        subList = imagesArray.subList(INDEX, BATCH_SIZE + INDEX);
                        INDEX += BATCH_SIZE;

                    } else {
                        subList = imagesArray.subList(INDEX, imagesArraySize);
                        INDEX = imagesArraySize;
                    }
                }
                if(!subList.isEmpty()) {
                    JSONArray array = new JSONArray(subList);
                    payload.put("data", array);

                    AppLogger.e(TAG, "subList size => " + subList.size());
                    singleList.add(getAppDataManager().getFdpApiService()
                            .uploadFarmersData(token, payload));

                    if (INDEX == 0 || INDEX >= imagesArraySize)
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        AppLogger.e(TAG, "batch list size => " + singleList.size());

        Single.merge(singleList).timeout(60, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSubscriber<ServerResponse>() {
                            @Override
                            public void onNext(ServerResponse serverResponse) {
                                if(imagesArraySize > INDEX)
                                    getView().setLoadingMessage(String.format("Uploading %s out of %s records...", INDEX + 1, imagesArraySize));

                                AppLogger.e(TAG, "Server response  => " + serverResponse.toString());
                             }
                            @Override
                            public void onError(Throwable t) {
                                AppLogger.e(TAG, "onError");

                                t.printStackTrace();
                                uploadDataListener.onUploadError(t);
                            }
                            @Override
                            public void onComplete() {
                                AppLogger.e(TAG, "OnComplete");
                                //Break out of the loop if all data has been uploaded
                                if (INDEX == 0 || INDEX >= imagesArraySize) {
                                    AppLogger.e(TAG, "BREAK LOOP");

                                   success();

                                } else
                                    sendImagesInBatches(submissionData, imagesArray);
                            }
                        });
    }

    private void success(){
        if (uploadDataListener != null)
            uploadDataListener.onUploadComplete("All data uploaded successfully!");
        uploadDataListener = null;
    }

    private void error(Throwable t){

        if (uploadDataListener != null) {
            t.printStackTrace();
            uploadDataListener.onUploadError(t);
            uploadDataListener = null;
        }
    }
}
