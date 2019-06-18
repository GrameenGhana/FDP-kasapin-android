package org.grameen.fdp.kasapin.ui.plotReview;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class PlotReviewPresenter extends BasePresenter<PlotReviewContract.View> implements PlotReviewContract.Presenter {

    private AppDataManager mAppDataManager;


    @Inject
    public PlotReviewPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void getAllPlotQuestions() {


    }


    @Override
    public void saveAnswerData(FormAnswerData answerData) {


    }

    @Override
    public void openNextActivity() {
        getView().openMainActivity();


    }
}
