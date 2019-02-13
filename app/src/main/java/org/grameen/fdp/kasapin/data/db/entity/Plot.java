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

@Entity(tableName = "plots", indices = @Index("farmerCode"), foreignKeys = @ForeignKey(entity = RealFarmer.class, parentColumns = "code", childColumns = "farmerCode", onDelete = CASCADE))
public class Plot {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    String distanceBetweenCocoaTrees;
    String EstimatedProduction;
    String farmerName;
    String farmerCode;
    String numberOfShadeTrees;
    String plotAge;
    String area;
    String plotPoints;
    String name;
    String ph;
    String lastVisitDate;
    String estimatedProductionSize;
    String answersData;

    String gpsPoints;



    public Plot() {

    }




    public String getEstimatedProductionSize() {
        return estimatedProductionSize;
    }

    public void setEstimatedProductionSize(String estimatedProductionSize) {
        this.estimatedProductionSize = estimatedProductionSize;
    }

    public void setFarmerCode(String farmerCode) {
        this.farmerCode = farmerCode;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public void setDistanceBetweenCocoaTrees(String distanceBetweenCocoaTrees) {
        this.distanceBetweenCocoaTrees = distanceBetweenCocoaTrees;
    }

    public void setEstimatedProduction(String estimatedProduction) {
        EstimatedProduction = estimatedProduction;
    }

    public void setNumberOfShadeTrees(String numberOfShadeTrees) {
        this.numberOfShadeTrees = numberOfShadeTrees;
    }

    public void setPlotAge(String plotAge) {
        this.plotAge = plotAge;
    }

    public void setPlotPoints(String plotPoints) {
        this.plotPoints = plotPoints;
    }

    public String getFarmerCode() {
        return farmerCode;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public String getDistanceBetweenCocoaTrees() {
        return distanceBetweenCocoaTrees;
    }

    public String getEstimatedProduction() {
        return EstimatedProduction;
    }

    public String getNumberOfShadeTrees() {
        return numberOfShadeTrees;
    }

    public String getPlotAge() {
        return plotAge;
    }

    public String getPlotPoints() {
        return plotPoints;
    }


    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public int getId() {
        return id;
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

    public void setAnswersData(String answersData) {
        this.answersData = answersData;
    }

    public String getAnswersData() {
        return answersData;
    }

    public void setGpsPoints(String gpsPoints) {
        this.gpsPoints = gpsPoints;
    }

    public String getGpsPoints() {
        return gpsPoints;
    }
}
