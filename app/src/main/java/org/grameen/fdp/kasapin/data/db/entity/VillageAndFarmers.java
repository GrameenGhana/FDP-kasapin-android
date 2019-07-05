package org.grameen.fdp.kasapin.data.db.entity;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Created by AangJnr on 26, September, 2018 @ 12:29 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class VillageAndFarmers {
    @Embedded
    public Village village;


    @Relation(parentColumn = "id", entityColumn = "villageId", entity = RealFarmer.class)
    public List<RealFarmer> farmerList;


    public void setFarmerList(List<RealFarmer> farmerList) {
        this.farmerList = farmerList;
    }

    public void setVillage(Village village) {
        this.village = village;
    }

    public List<RealFarmer> getFarmerList() {
        return farmerList;
    }

    public Village getVillage() {
        return village;
    }
}
