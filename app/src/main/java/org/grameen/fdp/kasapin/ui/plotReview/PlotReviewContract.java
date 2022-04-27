package org.grameen.fdp.kasapin.ui.plotReview;


import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

public class PlotReviewContract {
    public interface View extends BaseContract.View {
        void openMainActivity();

        void setPlotQuestions(List<Question> questions);

        void setUpViewPager();
    }

    public interface Presenter {
        void getAllPlotQuestions();

        void saveAnswerData(FormAnswerData answerData);
    }
}
