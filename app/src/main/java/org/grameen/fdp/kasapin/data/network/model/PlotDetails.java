package org.grameen.fdp.kasapin.data.network.model;

import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.entity.Plot;

public class PlotDetails {

    @SerializedName("plot_info")
    Plot plot;


    public PlotDetails() {
    }

    public Plot getPlot() {
        return plot;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }
}
