package org.grameen.fdp.kasapin.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "plot_assessments")
public class PlotAssessment {
    @PrimaryKey
    @NonNull
    String id = "";
    String plotName;
    int plotId;
    String results;
    Integer color;

    public PlotAssessment() {
    }

    @Ignore
    public PlotAssessment(String name, String result) {
        this.plotName = name;
        this.results = result;
    }

    public int getPlotId() {
        return plotId;
    }

    public void setPlotId(int plotId) {
        this.plotId = plotId;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }
}
