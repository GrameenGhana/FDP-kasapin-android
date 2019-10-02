package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by aangjnr on 17/03/2018.
 */

@Entity(tableName = "supplies_costs")

public class SuppliesCost {


    @PrimaryKey
    @NonNull
    String id;
    String cost;


    public SuppliesCost() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
