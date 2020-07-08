package org.grameen.fdp.kasapin.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "activities_plus_inputs")
public class ActivitiesPlusInput {
    @SerializedName("LastModifiedDate")
    String lastModifiedDate;

    @PrimaryKey
    @NonNull
    @SerializedName("Id")
    String id = "";

    @SerializedName("Name")
    String name;

    @SerializedName("Input_type_c")
    String inputType;

    @SerializedName("Quantity_c")
    String quantity;

    @SerializedName("Input_c")
    String inputId;

    @SerializedName("Cost_c")
    String totalCost;

    @SerializedName("Recommendation_c")
    String recommendationId;

    @SerializedName("reco__c")
    String recommendationName;

    public ActivitiesPlusInput() {
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(String recommendationId) {
        this.recommendationId = recommendationId;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getInputId() {
        return inputId;
    }

    public void setInputId(String inputUnitCost) {
        this.inputId = inputUnitCost;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRecommendationName() {
        return recommendationName;
    }

    public void setRecommendationName(String recommendationName) {
        this.recommendationName = recommendationName;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }
}
