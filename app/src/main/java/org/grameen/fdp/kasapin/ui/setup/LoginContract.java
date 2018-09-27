package org.grameen.fdp.kasapin.ui.setup;


import org.grameen.fdp.kasapin.di.Scope.PerActivity;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class LoginContract {



    public interface View extends  BaseContract.View {

        void onLoginSuccessful();

    }

     public interface Presenter {

        void makeLoginApiCall(String email, String password);




    }



}
