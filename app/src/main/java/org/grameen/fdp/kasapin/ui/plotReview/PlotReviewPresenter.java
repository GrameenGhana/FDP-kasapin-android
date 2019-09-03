package org.grameen.fdp.kasapin.ui.plotReview;


import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

        runSingleCall(getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByDisplayType(AppConstants.DISPLAY_TYPE_PLOT_FORM)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formsAndQuestions -> {
                    List<Question> questionList  = new ArrayList<>();
                    for(FormAndQuestions formAndQuestions : formsAndQuestions)
                        questionList.addAll(formAndQuestions.getQuestions());

                        getView().setPlotQuestions(questionList);
                },
                        throwable -> {
                    getView().showMessage(R.string.error_has_occurred);
                    throwable.printStackTrace();
                }));



    }


    @Override
    public void saveAnswerData(FormAnswerData answerData) {


    }

    @Override
    public void openNextActivity() {
        getView().openMainActivity();


    }
}
