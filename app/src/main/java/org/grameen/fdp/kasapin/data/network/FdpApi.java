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
    /**
     * Makes a login call to the server
     * @param loginRequest object holds the user data used to authenticate user at the server side
     */
    @POST(AppConstants.API_VERSION + "auth/user/login")
    Single<LoginResponse> makeLoginCall(@Body LoginRequest.ServerLoginRequest loginRequest);

    /**
     * Returns a user data from the server
     */
    @GET(AppConstants.API_VERSION + "auth/user/details")
    Single<User> getUser(@Query("token") String token);

    /**
     * Makes a call to return Communities/Villages data from the server
     * @param countryId is the country id of which the villages data should be returned
     * @param token is the token required to authenticate the user at the server side
     */
    @GET(AppConstants.API_VERSION + "auth/user/countryadmin/{id}")
    Single<CountryAdminLevelDataWrapper> getCommunitiesData(@Path("id") int countryId, @Query("token") String token);

    /**
     * Makes a call to return Forms, Questions and answers data from the server
     * @param countryId is the country id of which the villages data should be returned
     * @param token is the token required to authenticate the user at the server side
     */
    @GET(AppConstants.API_VERSION + "auth/user/survey/{id}")
    Single<FormsDataWrapper> getSurveyData(@Path("id") int countryId, @Query("token") String token);

    /**
     * Makes a call to return Recommendations data from the server
     * @param cropId is the crop id
     * @param countryId is the country id of which the villages data should be returned
     * @param token is the token required to authenticate the user at the server side
     */
    @GET(AppConstants.API_VERSION + "auth/user/recommendation/{crop_id}/{country_id}")
    Single<RecommendationsDataWrapper> getRecommendations(@Path("crop_id") int cropId, @Path("country_id") int countryId, @Query("token") String token);

    /**
     * Makes a call to upload farmer data to the server
     * @param token is the token required to authenticate the user at the server side
     * @param farmersData json body of the payload
     */
    //@POST(AppConstants.API + "synchupdata")
    @POST(AppConstants.API + "syncupdatatest")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Single<ServerResponse> uploadFarmersData(@Query("token") String token, @Body JsonObject farmersData);

    /**
     * Makes a call to download  data from the server
     * @param token is the token required to authenticate the user at the server side
     * @param body json body of the payload  request containing pagination data
     */
    @POST(AppConstants.API + "syncdowndata")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Single<ServerResponse> downloadFarmerData(@Query("token") String token, @Body JsonObject body);
}
