package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;


/**
 * Created by aangjnr on 29/11/2017.
 */

//, foreignKeys = {@ForeignKey(entity = FormTranslation.class, parentColumns = "id", childColumns = "formTranslationId", deferred = true)}
@Entity(tableName = "questions", indices = {@Index(value = "formTranslationId"), @Index(value = "id", unique = true), @Index(value = "labelC", unique = true)})
public class Question {


    @PrimaryKey(autoGenerate = true)
    @NonNull
    int base_id;
    @Ignore
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
    private String formulaC;
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


    /**
     * No args constructor for use in serialization
     */
    public Question() {
    }

    /**
     * @param defaultValueC
     * @param id
     * @param captionC
     * @param createTime
     * @param helpTextC
     * @param hideC
     * @param labelC
     * @param formTranslationId
     * @param updateTime
     * @param formulaC
     * @param displayOrderC
     * @param requiredC
     * @param canEdit
     * @param optionsC
     * @param typeC
     */

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

    @NonNull
    public int getBase_id() {
        return base_id;
    }

    public void setBase_id(@NonNull int base_id) {
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

    public int getFormTranslationId() {
        return formTranslationId;
    }

    public void setFormTranslationId(int formTranslationId) {
        this.formTranslationId = formTranslationId;
    }

    public Question withFormTranslationId(int formTranslationId) {
        this.formTranslationId = formTranslationId;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Question withCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Question withUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getCaptionC() {
        return captionC;
    }

    public void setCaptionC(String captionC) {
        this.captionC = captionC;
    }

    public Question withCaptionC(String captionC) {
        this.captionC = captionC;
        return this;
    }

    public String getTypeC() {
        return typeC;
    }

    public void setTypeC(String typeC) {
        this.typeC = typeC;
    }

    public Question withTypeC(String typeC) {
        this.typeC = typeC;
        return this;
    }

    public int getRequiredC() {
        return requiredC;
    }

    public void setRequiredC(int requiredC) {
        this.requiredC = requiredC;
    }

    public Question withRequiredC(int requiredC) {
        this.requiredC = requiredC;
        return this;
    }

    public String getFormulaC() {
        return formulaC;
    }

    public void setFormulaC(String formulaC) {
        this.formulaC = formulaC;
    }

    public Question withFormulaC(String formulaC) {
        this.formulaC = formulaC;
        return this;
    }

    public String getLabelC() {
        return labelC;
    }

    public void setLabelC(String labelC) {
        this.labelC = labelC;
    }

    public Question withLabelC(String labelC) {
        this.labelC = labelC;
        return this;
    }

    public String getDefaultValueC() {
        return (defaultValueC != null) ? defaultValueC : "";
    }

    public void setDefaultValueC(String defaultValueC) {
        this.defaultValueC = defaultValueC;
    }

    public Question withDefaultValueC(String defaultValueC) {
        this.defaultValueC = defaultValueC;
        return this;
    }

    public int getDisplayOrderC() {
        return displayOrderC;
    }

    public void setDisplayOrderC(int displayOrderC) {
        this.displayOrderC = displayOrderC;
    }

    public Question withDisplayOrderC(int displayOrderC) {
        this.displayOrderC = displayOrderC;
        return this;
    }

    public String getHelpTextC() {
        return helpTextC;
    }

    public void setHelpTextC(String helpTextC) {
        this.helpTextC = helpTextC;
    }

    public Question withHelpTextC(String helpTextC) {
        this.helpTextC = helpTextC;
        return this;
    }

    public int getHideC() {
        return hideC;
    }

    public void setHideC(int hideC) {
        this.hideC = hideC;
    }

    public Question withHideC(int hideC) {
        this.hideC = hideC;
        return this;
    }

    public String getOptionsC() {
        return optionsC;
    }

    public void setOptionsC(String optionsC) {
        this.optionsC = optionsC;
    }

    public Question withOptionsC(String optionsC) {
        this.optionsC = optionsC;
        return this;
    }

    public int getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(int canEdit) {
        this.canEdit = canEdit;
    }

    public Question withCanEdit(int canEdit) {
        this.canEdit = canEdit;
        return this;
    }

    public boolean shouldHide() {
        return getHideC() == 1;
    }


    public boolean caEdit() {
        return canEdit == 1;
    }


    @Ignore
    public List<String> formatQuestionOptions() {

        String s = "";
        s = getOptionsC();
        Timber.i("OPTIONS = %s", s);

        if (!s.equalsIgnoreCase("null"))
            try {
                return Arrays.asList(s.trim().split(","));
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.singletonList(s);
            }

        else return new ArrayList<>();


    }

    public List<SkipLogic> getSkipLogics() {
        return skipLogics;
    }

    public void setSkipLogics(List<SkipLogic> skipLogics) {
        this.skipLogics = skipLogics;
    }
}
