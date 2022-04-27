package org.grameen.fdp.kasapin.ui.landing;


import androidx.annotation.Nullable;

import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

public class LandingContract {
    public interface View extends BaseContract.View {
        void showPopUp(@Nullable android.view.View v);

        void openMainActivity();

        void cacheFarmerImages(List<Farmer> farmers);
    }

    public interface Presenter {
        void showPopupDialog();

        void uploadLogsToServer();

        void getFarmers();

        void updateFarmerData(List<Farmer> updatedFarmers);
    }
}
