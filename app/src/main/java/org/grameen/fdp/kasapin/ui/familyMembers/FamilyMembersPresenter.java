package org.grameen.fdp.kasapin.ui.familyMembers;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

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
