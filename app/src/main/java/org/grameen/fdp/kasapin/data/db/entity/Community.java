package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aangjnr on 30/01/2018.
 */

@Entity(tableName = "communities", indices = {@Index(value = "countryId"), @Index(value = "parentId"), @Index(value = "id", unique = true)})
public class Community extends BaseModel{


    @SerializedName("country_id")
    int countryId;

    @SerializedName("name")
    String name;

    @SerializedName("type")
    String type;


    @SerializedName("parent_id")
    int parentId;



    public Community() {
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getParentId() {
        return parentId;
    }

    public String getType() {
        return type;
    }
}
