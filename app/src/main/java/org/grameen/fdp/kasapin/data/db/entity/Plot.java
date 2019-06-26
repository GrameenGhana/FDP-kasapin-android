package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aangjnr on 08/11/2017.
 */
//, foreignKeys = @ForeignKey(entity = RealFarmer.class, parentColumns = "code", childColumns = "farmerCode", onDelete = CASCADE)
@Entity(tableName = "plots", indices = {@Index("farmerCode"), @Index(value = "externalId", unique = true)})
public class Plot {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    @SerializedName("external_id_c")
    String externalId;
    String distanceBetweenCocoaTrees;
    @SerializedName("estimated_production_c")
    String EstimatedProduction;
    String farmerName;
    @SerializedName("farmer_code")
    String farmerCode;
    String numberOfShadeTrees;
    @SerializedName("age_c")
    String plotAge = "0";
    @SerializedName("area_c")
    String area;
    String plotPoints;
    @SerializedName("name_c")
    String name;

    @SerializedName("ph_c")
    String ph;

    String lastVisitDate;
    String estimatedProductionSize;

    @SerializedName("data")
    String answersData;
    String gpsPoints;
    @SerializedName("recommendation_id")
    int recommendationId = -1;

    int gapsId = -1;
    int startYear = 1;

    public Plot() {

    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getEstimatedProductionSize() {
        return estimatedProductionSize;
    }

    public void setEstimatedProductionSize(String estimatedProductionSize) {
        this.estimatedProductionSize = estimatedProductionSize;
    }

    public String getFarmerCode() {
        return farmerCode;
    }

    public void setFarmerCode(String farmerCode) {
        this.farmerCode = farmerCode;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getDistanceBetweenCocoaTrees() {
        return distanceBetweenCocoaTrees;
    }

    public void setDistanceBetweenCocoaTrees(String distanceBetweenCocoaTrees) {
        this.distanceBetweenCocoaTrees = distanceBetweenCocoaTrees;
    }

    public String getEstimatedProduction() {
        return EstimatedProduction;
    }

    public void setEstimatedProduction(String estimatedProduction) {
        EstimatedProduction = estimatedProduction;
    }

    public String getNumberOfShadeTrees() {
        return numberOfShadeTrees;
    }

    public void setNumberOfShadeTrees(String numberOfShadeTrees) {
        this.numberOfShadeTrees = numberOfShadeTrees;
    }

    public String getPlotAge() {
        return (plotAge == null) ? "" : plotAge;
    }

    public void setPlotAge(String plotAge) {
        this.plotAge = plotAge;
    }

    public String getPlotPoints() {
        return plotPoints;
    }

    public void setPlotPoints(String plotPoints) {
        this.plotPoints = plotPoints;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(String lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAnswersData() {
        return answersData;
    }

    public void setAnswersData(String answersData) {
        this.answersData = answersData;
    }

    public String getGpsPoints() {
        return gpsPoints;
    }

    public void setGpsPoints(String gpsPoints) {
        this.gpsPoints = gpsPoints;
    }

    public int getGapsId() {
        return gapsId;
    }

    public void setGapsId(int gapsId) {
        this.gapsId = gapsId;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    @Ignore
    public JSONObject getAOJsonData() throws JSONException {
        return new JSONObject(answersData);
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }
}
