package org.grameen.fdp.kasapin.data.network;


import com.google.gson.JsonObject;

import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.model.FarmerAndAnswers;
import org.grameen.fdp.kasapin.data.network.model.LoginRequest;
import org.grameen.fdp.kasapin.data.network.model.LoginResponse;
import org.grameen.fdp.kasapin.data.network.model.Response;
import org.grameen.fdp.kasapin.data.network.model.SyncDownData;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by AangJnr on 05, December, 2018 @ 4:13 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public interface FdpApi {

    @POST(AppConstants.API_VERSION + "auth/user/login")
    Single<LoginResponse> makeLoginCall(@Body LoginRequest.ServerLoginRequest loginRequest);

    @GET(AppConstants.API_VERSION + "auth/user/details")
    Single<User> getUser(@Query("token") String token);


    @POST(AppConstants.API_VERSION + "syncdowndata")
    Single<SyncDownData> getSyncDownData(@Query("token") String token, int countryId, int surveyorId, int pageStart, int pageEnd);


    @GET(AppConstants.API_VERSION + "auth/user/survey/{id}")
    Single<FormsDataWrapper> getSurveyData(@Path("id") int countryId, @Query("token") String token);

    @GET(AppConstants.API_VERSION + "auth/user/survey/{id}")
    Single<FormsDataWrapper> getSurveyData2(@Path("id") int countryId, @Query("token") String token);

    @GET(AppConstants.API_VERSION + "auth/user/recommendation/{crop_id}/{country_id}")
    Single<RecommendationsDataWrapper> getRecommendations(@Path("crop_id") int cropId, @Path("country_id") int countryId, @Query("token") String token);

    @POST(AppConstants.API + "synchupdata")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Single<Response> postFarmers(@Query("token") String token, @Body JsonObject farmersData);







}
