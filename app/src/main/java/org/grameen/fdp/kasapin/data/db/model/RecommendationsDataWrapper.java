package org.grameen.fdp.kasapin.data.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.entity.Recommendation;

import java.util.List;

/**
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class RecommendationsDataWrapper {

    @SerializedName("data")
    @Expose
    private List<Recommendation> data = null;

    /**
     * No args constructor for use in serialization
     */
    public RecommendationsDataWrapper() {
    }

    /**
     * @param recommendations Recommendation data from the server
     */
    public RecommendationsDataWrapper(List<Recommendation> recommendations) {
        super();
        this.data = data;
    }

    public List<Recommendation> getData() {
        return data;
    }

    public void setData(List<Recommendation> data) {
        this.data = data;
    }
}