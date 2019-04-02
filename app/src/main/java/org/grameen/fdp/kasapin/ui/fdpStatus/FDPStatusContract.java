package org.grameen.fdp.kasapin.ui.fdpStatus;


import android.support.annotation.Nullable;

import org.grameen.fdp.kasapin.ui.base.BaseContract;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FDPStatusContract {



    public interface View extends  BaseContract.View {


        void showPopUp(@Nullable android.view.View v);

        void openMainActivity();




    }

     public interface Presenter {

         void showPopupDialog();

         void uploadLogsToServer();




    }



}
