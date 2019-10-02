package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Created by aangjnr on 04/01/2018.
 */


@Entity(tableName = "calculations", indices = {@Index(value = "id", unique = true), @Index(value = "recommendationId")},
        foreignKeys = {@ForeignKey(entity = Recommendation.class, parentColumns = "id", childColumns = "recommendationId", onDelete = CASCADE)})
public class Calculation extends BaseModel {

    @SerializedName("recommendation_id")
    int recommendationId;
    @SerializedName("year_c")
    int year;
    @SerializedName("type_c")
    String type;
    @SerializedName("formula_c")
    String formula;


    public Calculation() {
    }


    public int getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
