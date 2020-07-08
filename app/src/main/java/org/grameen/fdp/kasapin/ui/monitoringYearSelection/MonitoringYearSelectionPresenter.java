package org.grameen.fdp.kasapin.ui.monitoringYearSelection;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

public class MonitoringYearSelectionPresenter extends BasePresenter<MonitoringYearSelectionContract.View> implements MonitoringYearSelectionContract.Presenter {
    AppDataManager mAppDataManager;

    @Inject
    public MonitoringYearSelectionPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }
}
