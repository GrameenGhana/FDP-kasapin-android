package org.grameen.fdp.kasapin.ui.addFarmer;


import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

public class AddEditFarmerContract {
    public interface View extends BaseContract.View {
        void startCameraIntent();
        void disableViews();
        void showFormFragment();
        void moveToNextForm();
        void showFarmerDetailsActivity(Farmer farmer);
        void setUpViews(Farmer farmer);
        void finishActivity();
    }

    public interface Presenter {
        void loadFormFragment(String farmerId, int formId);
        void saveData(Farmer farmer, FormAnswerData answerData, boolean exit, boolean wasProfileImageEdited);
        void getFarmerData(String code);
    }
}
