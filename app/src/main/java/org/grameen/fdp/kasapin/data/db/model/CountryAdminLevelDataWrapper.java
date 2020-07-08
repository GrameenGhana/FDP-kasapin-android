package org.grameen.fdp.kasapin.data.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.entity.District;

import java.util.List;

public class CountryAdminLevelDataWrapper {
    @SerializedName("data")
    @Expose
    private List<District> data;

    /**
     * No args constructor for use in serialization
     */
    public CountryAdminLevelDataWrapper() {
    }
    public CountryAdminLevelDataWrapper(List<District> data) {
        super();
        this.data = data;
    }

    public List<District> getData() {
        return data;
    }

    public void setData(List<District> data) {
        this.data = data;
    }
}