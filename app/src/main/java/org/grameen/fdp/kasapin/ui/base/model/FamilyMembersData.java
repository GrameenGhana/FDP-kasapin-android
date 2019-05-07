package org.grameen.fdp.kasapin.ui.base.model;

import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by aangjnr on 08/02/2018.
 */

public class FamilyMembersData {


    Integer id;
    List<Question> questionList;
    JSONObject jsonObject;


    public FamilyMembersData(int i, List<Question> list, JSONObject jsonObject) {
        this.id = i;
        this.questionList = list;
        this.jsonObject = jsonObject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
