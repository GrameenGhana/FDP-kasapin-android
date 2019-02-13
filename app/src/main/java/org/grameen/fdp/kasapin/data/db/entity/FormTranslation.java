package org.grameen.fdp.kasapin.data.db.entity;


/**
 * Created by AangJnr on 05, December, 2018 @ 12:44 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;

import org.grameen.fdp.kasapin.data.db.model.QuestionsAndSkipLogic;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "form_translation", indices = {@Index(value = "formId"), @Index(value = "id", unique = true)}, foreignKeys = {@ForeignKey(entity = Form.class, parentColumns = "id", childColumns = "formId", onDelete = CASCADE)})
public class FormTranslation {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int base_id;


    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("form_id")
    String formId;

    @Ignore
    @SerializedName("form")
    private Form form;

    @Ignore
    @SerializedName("questions")
    private List<QuestionsAndSkipLogic> questionsAndSkipLogics = null;

    /**
     * No args constructor for use in serialization
     */
    public FormTranslation() {
    }

    /**
     * @param id
     * @param name
     * @param formId
     */

    @Ignore
    public FormTranslation(int id, String name, String formId) {
        super();
        this.id = id;
        this.name = name;
        this.formId = formId;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormId() {
        return formId;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Form getForm() {
        return form;
    }


    public List<QuestionsAndSkipLogic> getQuestionsAndSkipLogics() {
        return questionsAndSkipLogics;
    }

    public void setQuestionsAndSkipLogics(List<QuestionsAndSkipLogic> questionsAndSkipLogics) {
        this.questionsAndSkipLogics = questionsAndSkipLogics;
    }
}