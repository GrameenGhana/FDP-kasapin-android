package org.grameen.fdp.kasapin.data.network;

import org.grameen.fdp.kasapin.MockData;
import org.grameen.fdp.kasapin.data.db.model.CountryAdminLevelDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.model.LoginResponse;
import org.grameen.fdp.kasapin.data.network.model.ServerResponse;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Single;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FdpApiServiceTest {
    @Mock
    FdpApiService fdpApiService;

    @Test
    public void test_that_makeLoginCall_returns_a_login_response_with_accessToken() {
        when(fdpApiService.makeLoginCall(MockData.fakeEmail, MockData.fakePassword)).thenAnswer(i->
                 Single.just(MockData.getFakeLoginResponse()));

        Single<LoginResponse> response = fdpApiService.makeLoginCall(MockData.fakeEmail, MockData.fakePassword);
        verify(fdpApiService, atLeastOnce()).makeLoginCall(ArgumentMatchers.eq(MockData.fakeEmail), ArgumentMatchers.eq(MockData.fakePassword));
        assertEquals(response.blockingGet().getToken(), MockData.fakeAccessToken);
    }

    @Test
    public void test_that_fetchUserData_returns_a_user_object_from_server() {
        when(fdpApiService.fetchUserData(MockData.fakeAccessToken)).thenAnswer(i ->
                Single.just(MockData.getFakeUser()));

        Single<User> response = fdpApiService.fetchUserData(MockData.fakeAccessToken);
        verify(fdpApiService, atLeastOnce()).fetchUserData(ArgumentMatchers.eq(MockData.fakeAccessToken));
        assertNotNull(response.blockingGet());
    }

    @Test
    public void test_that_fetchCommunitiesData_returns_CountryAdminLevelDataWrapper_object() {
        when(fdpApiService.fetchCommunitiesData(1, MockData.fakeAccessToken)).thenAnswer(i ->
                Single.just(MockData.getCountryAdminLevelDataWrapper()));

        Single<CountryAdminLevelDataWrapper> response = fdpApiService.fetchCommunitiesData(1, MockData.fakeAccessToken);
        verify(fdpApiService, atLeastOnce()).fetchCommunitiesData(ArgumentMatchers.eq(1), ArgumentMatchers.eq(MockData.fakeAccessToken));
        assertNotNull(response.blockingGet());
    }

    @Test
    public void test_that_fetchSurveyData_returns_a_FormsDataWrapper_object() {
        when(fdpApiService.fetchSurveyData(1, MockData.fakeAccessToken)).thenAnswer(i ->
                Single.just(MockData.getFakeFormsDataWrapper()));

        Single<FormsDataWrapper> response = fdpApiService.fetchSurveyData(1, MockData.fakeAccessToken);
        verify(fdpApiService, atLeastOnce()).fetchSurveyData(ArgumentMatchers.eq(1), ArgumentMatchers.eq(MockData.fakeAccessToken));
        assertNotNull(response.blockingGet());
    }

    @Test
    public void test_that_fetchRecommendations_returns_RecommendationsDataWrapper_object_from_server() {
        when(fdpApiService.fetchRecommendations(1,1, MockData.fakeAccessToken)).thenAnswer(i ->
                Single.just(MockData.getFakeRecommendationsDataWrapper()));

        Single<RecommendationsDataWrapper> response = fdpApiService.fetchRecommendations(1,1, MockData.fakeAccessToken);
        verify(fdpApiService, atLeastOnce()).fetchRecommendations(1,1, MockData.fakeAccessToken);
        assertNotNull(response.blockingGet());
        assert (response.blockingGet().getData().isEmpty());
    }

    @Test
    public void test_that_uploadFarmersData_returns_a_successful_ServerResponse() {

        JSONObject fakePayload = MockData.getFakeJSONObject();
        when(fdpApiService.uploadFarmersData(MockData.fakeAccessToken, fakePayload)).thenAnswer(i ->
                Single.just(MockData.getFakeResponse()));
        Single<ServerResponse> response = fdpApiService.uploadFarmersData(MockData.fakeAccessToken, fakePayload);
        verify(fdpApiService, atLeastOnce()).uploadFarmersData(MockData.fakeAccessToken, fakePayload);
        assertNotNull(response.blockingGet());
        assert(response.blockingGet().getStatus() == 200);
    }

    @Test
    public void test_that_fetchFarmersData_returns_ServerResponse_object_with_farmer_data() {
        when(fdpApiService.fetchFarmersData(MockData.fakeAccessToken, 1, 1, 1, 0)).thenAnswer(i ->
                Single.just(MockData.getFakeResponse()));

        Single<ServerResponse> response = fdpApiService.fetchFarmersData(MockData.fakeAccessToken, 1, 1, 1, 0);
        verify(fdpApiService, atLeastOnce()).fetchFarmersData(MockData.fakeAccessToken, 1, 1, 1, 0);
        assertNotNull(response.blockingGet());
        assert(response.blockingGet().getData().isEmpty());
    }
}