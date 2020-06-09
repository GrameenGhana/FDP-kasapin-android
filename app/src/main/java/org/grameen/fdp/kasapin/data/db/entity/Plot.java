package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

/**
 * Created by aangjnr on 08/11/2017.far
 */
@Entity(tableName = "plots", indices = {@Index("farmerCode"), @Index(value = "externalId", unique = true)})
public class Plot {
    @PrimaryKey(autoGenerate = true)
    int id;
    @SerializedName("external_id_c")
    private String externalId;
    private String distanceBetweenCocoaTrees;
    private String farmerName;
    @SerializedName("farmer_code")
    private String farmerCode;
    private String numberOfShadeTrees = null;
    @SerializedName("age_c")
    private String plotAge = "0";
    @SerializedName("area_c")
    private String area = null;
    @SerializedName("plot_gps_points")
    private String plotPoints;
    @SerializedName("name_c")
    private String name;
    @SerializedName("ph_c")
    private String ph;
    private String lastVisitDate;
    @SerializedName("estimated_production_c")
    private String estimatedProductionSize = null;
    @SerializedName("data")
    private String answersData;
    @SerializedName("recommendation_id")
    private int recommendationId;
    private int gapsId = 1;
    @SerializedName("start_year")
    private int startYear = 1;
    @Ignore
    @SerializedName("monitoring_list")
    private List<Monitoring> monitoringList;

    public Plot() {
    }

    @Ignore
    public List<PlotGpsPoint> getGpsPoints() {
        return getGson().fromJson(plotPoints, new TypeToken<List<PlotGpsPoint>>() {
        }.getType());
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getGapsId() {
        return gapsId;
    }

    public void setGapsId(int gapsId) {
        this.gapsId = gapsId;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(int recommendation_id) {
        this.recommendationId = recommendation_id;
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

    public List<Monitoring> getMonitoringList() {
        return monitoringList;
    }

    public void setMonitoringList(List<Monitoring> monitoringList) {
        this.monitoringList = monitoringList;
    }
}
