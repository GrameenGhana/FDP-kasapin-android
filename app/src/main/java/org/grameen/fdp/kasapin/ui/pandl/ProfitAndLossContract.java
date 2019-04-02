package org.grameen.fdp.kasapin.ui.pandl;


import android.support.annotation.Nullable;

import org.grameen.fdp.kasapin.ui.base.BaseContract;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class ProfitAndLossContract {



    public interface View extends  BaseContract.View {

        void issuePrint();
        void setUpViews();
        void moveToFdpStatusActivity();
        void getAllFarmerDataValues();
        boolean checkIfFarmerFdpStatusFormFilled(String code);


    }

     public interface Presenter {




    }



}
