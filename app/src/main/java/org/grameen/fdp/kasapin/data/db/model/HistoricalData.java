package org.grameen.fdp.kasapin.data.db.model;


import org.json.JSONException;
import org.json.JSONObject;
public class HistoricalData {
    String id;
    String dateTime;
    String lastModifiedDate;
    String answersJson = "{}";
    String formId;
    String name;

    public HistoricalData() {
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getAnswersJson() {
        return answersJson;
    }

    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
