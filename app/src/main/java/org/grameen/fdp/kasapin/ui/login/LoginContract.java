package org.grameen.fdp.kasapin.ui.login;


import org.grameen.fdp.kasapin.ui.base.BaseContract;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class LoginContract {


    public interface View extends BaseContract.View {


        void openNextActivity();

    }

    public interface Presenter {

        void makeLoginApiCall(String email, String password);

        void fetchUserData(String token);

        void fetchData();


    }


}
