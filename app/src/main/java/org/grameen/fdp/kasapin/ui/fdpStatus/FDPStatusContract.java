package org.grameen.fdp.kasapin.ui.fdpStatus;

import androidx.annotation.Nullable;

import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FDPStatusContract {
    public interface View extends BaseContract.View {
        void showFormFragment(@Nullable FormAnswerData answerData);

        void saveData();

        void setUpViews();

        void dismiss();
    }

    public interface Presenter {
        void getAnswerData(String farmerCode, int formTranslationId);

        void saveData(RealFarmer farmer, FormAnswerData formAnswerData);
    }
}
