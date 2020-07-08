package org.grameen.fdp.kasapin.ui.pandl;


import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.json.JSONObject;

public class ProfitAndLossContract {
    public interface View extends BaseContract.View {
        void issuePrinting();
        void setUpViews();
        void moveToFdpStatusActivity();
        void setAnswerData(JSONObject jsonObject);
        boolean checkIfFarmerFdpStatusFormFilled(String code);
        void populateTableData();
        void loadTableData();
    }

    public interface Presenter {
        void getAllAnswers(String farmerCode);
        void updatePlotData(Plot plot, boolean reloadTable);
        void saveLabourValues(FormAnswerData formAnswerData, Farmer farmer);
    }
}
