package org.grameen.fdp.kasapin.ui.addPlotMonitoring;


import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Spinner;

import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class AddPlotMonitoringContract {

    public interface View extends BaseContract.View {
        void setUpViews();
        void setAreaUnits(String unit);
        void setProductionUnit(String unit);
        void refresh(Spinner spinner, @Nullable String defValue, List<String> items);

        void addViewsDynamically(Question q);
        android.view.View getLabelView(Question q);
        android.view.View getAOView(final Question q);
        android.view.View getCompetenceView(final Question q);
        android.view.View getReasonForFailureView(final Question q);
        void loadDynamicFragmentAndViews(FormAndQuestions monitoringPlotInfoQuestions, FormAndQuestions aoMonitoringQuestions);

        void openNextActivity();
    }

    public interface Presenter {
        void getAreaUnits(String farmerCode);
        void getMonitoringQuestions();
        void saveMonitoringData(Monitoring monitoring);


    }


}
