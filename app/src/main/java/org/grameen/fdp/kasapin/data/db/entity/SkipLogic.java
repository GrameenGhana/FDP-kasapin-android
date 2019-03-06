package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by aangjnr on 02/01/2018.
 */

@Entity(tableName = "skip_logics",  indices = {@Index(value = "questionId")}, foreignKeys = {@ForeignKey(entity = Question.class, parentColumns = "id", childColumns = "questionId", deferred = true)})
public class SkipLogic {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    int id;

    @SerializedName("question_id")
    int questionId;

    @SerializedName("updated_at")
    String dateUpdated;

    @SerializedName("created_at")
    String dateCreated;

    @SerializedName("formula_c")
    String formula;

    @SerializedName("hide_c")
    int shouldHide;


    @Ignore
    String comparingQuestion;
    @Ignore
    String logicalOperator;
    @Ignore
    String answerValue;


    public SkipLogic() {
    }


    public void setId(@NonNull int id) {
        this.id = id;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setShouldHide(int shouldHide) {
        this.shouldHide = shouldHide;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getShouldHide() {
        return shouldHide;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public String getFormula() {
        return formula;
    }


    public boolean shouldHide(){
        return shouldHide == 1;
    }


    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }



    public String getAnswerValue() {
        return answerValue;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }


    public void setComparingQuestion(String comparingQuestion) {
        this.comparingQuestion = comparingQuestion;
    }

    public String getComparingQuestion() {
        return comparingQuestion;
    }
}
