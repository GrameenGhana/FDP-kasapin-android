package org.grameen.fdp.kasapin.ui.familyMembers;


import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FamilyMembersContract {


    public interface View extends BaseContract.View {
        void setUpViews();

        void setupTableView(FormAndQuestions familyMembersFormAndQuestions);
    }

    public interface Presenter {
        void getFamilyMembersFormAndQuestions();

    }


}
