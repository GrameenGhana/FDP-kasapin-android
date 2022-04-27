package org.grameen.fdp.kasapin.ui.detailedYearMonthlyView;

import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

public class DetailedMonthContract {
    public interface View extends BaseContract.View {
        void setPlotsData(List<Plot> plotsData);

        void setData();
    }

    public interface Presenter {
        void getPlotsData(String farmerCode);
    }
}