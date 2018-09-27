package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.grameen.fdp.kasapin.ui.base.model.Loc;

import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by aangjnr on 08/11/2017.
 */

@Entity(tableName = "plots", indices = @Index("farmerId"), foreignKeys = @ForeignKey(entity = RealFarmer.class, parentColumns = "id", childColumns = "farmerId", onDelete = CASCADE))
public class Plot {

    @PrimaryKey
    @NonNull
    String id;

    String name;
    String area;
    String farmerId;
    String Count_plot__c;
    String distanceBetweenCocoaTrees__c;
    String EstimatedProduction__c;
    String farmBL__c;
    String farmerName__c;
    String numberOfShadeTrees__c;
    String FDP_submission__c;

    String plotAge__c;
    String plotArea__c;
    String plotGPS__c;
    String plotName__c;
    String Total_cost__c;
    String Total_input_cost__c;
    String Total_labor_cost__c;
    String ph;
    String lastVisitDate;
    String estimatedProductionSize;

    List<Loc> locList;

    public Plot() {

    }

    @Ignore
    public Plot(String id, String name, String area, String grade, String estimatedProductionSize, String lastVisitDate) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.ph = grade;
        this.lastVisitDate = lastVisitDate;
        this.estimatedProductionSize = estimatedProductionSize;
     }


    public void setLocList(List<Loc> locList) {
        this.locList = locList;
    }

    public List<Loc> getLocList() {
        return locList;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public String getEstimatedProductionSize() {
        return estimatedProductionSize;
    }

    public void setEstimatedProductionSize(String estimatedProductionSize) {
        this.estimatedProductionSize = estimatedProductionSize;
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

    public String getCount_plot__c() {
        return Count_plot__c;
    }

    public void setCount_plot__c(String count_plot__c) {
        Count_plot__c = count_plot__c;
    }

    public String getDistanceBetweenCocoaTrees__c() {
        return distanceBetweenCocoaTrees__c;
    }

    public void setDistanceBetweenCocoaTrees__c(String distanceBetweenCocoaTrees__c) {
        this.distanceBetweenCocoaTrees__c = distanceBetweenCocoaTrees__c;
    }

    public String getEstimatedProduction__c() {
        return EstimatedProduction__c;
    }

    public void setEstimatedProduction__c(String estimatedProduction__c) {
        EstimatedProduction__c = estimatedProduction__c;
    }

    public String getFarmBL__c() {
        return farmBL__c;
    }

    public void setFarmBL__c(String farmBL__c) {
        this.farmBL__c = farmBL__c;
    }

    public String getFarmerName__c() {
        return farmerName__c;
    }

    public void setFarmerName__c(String farmerName__c) {
        this.farmerName__c = farmerName__c;
    }

    public String getFDP_submission__c() {
        return FDP_submission__c;
    }

    public void setFDP_submission__c(String FDP_submission__c) {
        this.FDP_submission__c = FDP_submission__c;
    }

    public String getNumberOfShadeTrees__c() {
        return numberOfShadeTrees__c;
    }

    public void setNumberOfShadeTrees__c(String numberOfShadeTrees__c) {
        this.numberOfShadeTrees__c = numberOfShadeTrees__c;
    }

    public String getPlotAge__c() {
        return plotAge__c;
    }

    public void setPlotAge__c(String plotAge__c) {
        this.plotAge__c = plotAge__c;
    }

    public String getPlotArea__c() {
        return plotArea__c;
    }

    public void setPlotArea__c(String plotArea__c) {
        this.plotArea__c = plotArea__c;
    }

    public String getPlotGPS__c() {
        return plotGPS__c;
    }

    public void setPlotGPS__c(String plotGPS__c) {
        this.plotGPS__c = plotGPS__c;
    }

    public String getPlotName__c() {
        return plotName__c;
    }

    public void setPlotName__c(String plotName__c) {
        this.plotName__c = plotName__c;
    }

    public String getTotal_cost__c() {
        return Total_cost__c;
    }

    public void setTotal_cost__c(String total_cost__c) {
        Total_cost__c = total_cost__c;
    }

    public String getTotal_input_cost__c() {
        return Total_input_cost__c;
    }

    public void setTotal_input_cost__c(String total_input_cost__c) {
        Total_input_cost__c = total_input_cost__c;
    }

    public String getTotal_labor_cost__c() {
        return Total_labor_cost__c;
    }

    public void setTotal_labor_cost__c(String total_labor_cost__c) {
        Total_labor_cost__c = total_labor_cost__c;
    }
}
