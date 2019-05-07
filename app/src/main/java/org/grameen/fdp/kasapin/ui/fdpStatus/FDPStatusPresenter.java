package org.grameen.fdp.kasapin.ui.fdpStatus;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FDPStatusPresenter extends BasePresenter<FDPStatusContract.View> implements FDPStatusContract.Presenter {

    private AppDataManager mAppDataManager;


    @Inject
    public FDPStatusPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


}
