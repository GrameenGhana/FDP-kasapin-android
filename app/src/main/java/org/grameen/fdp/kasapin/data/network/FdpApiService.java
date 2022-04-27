package org.grameen.fdp.kasapin.data.network;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.grameen.fdp.kasapin.data.db.model.CountryAdminLevelDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.model.LoginRequest;
import org.grameen.fdp.kasapin.data.network.model.LoginResponse;
import org.grameen.fdp.kasapin.data.network.model.ServerResponse;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    public Single<ServerResponse> uploadFarmersData(String token, JSONObject data) {
        JsonObject gson = new JsonParser().parse(data.toString()).getAsJsonObject();
        return fdpApi.uploadFarmersData(token, gson).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ServerResponse> fetchFarmersData(String token, int countryId, int surveyorId, int pageUp, int pageDown) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country_id", countryId);
        jsonObject.addProperty("surveyor_id", surveyorId);
        jsonObject.addProperty("pstart", pageUp);
        jsonObject.addProperty("pend", pageDown);

        return fdpApi.downloadFarmerData(token, jsonObject).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable fetchFarmersDatatest(String token, int countryId, int surveyorId, int pageUp, int pageDown) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country_id", countryId);
        jsonObject.addProperty("surveyor_id", surveyorId);
        jsonObject.addProperty("pstart", pageUp);
        jsonObject.addProperty("pend", pageDown);

        return fdpApi.downloadFarmerDataTest(token, jsonObject).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).ignoreElement();
    }
}
