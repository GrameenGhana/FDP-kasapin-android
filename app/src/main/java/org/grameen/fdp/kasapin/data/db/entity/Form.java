package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "forms", indices = {@Index(value = "id", unique = true)})
public class Form {
    @SerializedName("translation_id")
    public int translationId;
    @PrimaryKey(autoGenerate = true)
    int base_id;
    @Ignore
    List<Question> questionList;
    @SerializedName("id")
    private int id;
    @SerializedName("hide_c")
    private int hide = 0;
    @SerializedName("create_time")
    private String createTime;
    @SerializedName("update_time")
    private String updateTime;
    @SerializedName("form_name_c")
    private String formNameC;
    @SerializedName("display_order_c")
    private int displayOrderC;
    @SerializedName("type_c")
    private String typeC;
    @SerializedName("display_type_c")
    private String displayTypeC;
    @SerializedName("custom_c")
    private String customC;
    private String translation;

    /**
     * No args constructor for use in serialization
     */
    public Form() {
    }

    @Ignore
    public Form(int id, String createTime, String updateTime, String formNameC, int displayOrderC, String typeC, String displayTypeC, String customC) {
        super();
        this.id = id;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.formNameC = formNameC;
        this.displayOrderC = displayOrderC;
        this.typeC = typeC;
        this.displayTypeC = displayTypeC;
        this.customC = customC;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setTranslationId(int formTranslationId) {
        this.translationId = formTranslationId;
    }

    public int getFormTranslationId() {
        return translationId;
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

    public String getFormNameC() {
        return formNameC;
    }

    public void setFormNameC(String formNameC) {
        this.formNameC = formNameC;
    }

    public int getDisplayOrderC() {
        return displayOrderC;
    }

    public void setDisplayOrderC(int displayOrderC) {
        this.displayOrderC = displayOrderC;
    }

    public String getTypeC() {
        return typeC;
    }

    public void setTypeC(String typeC) {
        this.typeC = typeC;
    }

    public String getDisplayTypeC() {
        return displayTypeC;
    }

    public void setDisplayTypeC(String displayTypeC) {
        this.displayTypeC = displayTypeC;
    }

    public String getCustomC() {
        return customC;
    }

    public void setCustomC(String customC) {
        this.customC = customC;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    public int getHide() {
        return hide;
    }

    public void setHide(int hide) {
        this.hide = hide;
    }

    @Ignore
    public boolean shouldHide() {
        return hide > 0;
    }
}