package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aangjnr on 13/12/2017.
 */

@Entity(tableName = "farmers", indices = {@Index(value = "id", unique = true), @Index(value = "code", unique = true)})
public class RealFarmer{

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    String farmerName;
    String code;
    int villageId;
    String gender;
    String birthYear = "1970";
    String educationLevel;
    String imageUrl;
    Date firstVisitDate;
    Date lastVisitDate;
    @SerializedName("LastModifiedDate")
    Date lastModifiedDate;

    String landArea;

    int syncStatus = 0;
    String hasSubmitted = "NO";

    String villageName;

    @Ignore
    String externalId;

    public RealFarmer(){}


    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setFirstVisitDate(Date firstVisitDate) {
        this.firstVisitDate = firstVisitDate;
    }


    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public void setLastVisitDate(Date lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public Date getFirstVisitDate() {
        return firstVisitDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Date getLastVisitDate() {
        return lastVisitDate;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLandArea() {

        return landArea;
    }

    public void setLandArea(String landArea) {
        this.landArea = landArea;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String education) {
        this.educationLevel = education;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getHasSubmitted() {
        return hasSubmitted;
    }

    public void setHasSubmitted(String hasSubmitted) {
        this.hasSubmitted = hasSubmitted;
    }


    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }



    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public int getVillageId() {
        return villageId;
    }

    public void setVillageId(int villageId) {
        this.villageId = villageId;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getVillageName() {
        return villageName;
    }


    public boolean hasAgreed(){
        return hasSubmitted.equalsIgnoreCase("YES");
    }
}
