package org.grameen.fdp.kasapin.ui.fdpStatus;

import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

public class FDPStatusContract {
    public interface View extends BaseContract.View {
        void showFormFragment();

        void saveData();

        void setUpViews();

        void dismiss();
    }

    public interface Presenter {
        void getAnswerData(String farmerCode, int formTranslationId);

        void saveData(Farmer farmer, FormAnswerData formAnswerData);
    }
}
