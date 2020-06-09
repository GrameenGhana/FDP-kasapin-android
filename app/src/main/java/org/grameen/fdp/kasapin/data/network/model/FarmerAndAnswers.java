package org.grameen.fdp.kasapin.data.network.model;

import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;

import java.util.List;

public class FarmerAndAnswers {

    @SerializedName("farmer")
    RealFarmer farmer;


    @SerializedName("forms")
    List<FormAnswerData> answers;

    @SerializedName("plot_details")
    List<List<Plot>> plotDetails;


    public FarmerAndAnswers() {
    }

    public List<FormAnswerData> getAnswers() {
        return answers;
    }

    public void setAnswers(List<FormAnswerData> answers) {
        this.answers = answers;
    }

    public RealFarmer getFarmer() {
        return farmer;
    }

    public void setFarmer(RealFarmer farmer) {
        this.farmer = farmer;
    }

    public List<List<Plot>> getPlotDetails() {
        return plotDetails;
    }

    public void setPlotDetails(List<List<Plot>> plotDetails) {
        this.plotDetails = plotDetails;
    }
}
