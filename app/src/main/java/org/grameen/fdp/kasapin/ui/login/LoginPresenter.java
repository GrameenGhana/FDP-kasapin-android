package org.grameen.fdp.kasapin.ui.login;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.DataManager;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.model.LoginResponse;
import org.grameen.fdp.kasapin.syncManager.DownloadResources;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import javax.inject.Inject;

import io.reactivex.observers.DisposableSingleObserver;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter, FdpCallbacks.OnDownloadResourcesListener {

    private AppDataManager mAppDataManager;


    @Inject
    public LoginPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;


    }


    @Override
    public void makeLoginApiCall(String email, String password) {
        getView().showLoading("Signing In", "Please wait...", true, 0, false);

        runSingleCall(mAppDataManager.getFdpApiService().makeLoginCall(email, password)
                .subscribeWith(
                        new DisposableSingleObserver<LoginResponse>() {
                            @Override
                            public void onSuccess(LoginResponse responseBody) {


                                String token = responseBody.getToken();
                                mAppDataManager.setAccessToken(token);
                                fetchUserData(token);


                            }

                            @Override
                            public void onError(Throwable e) {
                                AppLogger.e(TAG, e.getMessage());
                                getView().hideLoading();
                                getView().showMessage(e.getMessage());
                            }
                        }));
    }


    @Override
    public void fetchUserData(String token) {

        runSingleCall(mAppDataManager.getFdpApiService().fetchUserData(token)
                .subscribeWith(new DisposableSingleObserver<User>() {
                    @Override
                    public void onSuccess(User user) {


                        Country country = new Country();
                        country.setId(user.getCountryId());
                        country.setCurrency("GHS");
                        country.setIsoCode("GHA");
                        country.setName("Ghana");

                        getAppDataManager().setStringValue("country", getGson().toJson(country));


                        mAppDataManager.updateUserInfo(user);
                        fetchData();

                    }

                    @Override
                    public void onError(Throwable e) {
                        AppLogger.e(TAG, e);
                        getView().hideLoading();
                        getView().showMessage(e.getMessage());
                    }
                }));
    }


    @Override
    public void fetchData() {

        DownloadResources.newInstance(getView(), mAppDataManager, this, true).getSurveyData();

    }

    @Override
    public void openNextActivity() {


        getView().openNextActivity();


    }


    @Override
    public void onSuccess(String message) {
        AppLogger.i(TAG, "**** On Success");
        getView().hideLoading();

        getView().showMessage(message);

        //Todo go to next activity
        getAppDataManager().setUserLoggedInMode(DataManager.LoggedInMode.LOGGED_IN);
        getView().openNextActivity();


    }

    @Override
    public void onError(Throwable e) {
        getView().hideLoading();
        getView().showMessage(e.getMessage());

        AppLogger.e(TAG, e);
        e.printStackTrace();


        getView().showDialog(true, "An error occurred.", e.getMessage(), (dialog, which) -> {

            dialog.dismiss();

        }, "OK", null, "", 0);


    }
}
