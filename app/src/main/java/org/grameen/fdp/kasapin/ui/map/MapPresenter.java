package org.grameen.fdp.kasapin.ui.map;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class MapPresenter extends BasePresenter<MapContract.View> implements MapContract.Presenter {

    private AppDataManager mAppDataManager;


    @Inject
    public MapPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void openNextActivity() {
        getView().openMainActivity();


    }
}
