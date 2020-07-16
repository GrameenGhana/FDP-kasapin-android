package org.grameen.fdp.kasapin.ui.login;

import org.grameen.fdp.kasapin.ui.base.BaseContract;

public class LoginContract {
    public interface View extends BaseContract.View {
        void openNextActivity();
    }

    public interface Presenter {
        void makeLoginApiCall(String email, String password);
        void fetchUserData(String token);
        void fetchData();
        void setUserAsLoggedIn();
    }
}
