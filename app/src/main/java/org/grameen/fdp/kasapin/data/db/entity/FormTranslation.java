package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.model.QuestionsAndSkipLogic;

import java.util.List;

//, foreignKeys = {@ForeignKey(entity = Form.class, parentColumns = "id", childColumns = "formId", deferred = true)}
@Entity(tableName = "form_translation", indices = {@Index(value = "formId"), @Index(value = "id", unique = true)})
public class FormTranslation extends BaseModel {
    @SerializedName("form_id")
    String formId;
    @SerializedName("name")
    private String name;
    @Ignore
    @SerializedName("form")
    private Form form;
    @Ignore
    @SerializedName("questions")
    private List<QuestionsAndSkipLogic> questionsAndSkipLogic;

    /**
     * No args constructor for use in serialization
     */
    public FormTranslation() {
    }

    @Ignore
    public FormTranslation(int id, String name, String formId) {
        super();
        this.id = id;
        this.name = name;
        this.formId = formId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public List<QuestionsAndSkipLogic> getQuestionsAndSkipLogic() {
        return questionsAndSkipLogic;
    }

    public void setQuestionsAndSkipLogic(List<QuestionsAndSkipLogic> questionsAndSkipLogic) {
        this.questionsAndSkipLogic = questionsAndSkipLogic;
    }
}