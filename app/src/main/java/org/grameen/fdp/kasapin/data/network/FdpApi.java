package org.grameen.fdp.kasapin.data.network;


import com.google.gson.JsonObject;

import org.grameen.fdp.kasapin.data.db.model.CountryAdminLevelDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.model.ServerResponse;
import org.grameen.fdp.kasapin.data.network.model.LoginRequest;
import org.grameen.fdp.kasapin.data.network.model.LoginResponse;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public interface FdpApi {
    @POST(AppConstants.API_VERSION + "auth/user/login")
    Single<LoginResponse> makeLoginCall(@Body LoginRequest.ServerLoginRequest loginRequest);

    @GET(AppConstants.API_VERSION + "auth/user/details")
    Single<User> getUser(@Query("token") String token);

    @GET(AppConstants.API_VERSION + "auth/user/countryadmin/{id}")
    Single<CountryAdminLevelDataWrapper> getCommunitiesData(@Path("id") int countryId, @Query("token") String token);

    @GET(AppConstants.API_VERSION + "auth/user/survey/{id}")
    Single<FormsDataWrapper> getSurveyData(@Path("id") int countryId, @Query("token") String token);

    @GET(AppConstants.API_VERSION + "auth/user/recommendation/{crop_id}/{country_id}")
    Single<RecommendationsDataWrapper> getRecommendations(@Path("crop_id") int cropId, @Path("country_id") int countryId, @Query("token") String token);

    @POST(AppConstants.API + "synchupdata")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Single<ServerResponse> uploadFarmersData(@Query("token") String token, @Body JsonObject farmersData);

    @POST(AppConstants.API + "syncdowndata")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Single<ServerResponse> downloadFarmerData(@Query("token") String token, @Body JsonObject body);
}
