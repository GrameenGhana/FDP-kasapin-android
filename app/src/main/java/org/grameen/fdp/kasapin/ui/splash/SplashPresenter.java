package org.grameen.fdp.kasapin.ui.splash;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.DataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

public class SplashPresenter extends BasePresenter<SplashContract.View> implements SplashContract.Presenter {
    AppDataManager mAppDataManager;

    @Inject
    public SplashPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void checkIfIsLoggedIn() {
        if (mAppDataManager.getUserLoggedInMode() == DataManager.LoggedInMode.LOGGED_IN.getType())
            getView().openNextActivity();
        else
            getView().openLoginActivity();
    }

    @Override
    public void startDelay() {
        getView().animateLogoAndWait();
    }
}
