package org.grameen.fdp.kasapin.ui.setup;


import android.content.Intent;

import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.data.AppDataManager;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter{

    AppDataManager mAppDataManager;



    @Inject
    public LoginPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void makeLoginApiCall(String email, String password) {
        getView().showLoading();

        //Todo perform Login
        getView().onLoginSuccessful();


    }


    @Override
    public void openNextActivity() {


        getView().openNextActivity();

    }


}
