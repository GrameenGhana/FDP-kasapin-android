package org.grameen.fdp.kasapin.ui.familyMembers;


import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

public class FamilyMembersContract {
    public interface View extends BaseContract.View {
        void setUpViews();

        void setupTableView(FormAndQuestions familyMembersFormAndQuestions);
    }

    public interface Presenter {
        void getFamilyMembersFormAndQuestions();
    }
}
