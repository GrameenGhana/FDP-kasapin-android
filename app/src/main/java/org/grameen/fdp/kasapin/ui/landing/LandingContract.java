package org.grameen.fdp.kasapin.ui.landing;


import androidx.annotation.Nullable;

import org.grameen.fdp.kasapin.ui.base.BaseContract;

public class LandingContract {
    public interface View extends BaseContract.View {
        void showPopUp(@Nullable android.view.View v);
        void openMainActivity();
    }

    public interface Presenter {
        void showPopupDialog();
        void uploadLogsToServer();
    }
}
