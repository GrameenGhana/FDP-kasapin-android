package org.grameen.fdp.kasapin.ui.splash;

import org.grameen.fdp.kasapin.ui.base.BaseContract;

public class SplashContract {
    public interface View extends BaseContract.View {
        void animateLogoAndWait();
        void openLoginActivity();
        void openNextActivity();
    }

    public interface Presenter {
        void startDelay();
        void checkIfIsLoggedIn();
    }
}
