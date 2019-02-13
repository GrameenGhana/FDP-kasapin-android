package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.sql.Date;


/**
 * Created by aangjnr on 24/03/2018.
 */

@Entity(tableName = "submission_table")
public class Submission {
    @PrimaryKey
    @NonNull
    String Id;

    Date End__c;
    String External_id__c;
    String Respondent__c;

    Date Start__c;
    String Surveyor__c;


    public Submission() {
    }

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public void setEnd__c(Date end__c) {
        End__c = end__c;
    }

    public void setExternal_id__c(String external_id__c) {
        External_id__c = external_id__c;
    }

    public void setRespondent__c(String respondent__c) {
        Respondent__c = respondent__c;
    }

    public void setStart__c(Date start__c) {
        Start__c = start__c;
    }

    public void setSurveyor__c(String surveyor__c) {
        Surveyor__c = surveyor__c;
    }


    public Date getEnd__c() {
        return End__c;
    }

    public Date getStart__c() {
        return Start__c;
    }

    public String getExternal_id__c() {
        return External_id__c;
    }

    public String getRespondent__c() {
        return Respondent__c;
    }

    public String getSurveyor__c() {
        return Surveyor__c;
    }


}
