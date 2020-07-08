package org.grameen.fdp.kasapin.ui.farmerProfile;


import android.widget.Button;

import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FarmerProfileContract {


    public interface View extends BaseContract.View {

        void initializeViews(boolean shouldLoadButtons);

        void setUpFarmersPlotsAdapter(List<Plot> plotList);

        void addButtons(List<Button> buttons);

        void updateFarmerSyncStatus();


    }

    public interface Presenter {

        void getFarmersPlots(String farmerCode);

        void deletePlot(Plot plot);

        void loadDynamicButtons(List<FormAndQuestions> formAndQuestions);

        void syncFarmerData(Farmer farmer, boolean showProgress);


    }

}
