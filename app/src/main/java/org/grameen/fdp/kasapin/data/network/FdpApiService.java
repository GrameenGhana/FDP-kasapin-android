package org.grameen.fdp.kasapin.data.network;


import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.model.DataWrapper;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.model.FormTranslation;
import org.grameen.fdp.kasapin.data.network.model.LoginRequest;
import org.grameen.fdp.kasapin.data.network.model.LoginResponse;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by AangJnr on 05, December, 2018 @ 4:03 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Singleton
public class FdpApiService {

    @Inject
    AppDataManager appDataManager;
    private FdpApi fdpApi;

    @Inject
    public FdpApiService(FdpApi _fdpApi) {
        this.fdpApi = _fdpApi;
    }





    public Single<LoginResponse> makeLoginCall(String email, String password) {
        LoginRequest.ServerLoginRequest loginRequest = new LoginRequest.ServerLoginRequest(email, password);

        return fdpApi.makeLoginCall(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<User> fetchUserData(String token) {

        return fdpApi.getUser(token) .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    public Single<DataWrapper> fetchSurveyData(int id, String token) {
        return fdpApi.getSurveyData(id, token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DataWrapper> fetchSurveyData2(int id, String token) {
        return fdpApi.getSurveyData2(id, token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



}
