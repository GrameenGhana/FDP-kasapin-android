package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "communities", indices = {@Index(value = "countryId"), @Index(value = "parentId"), @Index(value = "id", unique = true)})
public class Community extends BaseModel {
    @SerializedName("name")
    String name;
    @SerializedName("country_id")
    private
    int countryId;
    @SerializedName("type")
    private
    String type;
    @SerializedName("parent_id")
    private
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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
