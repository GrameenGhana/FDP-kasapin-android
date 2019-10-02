package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import org.grameen.fdp.kasapin.utilities.TimeUtils;


/**
 * Created by aangjnr on 24/03/2018.
 */

@Entity(tableName = "submission_table")
public class Submission {
    @PrimaryKey
    @NonNull
    String Id;

    String End__c;
    String External_id__c;
    String Respondent__c;
    String Start__c = TimeUtils.getCurrentDateTime();
    int Surveyor__c;


    public Submission() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getStart__c() {
        return Start__c;
    }

    public void setStart__c(String start__c) {
        Start__c = start__c;
    }

    public int getSurveyor__c() {
        return Surveyor__c;
    }

    public void setSurveyor__c(int surveyor__c) {
        Surveyor__c = surveyor__c;
    }

    public String getEnd__c() {
        return End__c;
    }

    public void setEnd__c(String end__c) {
        End__c = end__c;
    }

    public String getExternal_id__c() {
        return External_id__c;
    }

    public void setExternal_id__c(String external_id__c) {
        External_id__c = external_id__c;
    }

    public String getRespondent__c() {
        return Respondent__c;
    }

    public void setRespondent__c(String respondent__c) {
        Respondent__c = respondent__c;
    }


}
