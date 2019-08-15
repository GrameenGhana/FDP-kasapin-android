package org.grameen.fdp.kasapin.ui.plotMonitoringActivity;


import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class PlotMonitoringContract {


    public interface View extends BaseContract.View {
        void setUpViews();
        void setupAdoptionObservationsTableView(FormAndQuestions aoFormQuestions, FormAndQuestions monitoringAOFormAndQuestions);
        //void setupTableViewPager();
        void updateTableData(List<Monitoring> monitorings);
    }

    public interface Presenter {
        void getAOQuestions();
        void syncFarmerData(RealFarmer farmer, boolean showProgress);
        void getMonitoringForSelectedYear(Plot plot, int selectedYear);

    }


}
