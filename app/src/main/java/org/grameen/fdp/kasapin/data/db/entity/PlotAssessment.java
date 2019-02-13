package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by aangjnr on 15/02/2018.
 */

@Entity(tableName = "plot_assessments" )
public class PlotAssessment {

    @PrimaryKey
    @NonNull
    String id;
    String plotName;
    int plotId;
    String results;
    Integer color;


    public PlotAssessment(){}

    @Ignore
    public PlotAssessment(String name, String result){this.plotName = name; this.results = result;}


    public void setPlotId(int plotId) {
        this.plotId = plotId;
    }

    public int getPlotId() {
        return plotId;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public void setResults(String results) {
        this.results = results;
    }


    public String getResults() {
        return results;
    }


    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getColor() {
        return color;
    }

    public String getPlotName() {
        return plotName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
