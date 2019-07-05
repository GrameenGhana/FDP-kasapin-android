package org.grameen.fdp.kasapin.data.network.model;

import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.entity.Plot;

import java.util.List;

public class PlotDetails {

    @SerializedName("plot_info")
    Plot plot;



    public PlotDetails(){}


    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    public Plot getPlot() {
        return plot;
    }
}
