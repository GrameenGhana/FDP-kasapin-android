package org.grameen.fdp.kasapin.ui.crops;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class BoilerPlatePresenter extends BasePresenter<BoilerPlateContract.View> implements BoilerPlateContract.Presenter {

    AppDataManager mAppDataManager;


    @Inject
    public BoilerPlatePresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void openNextActivity() {

    }


}
