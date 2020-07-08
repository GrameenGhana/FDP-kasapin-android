package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "mappings", indices = {@Index(value = "id", unique = true), @Index(value = "questionId", unique = true)})
public class Mapping extends BaseModel {
    @SerializedName("question_id")
    private int questionId;
    @SerializedName("object_c")
    private String objectName;
    @SerializedName("field_c")
    private String fieldName;

    /**
     * No args constructor for use in serialization
     */
    public Mapping() {
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}