package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "questions", indices = {@Index(value = "formTranslationId"), @Index(value = "id", unique = true), @Index(value = "labelC", unique = true)})
public class Question {
    @PrimaryKey(autoGenerate = true)
    private
    int base_id;
    @Ignore
    private
    List<SkipLogic> skipLogics;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("form_translation_id")
    @Expose
    private int formTranslationId;
    @SerializedName("created_at")
    @Expose
    private String createTime;
    @SerializedName("updated_at")
    @Expose
    private String updateTime;
    @SerializedName("caption_c")
    @Expose
    private String captionC;
    @SerializedName("type_c")
    @Expose
    private String typeC;
    @SerializedName("required_c")
    @Expose
    private int requiredC;
    @SerializedName("formula_c")
    @Expose
    private String formulaC = "";
    @SerializedName("label_c")
    @Expose
    private String labelC;

    @SerializedName("default_value_c")
    @Expose
    private String defaultValueC;
    @SerializedName("display_order_c")
    @Expose
    private int displayOrderC;
    @SerializedName("help_text_c")
    @Expose
    private String helpTextC;
    @SerializedName("hide_c")
    @Expose
    private int hideC;
    @SerializedName("options_c")
    @Expose
    private String optionsC;
    @SerializedName("can_edit")
    @Expose
    private int canEdit;
    @SerializedName("related_questions_c")
    @Expose
    private String relatedQuestions;
    @SerializedName("min_c")
    @Expose
    private String minValue;
    @SerializedName("max_c")
    @Expose
    private String maxValue;

    @Expose
    @SerializedName("error_display_message_c")
    private String errorMessage;

    @Ignore
    private String validationId = null;


    /**
     * No args constructor for use in serialization
     */
    public Question() {
    }

    @Ignore
    public Question(int id, int formTranslationId, String createTime, String updateTime, String captionC, String typeC, int requiredC, String formulaC, String labelC, String defaultValueC, int displayOrderC, String helpTextC, int hideC, String optionsC, int canEdit) {
        super();
        this.id = id;
        this.formTranslationId = formTranslationId;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.captionC = captionC;
        this.typeC = typeC;
        this.requiredC = requiredC;
        this.formulaC = formulaC;
        this.labelC = labelC;
        this.defaultValueC = defaultValueC;
        this.displayOrderC = displayOrderC;
        this.helpTextC = helpTextC;
        this.hideC = hideC;
        this.optionsC = optionsC;
        this.canEdit = canEdit;
    }

    public int getBase_id() {
        return base_id;
    }

    public void setBase_id(int base_id) {
        this.base_id = base_id;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public Question withId(int id) {
        this.id = id;
        return this;
    }

    public String getValidationId() {
        if (validationId == null)
            return labelC;
        else
            return validationId;
    }

    public void setValidationId(String validationId) {
        this.validationId = validationId;
    }

    public int getFormTranslationId() {
        return formTranslationId;
    }

    public void setFormTranslationId(int formTranslationId) {
        this.formTranslationId = formTranslationId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCaptionC() {
        return captionC;
    }

    public void setCaptionC(String captionC) {
        this.captionC = captionC;
    }

    public String getTypeC() {
        return typeC;
    }

    public void setTypeC(String typeC) {
        this.typeC = typeC;
    }

    public int getRequiredC() {
        return requiredC;
    }

    public void setRequiredC(int requiredC) {
        this.requiredC = requiredC;
    }

    public String getFormulaC() {
        return formulaC;
    }

    public void setFormulaC(String formulaC) {
        this.formulaC = formulaC;
    }

    public String getLabelC() {
        return labelC;
    }

    public void setLabelC(String labelC) {
        this.labelC = labelC;
    }

    public String getDefaultValueC() {
        return (defaultValueC != null) ? defaultValueC : "";
    }

    public void setDefaultValueC(String defaultValueC) {
        this.defaultValueC = defaultValueC;
    }

    public int getDisplayOrderC() {
        return displayOrderC;
    }

    public void setDisplayOrderC(int displayOrderC) {
        this.displayOrderC = displayOrderC;
    }

    public String getHelpTextC() {
        return helpTextC;
    }

    public void setHelpTextC(String helpTextC) {
        this.helpTextC = helpTextC;
    }

    public int getHideC() {
        return hideC;
    }

    public void setHideC(int hideC) {
        this.hideC = hideC;
    }

    public String getOptionsC() {
        return optionsC;
    }

    public void setOptionsC(String optionsC) {
        this.optionsC = optionsC;
    }

    public int getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(int canEdit) {
        this.canEdit = canEdit;
    }

    public boolean shouldHide() {
        return getHideC() == 1;
    }

    public boolean caEdit() {
        return canEdit == 1;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    @Ignore
    public List<String> formatQuestionOptions() {
        String s;
        s = getOptionsC();
        if (s != null &&!s.equalsIgnoreCase("null"))
            try {
                return Arrays.asList(s.trim().split(","));
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.singletonList(s);
            }
        else return new ArrayList<>();
    }

    public String getRelatedQuestions() {
        return relatedQuestions;
    }

    public void setRelatedQuestions(String relatedQuestions) {
        this.relatedQuestions = relatedQuestions;
    }

    public String getErrorMessage() {
        if(errorMessage == null || errorMessage.isEmpty())
            return "This field is required";
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Ignore
    public String[] splitRelatedQuestions() {
        if (relatedQuestions != null)
            return relatedQuestions.split(",");
        else return null;
    }

    public List<SkipLogic> getSkipLogics() {
        return skipLogics;
    }

    public void setSkipLogics(List<SkipLogic> skipLogics) {
        this.skipLogics = skipLogics;
    }

    public boolean isRequired() {
        return requiredC == 1;
    }
}
