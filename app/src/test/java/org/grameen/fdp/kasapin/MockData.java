package org.grameen.fdp.kasapin;

import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.json.JSONException;
import org.json.JSONObject;

public class MockData {
    public static String fakeEmail = "fakeuser@test.com";
    public static String fakeAccessToken = "xxxxxxxxxxxxxxxxxxxxxxxxxx";
    public static String fakePassword = "xxxxxxxxxxx";


    public JSONObject getFakeJSONObject(){
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
