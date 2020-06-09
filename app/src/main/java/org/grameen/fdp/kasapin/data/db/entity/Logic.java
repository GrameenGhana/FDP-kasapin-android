package org.grameen.fdp.kasapin.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aangjnr on 02/01/2018.
 */


@Entity(tableName = "logics")
public class Logic {

    @PrimaryKey
    @NonNull
    @SerializedName("Id")
    String id;

    @SerializedName("LastModifiedDate")
    String lastModifiedDate;


    @SerializedName("Name")
    String name;


    @SerializedName("Parent_logic__c")
    String parentLogic = "";

    @SerializedName("Parent_logical_operator__c")
    String parentLogicalOperator = "";


    @SerializedName("Result__c")
    String result = "";

    @SerializedName("Result_questions__c")
    String resultQuestions = "";


    @SerializedName("Logical_operator_1__c")
    String logicalOperator1 = "";

    @SerializedName("Logical_operator_2__c")
    String logicalOperator2 = "";

    @SerializedName("Logical_operator_3__c")
    String logicalOperator3 = "";

    @SerializedName("Logical_operator_4__c")
    String logicalOperator4 = "";

    @SerializedName("Logical_operator_5__c")
    String logicalOperator5 = "";

    @SerializedName("Logical_operator_6__c")
    String logicalOperator6 = "";

    @SerializedName("Logical_operator_7__c")
    String logicalOperator7 = "";

    @SerializedName("Logical_operator_8__c")
    String logicalOperator8 = "";

    @SerializedName("Logical_operator_9__c")
    String logicalOperator9 = "";

    @SerializedName("Logical_operator_10__c")
    String logicalOperator10 = "";


    @SerializedName("Question_1__c")
    String question1 = "";

    @SerializedName("Question_2__c")
    String question2 = "";

    @SerializedName("Question_3__c")
    String question3 = "";

    @SerializedName("Question_4__c")
    String question4 = "";

    @SerializedName("Question_5__c")
    String question5 = "";

    @SerializedName("Question_6__c")
    String question6 = "";

    @SerializedName("Question_7__c")
    String question7 = "";

    @SerializedName("Question_8__c")
    String question8 = "";

    @SerializedName("Question_9__c")
    String question9 = "";

    @SerializedName("Question_10__c")
    String question10 = "";


    @SerializedName("Question_logic_operation_1__c")
    String questionLogicOperation1 = "";

    @SerializedName("Question_logic_operation_2__c")
    String questionLogicOperation2 = "";

    @SerializedName("Question_logic_operation_3__c")
    String questionLogicOperation3 = "";

    @SerializedName("Question_logic_operation_4__c")
    String questionLogicOperation4 = "";

    @SerializedName("Question_logic_operation_5__c")
    String questionLogicOperation5 = "";

    @SerializedName("Question_logic_operation_6__c")
    String questionLogicOperation6 = "";

    @SerializedName("Question_logic_operation_7__c")
    String questionLogicOperation7 = "";

    @SerializedName("Question_logic_operation_8__c")
    String questionLogicOperation8 = "";

    @SerializedName("Question_logic_operation_9__c")
    String questionLogicOperation9 = "";

    @SerializedName("Question_logic_operation_10__c")
    String questionLogicOperation10 = "";


    @SerializedName("Value_1__c")
    String value1 = "";

    @SerializedName("Value_2__c")
    String value2 = "";

    @SerializedName("Value_3__c")
    String value3 = "";

    @SerializedName("Value_4__c")
    String value4 = "";

    @SerializedName("Value_5__c")
    String value5 = "";

    @SerializedName("Value_6__c")
    String value6 = "";

    @SerializedName("Value_7__c")
    String value7 = "";

    @SerializedName("Value_8__c")
    String value8 = "";

    @SerializedName("Value_9__c")
    String value9 = "";

    @SerializedName("Value_10__c")
    String value10 = "";


    String evaluatedValue;

    @Ignore
    String QUESTION;
    @Ignore
    String VALUE;
    @Ignore
    String LOGICAL_OPERATOR;

