package org.grameen.fdp.kasapin.ui.pandl;


import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.json.JSONObject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class ProfitAndLossContract {


    public interface View extends BaseContract.View {

        void enablePrint();

        void issuePrinting();

        void setUpViews();

        void moveToFdpStatusActivity();

        void setAnswerData(JSONObject jsonObject);

        boolean checkIfFarmerFdpStatusFormFilled(String code);

        void populateTableData();

        void reloadTableData();

    }

    public interface Presenter {

        void getAllAnswers(String farmerCode);

        void updatePlotData(Plot plot, boolean reloadTable);

    }


}
