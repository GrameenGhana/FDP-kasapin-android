package org.grameen.fdp.kasapin.ui.gpsPicker;


import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

public class MapContract {


    public interface View extends BaseContract.View {
        void openMainActivity();
        //void setPlotData(Plot plot);
}
    public interface Presenter {
       // void getPlotData(String externalId);
    }
}
