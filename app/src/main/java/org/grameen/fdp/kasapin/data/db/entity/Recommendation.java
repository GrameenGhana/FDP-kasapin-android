package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "recommendations", indices = {@Index(value = "id", unique = true), @Index(value = "country"), @Index(value = "cropId")})
public class Recommendation extends BaseModel {
    @SerializedName("crop_id")
    int cropId;
    @SerializedName("label")
    String label;
    @SerializedName("reco_name")
    String recommendationName;
    int hierarchy;
    @SerializedName("condition")
    String condition;
    @SerializedName("change_condition")
    String changeCondition;
    @SerializedName("change_option")
    String changeOption;
    int country;
    @Ignore
    List<Calculation> calculations;
    @Ignore
    @SerializedName("recommendation_activity")
    List<RecommendationActivity> recommendationActivities;

    public Recommendation() {
    }

    public String getRecommendationName() {
        return recommendationName;
    }

    public void setRecommendationName(String recommendationName) {
        this.recommendationName = recommendationName;
    }

    public int getCropId() {
        return cropId;
    }

    public void setCropId(int cropId) {
        this.cropId = cropId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getChangeCondition() {
        return changeCondition;
    }

    public void setChangeCondition(String changeCondition) {
        this.changeCondition = changeCondition;
    }

    public String getChangeOption() {
        return changeOption;
    }

    public void setChangeOption(String changeOption) {
        this.changeOption = changeOption;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public List<Calculation> getCalculations() {
        return calculations;
    }

    public void setCalculations(List<Calculation> calculations) {
        this.calculations = calculations;
    }

    public List<RecommendationActivity> getRecommendationActivities() {
        return recommendationActivities;
    }

    public void setRecommendationActivities(List<RecommendationActivity> recommendationActivities) {
        this.recommendationActivities = recommendationActivities;
    }
}
