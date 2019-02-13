
package org.grameen.fdp.kasapin.data.network.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;

import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.Question;

public class FormTranslation {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("form_id")
    private int formId;
    @SerializedName("form")

    private Form form;
    @SerializedName("question")
    private List<Question> question = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public FormTranslation() {
    }

    /**
     * 
     * @param id
     * @param form
     * @param formId
     * @param name
     * @param question
     */
    public FormTranslation(int id, String name, int formId, Form form, List<Question> question) {
        super();
        this.id = id;
        this.name = name;
        this.formId = formId;
        this.form = form;
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FormTranslation withId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormTranslation withName(String name) {
        this.name = name;
        return this;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public FormTranslation withFormId(int formId) {
        this.formId = formId;
        return this;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public FormTranslation withForm(Form form) {
        this.form = form;
        return this;
    }

    public List<Question> getQuestion() {
        return question;
    }

    public void setQuestion(List<Question> question) {
        this.question = question;
    }

    public FormTranslation withQuestion(List<Question> question) {
        this.question = question;
        return this;
    }

}
