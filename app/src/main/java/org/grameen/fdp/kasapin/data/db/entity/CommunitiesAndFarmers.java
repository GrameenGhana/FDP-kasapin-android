package org.grameen.fdp.kasapin.data.db.entity;


import androidx.room.Embedded;
import androidx.room.Relation;

import org.grameen.fdp.kasapin.data.db.model.FarmerNameAndCode;

import java.util.List;

public class CommunitiesAndFarmers {
    @Embedded
    public Community village;

    @Relation(parentColumn = "id", entityColumn = "villageId", entity = Farmer.class, projection = {"code", "farmerName"})
    public List<FarmerNameAndCode> farmerList;

    public List<FarmerNameAndCode> getFarmerList() {
        return farmerList;
    }

    public void setFarmerList(List<FarmerNameAndCode> farmerList) {
        this.farmerList = farmerList;
    }

    public Community getVillage() {
        return village;
    }

    public void setVillage(Community village) {
        this.village = village;
    }

}
