package org.grameen.fdp.kasapin.ui.AddEditFarmerPlot;


import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

public class AddEditFarmerPlotContract {
    public interface View extends BaseContract.View {
        void showForm(List<FormAndQuestions> formAndQuestionsList);

        void showPlotDetailsActivity(Plot plot);

        void moveToMapActivity(Plot plot);
    }

    public interface Presenter {
        void getPlotQuestions();

        void saveData(Plot plot, String flag);
    }
}
