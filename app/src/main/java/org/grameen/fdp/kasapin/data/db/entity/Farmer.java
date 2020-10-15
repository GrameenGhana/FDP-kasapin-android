package org.grameen.fdp.kasapin.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Objects;

@Entity(tableName = "farmers", indices = {@Index(value = "code", unique = true), @Index(value = "villageId"), @Index(value = "syncStatus"), @Index(value = "gender"),})
public class Farmer {
    @PrimaryKey()
    @NonNull
    @SerializedName("farmer_code_c")
    String code;

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("full_name_c")
    String farmerName;

    @SerializedName("gender_c")
    String gender;

    @SerializedName("birthday_c")
    String birthYear = "1970";

    @SerializedName("educational_level_c")
    String educationLevel;

    @SerializedName("farmer_photo_c")
    String imageUrl = null;

    @SerializedName("country_admin_level_id")
    int villageId;

    Date firstVisitDate;
    Date lastVisitDate;
    int syncStatus = 1;

    @SerializedName("LastModifiedDate")
    Date lastModifiedDate;

    String landArea;

    String hasSubmitted = "NO";

    String villageName;

    @Ignore
    String externalId;


    public Farmer() {}

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Date getFirstVisitDate() {
        return firstVisitDate;
    }

    public void setFirstVisitDate(Date firstVisitDate) {
        this.firstVisitDate = firstVisitDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Farmer)) return false;
        Farmer farmer = (Farmer) o;
        return code.equals(farmer.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    public Date getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(Date lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getHasSubmitted() {
        return hasSubmitted;
    }

    public void setHasSubmitted(String hasSubmitted) {
        this.hasSubmitted = hasSubmitted;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    @NonNull
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

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    @Ignore
    public boolean hasAgreed() {
        return hasSubmitted.equalsIgnoreCase("YES");
    }

    @Ignore
    public boolean isSynced() {
        return syncStatus == 1;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
