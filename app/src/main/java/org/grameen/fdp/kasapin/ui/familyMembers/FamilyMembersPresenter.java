package org.grameen.fdp.kasapin.ui.familyMembers;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FamilyMembersPresenter extends BasePresenter<FamilyMembersContract.View> implements FamilyMembersContract.Presenter {
    AppDataManager mAppDataManager;
    @Inject
    public FamilyMembersPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void getFamilyMembersFormAndQuestions() {
        runSingleCall(getAppDataManager().getDatabaseManager().formAndQuestionsDao().getFormAndQuestionsByName(AppConstants.FAMILY_MEMBERS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formAndQuestions ->
                                getView().setupTableView(formAndQuestions),
                        throwable -> getView().showMessage(throwable.getMessage())));
    }
}
