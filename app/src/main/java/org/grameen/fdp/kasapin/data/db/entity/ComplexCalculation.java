package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by aangjnr on 15/02/2018.
 */

@Entity(tableName = "complex_calculations", indices = @Index("questionId"), foreignKeys = @ForeignKey(entity = Question.class, parentColumns = "id", childColumns = "questionId", onDelete = CASCADE))
public class ComplexCalculation {

    @PrimaryKey
    @NonNull
    String id;

    @SerializedName("LastModifiedDate")
    String lastModifiedDate;
    String name;
    @SerializedName("Question__c")
    String questionId;
    @SerializedName("Condition__c")
    String condition;


    public ComplexCalculation() {
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
