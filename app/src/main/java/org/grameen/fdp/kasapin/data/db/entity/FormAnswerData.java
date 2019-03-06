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

import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.model.QuestionsAndSkipLogic;

import java.util.Date;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "form_answers", indices = @Index("farmerCode"))
public class FormAnswerData {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    @SerializedName("data")
    String data;


    @SerializedName("form_id")
    int formId;

    @SerializedName("farmer_code")
    String farmerCode;


    @SerializedName("created_at")
    Date dateCreated = new Date(System.currentTimeMillis());


    @SerializedName("updated_at")
    Date dateUpdated;




    /**
     * No args constructor for use in serialization
     */
    public FormAnswerData() {
    }


    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setData(String data) {
        this.data = data;
    }


    public void setFarmerCode(String farmerCode) {
        this.farmerCode = farmerCode;
    }


    public String getFarmerCode() {
        return farmerCode;
    }

    public String getData() {
        return data;
    }


    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getFormId() {
        return formId;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }
}