    public Logic() {
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getEvaluatedValue() {
        return evaluatedValue;
    }

    public void setEvaluatedValue(String evaluatedValue) {
        this.evaluatedValue = evaluatedValue;
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

    public String getQuestion4() {
        return question4;
    }

    public void setQuestion4(String question4) {
        this.question4 = question4;
    }

    public String getQuestion3() {
        return question3;
    }

    public void setQuestion3(String question3) {
        this.question3 = question3;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getQuestion5() {
        return question5;
    }

    public void setQuestion5(String question5) {
        this.question5 = question5;
    }

    public String getLogicalOperator1() {
        return logicalOperator1;
    }

    public void setLogicalOperator1(String logicalOperator1) {
        this.logicalOperator1 = logicalOperator1;
    }

    public String getLogicalOperator2() {
        return logicalOperator2;
    }

    public void setLogicalOperator2(String logicalOperator2) {
        this.logicalOperator2 = logicalOperator2;
    }

    public String getLogicalOperator3() {
        return logicalOperator3;
    }

    public void setLogicalOperator3(String logicalOperator3) {
        this.logicalOperator3 = logicalOperator3;
    }

    public String getLogicalOperator4() {
        return logicalOperator4;
    }

    public void setLogicalOperator4(String logicalOperator4) {
        this.logicalOperator4 = logicalOperator4;
    }

    public String getLogicalOperator5() {
        return logicalOperator5;
    }

    public void setLogicalOperator5(String logicalOperator5) {
        this.logicalOperator5 = logicalOperator5;
    }

    public String getLogicalOperator6() {
        return logicalOperator6;
    }

    public void setLogicalOperator6(String logicalOperator6) {
        this.logicalOperator6 = logicalOperator6;
    }

    public String getLogicalOperator7() {
        return logicalOperator7;
    }

    public void setLogicalOperator7(String logicalOperator7) {
        this.logicalOperator7 = logicalOperator7;
    }

    public String getLogicalOperator8() {
        return logicalOperator8;
    }

    public void setLogicalOperator8(String logicalOperator8) {
        this.logicalOperator8 = logicalOperator8;
    }

    public String getLogicalOperator9() {
        return logicalOperator9;
    }

    public void setLogicalOperator9(String logicalOperator9) {
        this.logicalOperator9 = logicalOperator9;
    }

    public String getLogicalOperator10() {
        return logicalOperator10;
    }

    public void setLogicalOperator10(String logicalOperator10) {
        this.logicalOperator10 = logicalOperator10;
    }

    public String getParentLogic() {
        return parentLogic;
    }

    public void setParentLogic(String parentLogic) {
        this.parentLogic = parentLogic;
    }

    public String getParentLogicalOperator() {
        return parentLogicalOperator;
    }

    public void setParentLogicalOperator(String parentLogicalOperator) {
        this.parentLogicalOperator = parentLogicalOperator;
    }

    public String getQuestion6() {
        return question6;
    }

    public void setQuestion6(String question6) {
        this.question6 = question6;
    }

    public String getQuestion7() {
        return question7;
    }

    public void setQuestion7(String question7) {
        this.question7 = question7;
    }

    public String getQuestion8() {
        return question8;
    }

    public void setQuestion8(String question8) {
        this.question8 = question8;
    }

    public String getQuestion9() {
        return question9;
    }

    public void setQuestion9(String question9) {
        this.question9 = question9;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultQuestions() {
        return resultQuestions;
    }

    public void setResultQuestions(String resultQuestions) {
        this.resultQuestions = resultQuestions;
    }

    public String getQuestion10() {
        return question10;
    }

    public void setQuestion10(String question10) {
        this.question10 = question10;
    }

    public String getQuestionLogicOperation1() {
        return questionLogicOperation1;
    }

    public void setQuestionLogicOperation1(String questionLogicOperation1) {
        this.questionLogicOperation1 = questionLogicOperation1;
    }

    public String getQuestionLogicOperation2() {
        return questionLogicOperation2;
    }

    public void setQuestionLogicOperation2(String questionLogicOperation2) {
        this.questionLogicOperation2 = questionLogicOperation2;
    }

    public String getQuestionLogicOperation3() {
        return questionLogicOperation3;
    }

    public void setQuestionLogicOperation3(String questionLogicOperation3) {
        this.questionLogicOperation3 = questionLogicOperation3;
    }

    public String getQuestionLogicOperation4() {
        return questionLogicOperation4;
    }

    public void setQuestionLogicOperation4(String questionLogicOperation4) {
        this.questionLogicOperation4 = questionLogicOperation4;
    }

    public String getQuestionLogicOperation5() {
        return questionLogicOperation5;
    }

    public void setQuestionLogicOperation5(String questionLogicOperation5) {
        this.questionLogicOperation5 = questionLogicOperation5;
    }

    public String getQuestionLogicOperation6() {
        return questionLogicOperation6;
    }

    public void setQuestionLogicOperation6(String questionLogicOperation6) {
        this.questionLogicOperation6 = questionLogicOperation6;
    }

    public String getQuestionLogicOperation7() {
        return questionLogicOperation7;
    }

    public void setQuestionLogicOperation7(String questionLogicOperation7) {
        this.questionLogicOperation7 = questionLogicOperation7;
    }

    public String getQuestionLogicOperation8() {
        return questionLogicOperation8;
    }

    public void setQuestionLogicOperation8(String questionLogicOperation8) {
        this.questionLogicOperation8 = questionLogicOperation8;
    }

    public String getQuestionLogicOperation9() {
        return questionLogicOperation9;
    }

    public void setQuestionLogicOperation9(String questionLogicOperation9) {
        this.questionLogicOperation9 = questionLogicOperation9;
    }

    public String getQuestionLogicOperation10() {
        return questionLogicOperation10;
    }

    public void setQuestionLogicOperation10(String questionLogicOperation10) {
        this.questionLogicOperation10 = questionLogicOperation10;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        this.value5 = value5;
    }

    public String getValue6() {
        return value6;
    }

    public void setValue6(String value6) {
        this.value6 = value6;
    }

    public String getValue7() {
        return value7;
    }

    public void setValue7(String value7) {
        this.value7 = value7;
    }

    public String getValue8() {
        return value8;
    }

    public void setValue8(String value8) {
        this.value8 = value8;
    }

    public String getValue9() {
        return value9;
    }

    public void setValue9(String value9) {
        this.value9 = value9;
    }

    public String getValue10() {
        return value10;
    }

    public void setValue10(String value10) {
        this.value10 = value10;
    }

    @Ignore
    public String getLOGICAL_OPERATOR() {
        return LOGICAL_OPERATOR;
    }

    @Ignore
    public void setLOGICAL_OPERATOR(String LOGICAL_OPERATOR) {
        this.LOGICAL_OPERATOR = LOGICAL_OPERATOR;
    }

    @Ignore
    public String getQUESTION() {
        return QUESTION;
    }

    @Ignore
    public void setQUESTION(String QUESTION) {
        this.QUESTION = QUESTION;
    }

    @Ignore
    public String getVALUE() {
        return VALUE;
    }

    @Ignore
    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
    }


}
