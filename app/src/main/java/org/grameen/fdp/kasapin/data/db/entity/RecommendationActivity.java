package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by aangjnr on 17/01/2018.
 */

@Entity(tableName = "recommendation_activities",
        indices = {@Index("recommendationId"), @Index("activityId") })

public class RecommendationActivity extends BaseModel{


    @SerializedName("recommendation_id")
    int recommendationId;

    @SerializedName("activity_id")
    int activityId;

    @SerializedName("activity_translation_c")
    String activityTranslation;

    @SerializedName("month_c")
    String month;

    @SerializedName("year_c")
    int year;

    @SerializedName("seasonal_c")
    int seasonal;


    @SerializedName("labor_days_c")
    double laborDays;

    public RecommendationActivity() {
    }


    public void setLaborDays(double laborDays) {
        this.laborDays = laborDays;
    }

    public double getLaborDays() {
        return laborDays;
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
}
