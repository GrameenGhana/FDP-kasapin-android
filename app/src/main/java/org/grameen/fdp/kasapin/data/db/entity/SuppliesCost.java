package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.renderscript.ScriptC;
import android.support.annotation.NonNull;

/**
 * Created by aangjnr on 17/03/2018.
 */

@Entity(tableName = "supplies_costs")

public class SuppliesCost {


    @PrimaryKey
    @NonNull
    String id;
    String cost;



    public SuppliesCost(){}


    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getCost() {
        return cost;
    }
}
