package org.grameen.fdp.kasapin.data.db.entity;


import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CommunitiesAndFarmers {
    @Embedded
    public Community village;

    @Relation(parentColumn = "id", entityColumn = "villageId", entity = Farmer.class)
    public List<Farmer> farmerList;

    public List<Farmer> getFarmerList() {
        return farmerList;
    }

    public void setFarmerList(List<Farmer> farmerList) {
        this.farmerList = farmerList;
    }

    public Community getVillage() {
        return village;
    }

    public void setVillage(Community village) {
        this.village = village;
    }

}
