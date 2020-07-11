package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "skip_logics", indices = {@Index(value = "questionId")})
public class SkipLogic {
    @PrimaryKey
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getShouldHide() {
        return shouldHide;
    }

    public void setShouldHide(int shouldHide) {
        this.shouldHide = shouldHide;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public boolean shouldHide() {
        return shouldHide == 1;
    }

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public String getComparingQuestion() {
        return comparingQuestion;
    }

    public void setComparingQuestion(String comparingQuestion) {
        this.comparingQuestion = comparingQuestion;
    }
}
