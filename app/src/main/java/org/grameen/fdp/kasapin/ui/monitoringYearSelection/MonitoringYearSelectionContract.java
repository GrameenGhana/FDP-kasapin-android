package org.grameen.fdp.kasapin.ui.monitoringYearSelection;


import org.grameen.fdp.kasapin.ui.base.BaseContract;

public class MonitoringYearSelectionContract {
    public interface View extends BaseContract.View {
        void setUpViews();
        void setupListAdapter();
    }

    public interface Presenter {}
}
