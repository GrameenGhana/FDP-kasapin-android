package org.grameen.fdp.kasapin.ui.gpsPicker;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

public class MapPresenter extends BasePresenter<MapContract.View> implements MapContract.Presenter {
    @Inject
    public MapPresenter(AppDataManager appDataManager) {
        super(appDataManager);
    }

    @Override
    public void openNextActivity() {
        getView().openMainActivity();
    }
}
