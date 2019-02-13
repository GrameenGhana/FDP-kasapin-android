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

@Entity(tableName = "form_answers", indices = {@Index(value = "formId", unique = true), @Index(value = "farmerCode", unique = true)},
        foreignKeys = {@ForeignKey(entity = Form.class, parentColumns = "id", childColumns = "formId", onDelete = CASCADE),
        @ForeignKey(entity = RealFarmer.class, parentColumns = "code", childColumns = "farmerCode", onDelete = CASCADE)})
public class FormAnswerData {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int base_id;

    @SerializedName("id")
    private int id;

    @SerializedName("data")
    private String data;


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