package org.grameen.fdp.kasapin.ui.farmerProfile;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class AddFarmerPresenter extends BasePresenter<AddFarmerContract.View> implements AddFarmerContract.Presenter{

    AppDataManager mAppDataManager;



    @Inject
    public AddFarmerPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }




    @Override
    public void openNextActivity() {

    }


}
