package org.grameen.fdp.kasapin.ui.plotMonitoringActivity;


import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

public class PlotMonitoringContract {
    public interface View extends BaseContract.View {
        void setUpViews();

        void setupAdoptionObservationsTableView(FormAndQuestions aoFormQuestions, FormAndQuestions monitoringAOFormAndQuestions);

        void updateTableData(List<Monitoring> monitorings);
    }

    public interface Presenter {
        void getAOQuestions();

        void syncFarmerData(Farmer farmer, boolean showProgress);

        void getMonitoringForSelectedYear(Plot plot, int selectedYear);
    }
}
