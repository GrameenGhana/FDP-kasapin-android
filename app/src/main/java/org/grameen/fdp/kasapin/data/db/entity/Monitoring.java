package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by aangjnr on 08/02/2018.
 */

@Entity(tableName = "monitorings", indices = @Index("plotId"), foreignKeys = @ForeignKey(entity = Plot.class, parentColumns = "id", childColumns = "plotId", onDelete = CASCADE))
public class Monitoring {
    @PrimaryKey
    @NonNull
    String id;

    String year;
    String name;
    String json;
    String plotId;



    public Monitoring(){}

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getJson() {
        return json;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setPlotId(String plotId) {
        this.plotId = plotId;
    }

    public String getPlotId() {
        return plotId;
    }




}
