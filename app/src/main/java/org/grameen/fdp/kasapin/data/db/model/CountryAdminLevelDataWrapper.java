package org.grameen.fdp.kasapin.data.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.entity.District;

import java.util.List;

/**
 * Created by AangJnr on 09, December, 2018 @ 11:42 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class CountryAdminLevelDataWrapper {

    @SerializedName("data")
    @Expose
    private List<District> data;

    /**
     * No args constructor for use in serialization
     */
    public CountryAdminLevelDataWrapper() {
    }

    /**
     * @param data
     */
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