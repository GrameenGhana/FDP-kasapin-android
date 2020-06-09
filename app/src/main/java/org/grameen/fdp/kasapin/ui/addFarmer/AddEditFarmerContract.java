package org.grameen.fdp.kasapin.ui.addFarmer;


import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class AddEditFarmerContract {


    public interface View extends BaseContract.View {

        void startCameraIntent();

        void disableViews();

        void showFormFragment(FormAnswerData surveyAnswer);

        void moveToNextForm();

        void showFarmerDetailsActivity(RealFarmer farmer);

        void setUpViews();

        void finishActivity();
    }

    public interface Presenter {

        void loadFormFragment(String farmerId, int formId);

        void saveData(RealFarmer farmer, FormAnswerData answerData, boolean exit);

    }


}
