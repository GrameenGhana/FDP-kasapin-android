package org.grameen.fdp.kasapin.ui.farmAssessment;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

public class FarmAssessmentPresenter extends BasePresenter<FarmAssessmentContract.View> implements FarmAssessmentContract.Presenter {

    @Inject
    public FarmAssessmentPresenter(AppDataManager appDataManager) {
        super(appDataManager);
    }
}
