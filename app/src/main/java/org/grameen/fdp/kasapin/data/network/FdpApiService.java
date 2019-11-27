package org.grameen.fdp.kasapin.data.network;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.model.CountryAdminLevelDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.model.FarmerAndAnswers;
import org.grameen.fdp.kasapin.data.network.model.LoginRequest;
import org.grameen.fdp.kasapin.data.network.model.LoginResponse;
import org.grameen.fdp.kasapin.data.network.model.Response;
import org.grameen.fdp.kasapin.data.network.model.SyncDownData;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AangJnr on 05, December, 2018 @ 4:03 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Singleton
public class FdpApiService {

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

        return fdpApi.getUser(token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<CountryAdminLevelDataWrapper> fetchCommunitiesData(int countryId, String token) {
        return fdpApi.getCommunitiesData(countryId, token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



    public Single<FormsDataWrapper> fetchSurveyData(int id, String token) {
        return fdpApi.getSurveyData(id, token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<RecommendationsDataWrapper> fetchRecommendations(int cropId, int countryId, String token) {
        return fdpApi.getRecommendations(cropId, countryId, token).subscribeOn(Schedulers.io());
    }

    public Single<Response> pushFarmersData(String token, JSONObject data) {

        JsonObject gson = new JsonParser().parse(data.toString()).getAsJsonObject();


        return fdpApi.postFarmers(token, gson).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<SyncDownData> fetchSyncDownData(String token, int countryId, int surveyorId, int pageUp, int pageDown) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country_id", countryId);
        jsonObject.addProperty("surveyor_id", surveyorId);
        jsonObject.addProperty("pstart", pageUp);
        jsonObject.addProperty("pend", pageDown);

        return fdpApi.getSyncDownData(token, jsonObject).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
