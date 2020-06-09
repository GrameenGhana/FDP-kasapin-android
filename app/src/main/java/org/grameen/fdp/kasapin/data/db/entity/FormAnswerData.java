package org.grameen.fdp.kasapin.data.db.entity;


/*
 * Created by AangJnr on 05, December, 2018 @ 12:44 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "form_answers", indices = @Index("farmerCode"))
public class FormAnswerData extends BaseModel {
    @SerializedName("data")
    private String data;

    @SerializedName("form_translation_id")
    private int formId;

    @SerializedName("farmer_code")
    private String farmerCode;
    /**
     * No args constructor for use in serialization
     */
    public FormAnswerData() {
    }

     public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFarmerCode() {
        return farmerCode;
    }

    public void setFarmerCode(String farmerCode) {
        this.farmerCode = farmerCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Ignore
    public JSONObject getJsonData() {
        try {
            return new JSONObject(data);
        } catch (JSONException ignored) {
            return new JSONObject();
        }
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

}