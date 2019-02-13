package org.grameen.fdp.kasapin.ui.splash;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.DataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

import static java.lang.Thread.sleep;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class SplashPresenter extends BasePresenter<SplashContract.View> implements SplashContract.Presenter{

    AppDataManager mAppDataManager;



    @Inject
    public SplashPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }




    @Override
    public void openNextActivity() {

        if(mAppDataManager.getUserLoggedInMode() == DataManager.LoggedInMode.LOGGED_OUT.getType())
            getView().openLoginActivity();
        else
            getView().openNextActivity();

    }



    @Override
    public void startDelay() {

        getView().animateLogoAndWait();

    }





}
