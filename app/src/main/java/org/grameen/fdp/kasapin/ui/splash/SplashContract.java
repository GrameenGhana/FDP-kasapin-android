package org.grameen.fdp.kasapin.ui.splash;


import android.content.Intent;

import org.grameen.fdp.kasapin.ui.base.BaseContract;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class SplashContract {



    public interface View extends  BaseContract.View {

        void animateLogoAndWait();

        void openLoginActivity();

        void openNextActivity();


    }

     public interface Presenter {

        void startDelay();



    }



}
