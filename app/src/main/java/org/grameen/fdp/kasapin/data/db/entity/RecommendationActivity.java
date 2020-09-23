package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "recommendation_activities",
        indices = {@Index("recommendationId"), @Index("activityId")})

public class RecommendationActivity extends BaseModel {
    @SerializedName("recommendation_id")
    int recommendationId;

    @SerializedName("activity_id")
    int activityId;

    @SerializedName("activity_translation_c")
    String activityTranslation;

    @SerializedName("month_c")
    String month;

    @SerializedName("image_id")
    String imageId;

    @SerializedName("year_c")
    int year;

    @SerializedName("seasonal_c")
    int seasonal;

    @SerializedName("labor_days_c")
    double laborDays;

    @SerializedName("labor_cost_c")
    double laborCost;

    @SerializedName("supplies_cost_c")
    double suppliesCost;

    public RecommendationActivity() {
    }

    public double getLaborDays() {
        return laborDays;
    }

    public void setLaborDays(double laborDays) {
        this.laborDays = laborDays;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getActivityTranslation() {
        return activityTranslation;
    }

    public void setActivityTranslation(String activityTranslation) {
        this.activityTranslation = activityTranslation;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeasonal() {
        return seasonal;
    }

    public void setSeasonal(int seasonal) {
        this.seasonal = seasonal;
    }

    public double getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(double laborCost) {
        this.laborCost = laborCost;
    }

    public double getSuppliesCost() {
        return suppliesCost;
    }

    public void setSuppliesCost(double suppliesCost) {
        this.suppliesCost = suppliesCost;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
         return imageId != null ? imageId.replace(".png", "") : "n/a";
    }
}
