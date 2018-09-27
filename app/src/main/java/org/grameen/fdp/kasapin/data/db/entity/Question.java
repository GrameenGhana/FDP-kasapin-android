package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;


/**
 * Created by aangjnr on 29/11/2017.
 */

@Entity(tableName = "questions", indices = {@Index(value = "formId")}, foreignKeys = {@ForeignKey(entity = Form.class, parentColumns = "id", childColumns = "formId", onDelete = CASCADE)})
public class Question {

    @PrimaryKey
    @NonNull
    @SerializedName("Id")
    String id;

    @SerializedName("LastModifiedDate")
    String lastModifiedDate;


    @SerializedName("Name")
    String name;

    @SerializedName("Caption_c")
    String caption;

    @SerializedName("Default_value_c")
    String defaultValue;

    @SerializedName("Display_Order_c")
    Double displayOrder = 1.00;

    @SerializedName("Error_text_c")
    String errorText;

    @SerializedName("Help_Text_c")
    String helpText;

    @SerializedName("Hide_c")
    Boolean shouldHide;

    @SerializedName("Max_value_c")
    String maxValue;

    @SerializedName("Min_value_c")
    String minValue;

    @SerializedName("Options_c")
    String options;

    @SerializedName("Type__c")
    String type;

    @SerializedName("Related_questions_c")
    String relatedQuestions;

    @SerializedName("Form__r")
    String formId;


    @SerializedName("Translation_c")
    String translation;


    @Ignore
    Form form;

    public Question() {
    }


    public Form getForm() {
        return form;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setDisplayOrder(Double displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }


    public void setFormId(String formId) {
        this.formId = formId;
    }

    public void setShouldHide(Boolean shouldHide) {
        this.shouldHide = shouldHide;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRelatedQuestions(String relatedQuestions) {
        this.relatedQuestions = relatedQuestions;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getErrorText() {
        return errorText;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public Boolean getShouldHide() {
        return shouldHide;
    }

    public Double getDisplayOrder() {
        return displayOrder;
    }

    public String getCaption() {
        return caption;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getFormId() {
        return formId;
    }

    public String getHelpText() {
        return helpText;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public String getName() {
        return name;
    }

    public String getOptions() {
        return options;
    }

    public String getRelatedQuestions() {
        return relatedQuestions;
    }

    public String getTranslation() {
        return translation;
    }

    public String getType() {
        return type;
    }



    @Ignore
    public List<String> formatQuestionOptions() {

        String s = "";
        s = getOptions();
        Log.i("QUESTION MODEL", "OPTIONS = " + s);

        if (!s.equalsIgnoreCase("null"))
            try {
                return Arrays.asList(s.trim().split(","));
            } catch (Exception e) {
                e.printStackTrace();
                return Arrays.asList(s);
            }

        else return new ArrayList<>();

    }

}
