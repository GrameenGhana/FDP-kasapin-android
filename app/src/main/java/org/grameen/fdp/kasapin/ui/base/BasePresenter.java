package org.grameen.fdp.kasapin.ui.base;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.DataManager;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 8:02 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {

    private V mView;
    AppDataManager mAppDataManager;


    @Inject
    public BasePresenter(AppDataManager appDataManager) {
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void takeView(V view) {

        mView = view;

    }


    public AppCompatActivity getContext(){
        return (AppCompatActivity) mView;
    }

    @Override
    public void dropView() {
        if(mView != null) mView = null;
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
    public void toggleFullScreen(Boolean hideNavBar) {

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

 /*   public abstract void showDialog(Boolean cancelable, @Nullable String title, @Nullable String message,
                                    @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                    @NonNull String positiveText,
                                    @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                    @NonNull String negativeText, @Nullable int icon_drawable);
*/
 }