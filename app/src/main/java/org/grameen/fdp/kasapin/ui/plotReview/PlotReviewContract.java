package org.grameen.fdp.kasapin.ui.plotReview;


import android.support.annotation.Nullable;

import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

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
