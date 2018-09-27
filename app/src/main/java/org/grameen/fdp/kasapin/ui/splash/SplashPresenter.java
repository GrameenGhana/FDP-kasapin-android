package org.grameen.fdp.kasapin.ui.splash;


import android.animation.Animator;
import android.content.Intent;
import android.view.Window;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.DataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.ui.setup.LoginActivity;

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

        if(mAppDataManager.getCurrentUserLoggedInMode() == DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType())
            getView().openLoginActivity();
        else
            getView().openNextActivity();


    }

    @Override
    public void toggleFullScreen(Boolean hideNavBar) {
        getView().onToggleFullScreenClicked(hideNavBar);

    }

    @Override
    public void startDelay() {

        getView().animateLogoAndWait();

    }





}
