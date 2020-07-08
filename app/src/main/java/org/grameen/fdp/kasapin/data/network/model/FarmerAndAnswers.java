package org.grameen.fdp.kasapin.data.network.model;

import com.google.gson.annotations.SerializedName;

import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;

import java.util.List;

public class FarmerAndAnswers {
    @SerializedName("farmer")
   private Farmer farmer;

    @SerializedName("forms")
    private List<FormAnswerData> answers;

    @SerializedName("plot_details")
   private List<List<Plot>> plotDetails;


    public FarmerAndAnswers() {
    }

    public List<FormAnswerData> getAnswers() {
        return answers;
    }

    public void setAnswers(List<FormAnswerData> answers) {
        this.answers = answers;
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }

    public List<List<Plot>> getPlotDetails() {
        return plotDetails;
    }

    public void setPlotDetails(List<List<Plot>> plotDetails) {
        this.plotDetails = plotDetails;
    }
}
