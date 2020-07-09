package org.grameen.fdp.kasapin;

import org.grameen.fdp.kasapin.data.db.entity.Community;
import org.grameen.fdp.kasapin.data.db.entity.District;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.data.db.model.CountryAdminLevelDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.FormsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.RecommendationsDataWrapper;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.model.LoginResponse;
import org.grameen.fdp.kasapin.data.network.model.Response;
import org.grameen.fdp.kasapin.data.network.model.ServerResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

public class MockData {
    public static String fakeEmail = "fakeuser@test.com";
    public static String fakeAccessToken = "xxxxxxxxxxxxxxxxxxxxxxxxxx";
    public static String fakePassword = "xxxxxxxxxxx";


    public static LoginResponse getFakeLoginResponse(){
        LoginResponse response = new LoginResponse();
        response.setToken(fakeAccessToken);
        return response;
    }

    public static User getFakeUser() {
        User user = new User();
        user.setId(1);
        user.setFirstName("Fake");
        user.setLastName("User");
        user.setCountryId(1);
        return user;
    }

    public static Community getFakeCommunityData(){
        Community community = new Community();
        community.setId(1);
        community.setName("Fake Community");
        community.setCountryId(1);
        return community;
    }

    public static District getFakeDistrictData(){
        District district = new District();
        district.setId(1);
        district.setCommunities(Collections.singletonList(getFakeCommunityData()));
        return district;
    }

    public static CountryAdminLevelDataWrapper getCountryAdminLevelDataWrapper(){
        CountryAdminLevelDataWrapper wrapper = new CountryAdminLevelDataWrapper();
        wrapper.setData(Collections.singletonList(getFakeDistrictData()));
        return wrapper;
    }

    public static FormsDataWrapper getFakeFormsDataWrapper(){
        FormsDataWrapper wrapper = new FormsDataWrapper();
        wrapper.setData(Collections.emptyList());
        return wrapper;
    }


    public static RecommendationsDataWrapper getFakeRecommendationsDataWrapper(){
        RecommendationsDataWrapper wrapper = new RecommendationsDataWrapper();
        wrapper.setData(Collections.emptyList());
        return wrapper;
    }

    public static ServerResponse getFakeResponse(){
        ServerResponse response = new ServerResponse();
        response.setMessage("Success");
        response.setStatus(200);
        response.setData(Collections.emptyList());
        return response;
    }

    public static JSONObject getFakeJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fake_question", "Fake Answer");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Question getFakeQuestion(){
       Question question = new Question();
       question.setId(1);
       question.setCaptionC("Fake Question");
       question.setLabelC("fake_question");
       question.setDefaultValueC("None");
       return question;
    }

    public SkipLogic getFakeSkipLogic(){
        SkipLogic skipLogic = new SkipLogic();
        skipLogic.setId(1);
        skipLogic.setFormula("B==B");
        return skipLogic;
    }




}
