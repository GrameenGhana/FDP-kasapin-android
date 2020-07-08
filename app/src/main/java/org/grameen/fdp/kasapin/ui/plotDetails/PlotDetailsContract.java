package org.grameen.fdp.kasapin.ui.plotDetails;


import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

public class PlotDetailsContract {
    public interface View extends BaseContract.View {
        void showForm(List<FormAndQuestions> formAndQuestionsList);
        void setAreaUnits(String unit);
        void setProductionUnit(String unit);
        void loadRecommendation(List<Recommendation> recommendations);
        void showRecommendation();
    }

    public interface Presenter {
        void getPlotQuestions();
        void getAreaUnits(String farmerCode);
        void getRecommendations(int cropId);
        void saveData(Plot plot);
    }
}
