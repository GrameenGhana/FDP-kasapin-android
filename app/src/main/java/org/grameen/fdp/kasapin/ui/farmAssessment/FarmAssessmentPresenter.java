package org.grameen.fdp.kasapin.ui.farmAssessment;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FarmAssessmentPresenter extends BasePresenter<FarmAssessmentContract.View> implements FarmAssessmentContract.Presenter {

    private AppDataManager mAppDataManager;


    @Inject
    public FarmAssessmentPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


}
