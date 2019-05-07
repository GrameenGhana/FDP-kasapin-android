package org.grameen.fdp.kasapin.ui.base;


import android.support.v7.app.AppCompatActivity;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.network.model.BaseModel;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 8:02 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {

    public AppDataManager mAppDataManager;
    protected String TAG = "";
    private V mView;


    @Inject
    public BasePresenter(AppDataManager appDataManager) {
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void takeView(V view) {
        TAG = view.getClass().getSimpleName() + "\t";
        mView = view;

    }


    public AppCompatActivity getContext() {
        return (AppCompatActivity) mView;
    }

    @Override
    public void dropView() {
        if (mView != null) mView = null;
    }

    @Override
    public void setUserAsLoggedOut() {

        //getAppDataManager().setAccessToken(null);
        getAppDataManager().setUserAsLoggedOut();

    }

    public boolean isViewAttached() {
        return mView != null;
    }

    @Override
    public void openNextActivity() {

    }


    @Override
    public void onTokenExpire() {

        getView().openLoginActivityOnTokenExpire();

    }


    public V getView() {
        return mView;
    }


    public AppDataManager getAppDataManager() {
        return mAppDataManager;
    }


    protected void runSingleCall(Single<BaseModel> call, DisposableSingleObserver<BaseModel> disposableSingleObserver) {

        getAppDataManager().getCompositeDisposable().add(call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableSingleObserver));


    }

    protected void runSingleCall(Disposable disposableSingleObserver) {
        getAppDataManager().getCompositeDisposable().add(disposableSingleObserver);

    }


}