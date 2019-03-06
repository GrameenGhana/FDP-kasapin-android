package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by aangjnr on 30/01/2018.
 */

@Entity(tableName = "villages", indices = @Index(value = "countryId"))
public class Village {

    @PrimaryKey
    @NonNull
    int id;

    @SerializedName("LastModifiedDate")
    String lastModifiedDate;

     @NonNull
    String name;
    int countryId;

    @SerializedName("district__c")
    String district;


    public Village() {
    }


    public void setId(@NonNull int id) {
        this.id = id;
    }


    public int getCountryId() {
        return countryId;
    }

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

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }


}
