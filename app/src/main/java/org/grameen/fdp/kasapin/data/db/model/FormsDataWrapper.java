package org.grameen.fdp.kasapin.data.db.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.entity.FormTranslation;

import java.util.List;

public class FormsDataWrapper {
    @SerializedName("data")
    @Expose
    private List<FormTranslation> data = null;
    /**
     * No args constructor for use in serialization
     */
    public FormsDataWrapper() {
    }
    public FormsDataWrapper(List<FormTranslation> data) {
        super();
        this.data = data;
    }

    public List<FormTranslation> getData() {
        return data;
    }

    public void setData(List<FormTranslation> data) {
        this.data = data;
    }
}