package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aangjnr on 08/01/2018.
 */

@Entity(tableName = "recommendations")
public class Recommendation {

    @PrimaryKey
    @NonNull
    @SerializedName("Id")
    String id = "";

    @SerializedName("Translation__c")
    String translation;

    @SerializedName("LastModifiedDate")
    String lastModifiedDate;

    @SerializedName("Name")
    String name = "";

    @SerializedName("Condition__c")
    String condition = "";

    @SerializedName("description__c")
    String description = "";

    @SerializedName("Hierarchy__c")
    String hierarchy = "";

    @SerializedName("Cost_Year_0__c")
    String cost0 = "";

    @SerializedName("Cost_questions__c")
    String costQuestions = "";


    @SerializedName("Income_Year_0__c")
    String income0 = "";

    @SerializedName("Income_Year_1__c")
    String income1 = "";

    @SerializedName("Income_Year_2__c")
    String income2 = "";

    @SerializedName("Income_Year_3__c")
    String income3 = "";

    @SerializedName("Income_Year_4__c")
    String income4 = "";

    @SerializedName("Income_Year_5__c")
    String income5 = "";

    @SerializedName("Income_Year_6__c")
    String income6 = "";

    @SerializedName("Income_Year_7__c")
    String income7 = "";

    @SerializedName("Logic__c")
    String logicId = "";

    @SerializedName("Related_1__c")
    String relatedOne = "";

    @SerializedName("Related_2__c")
    String relatedTwo = "";

    @SerializedName("Year_back_to_Gaps__c")
    String yearBackToGAPs = "";


    @SerializedName("Income_questions__c")
    String questionsInvolved = "";

    public Recommendation(){}

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public String getRelatedOne() {
        return relatedOne;
    }

    public void setRelatedOne(String relatedOne) {
        this.relatedOne = relatedOne;
    }

    public String getRelatedTwo() {
        return relatedTwo;
    }

    public void setRelatedTwo(String relatedTwo) {
        this.relatedTwo = relatedTwo;
    }

    public String getYearBackToGAPs() {
        return yearBackToGAPs;
    }

    public void setYearBackToGAPs(String yearBackToGAPs) {
        this.yearBackToGAPs = yearBackToGAPs;
    }

    public String getLogicId() {
        return logicId;
    }

    public void setLogicId(String logicId) {
        this.logicId = logicId;
    }

    public String getIncome0() {
        return income0;
    }

    public void setIncome0(String income0) {
        this.income0 = income0;
    }

    public String getIncome1() {
        return income1;
    }

    public void setIncome1(String income1) {
        this.income1 = income1;
    }

    public String getIncome2() {
        return income2;
    }

    public void setIncome2(String income2) {
        this.income2 = income2;
    }

    public String getIncome3() {
        return income3;
    }

    public void setIncome3(String income3) {
        this.income3 = income3;
    }

    public String getIncome4() {
        return income4;
    }

    public void setIncome4(String income4) {
        this.income4 = income4;
    }

    public String getIncome5() {
        return income5;
    }

    public void setIncome5(String income5) {
        this.income5 = income5;
    }

    public String getIncome6() {
        return income6;
    }

    public void setIncome6(String income6) {
        this.income6 = income6;
    }

    public String getIncome7() {
        return income7;
    }

    public void setIncome7(String income7) {
        this.income7 = income7;
    }

    public String getQuestionsInvolved() {
        return questionsInvolved;
    }

    public void setQuestionsInvolved(String questionsInvolved) {
        this.questionsInvolved = questionsInvolved;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setCost0(String cost0) {
        this.cost0 = cost0;
    }


    public void setCostQuestions(String costQuestions) {
        this.costQuestions = costQuestions;
    }

    public String getCost0() {
        return cost0;
    }

    public String getCostQuestions() {
        return costQuestions;
    }
}
