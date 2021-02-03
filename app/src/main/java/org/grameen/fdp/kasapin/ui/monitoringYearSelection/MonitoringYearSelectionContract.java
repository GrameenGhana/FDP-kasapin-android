package org.grameen.fdp.kasapin.ui.monitoringYearSelection;


import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

public class MonitoringYearSelectionContract {
    public interface View extends BaseContract.View {
        void setUpViews();

        void setupListAdapter();

        void showError();

        void setFarmerAndPlotData(Farmer farmer, Plot plot);
    }

    public interface Presenter {
        void getFarmerAndPlotData(String farmerCode, String plotExternal);
    }
}
