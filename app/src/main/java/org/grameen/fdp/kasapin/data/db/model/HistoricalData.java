package org.grameen.fdp.kasapin.data.db.model;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AangJnr on 23, November, 2018 @ 11:13 AM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class HistoricalData {

    String id;
    String dateTime;
    String lastModifiedDate;
    String answersJson = "{}";
    String formId;
    String name;


    public HistoricalData() {
    }


    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public String getFormId() {
        return formId;
    }

    public String getAnswersJson() {
        return answersJson;
    }

    public String getDateTime() {
        return dateTime;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public JSONObject getAnswersJsonObject() {

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(getAnswersJson());
        } catch (JSONException ignored) {
            jsonObject = new JSONObject();
        }


        return jsonObject;
    }
}
