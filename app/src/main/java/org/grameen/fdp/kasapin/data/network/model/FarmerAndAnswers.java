package org.grameen.fdp.kasapin.data.network.model;

import com.crashlytics.android.answers.Answers;
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
    List<Plot> plots;


    public FarmerAndAnswers(){}


    public void setFarmer(RealFarmer farmer) {
        this.farmer = farmer;
    }

    public void setAnswers(List<FormAnswerData> answers) {
        this.answers = answers;
    }

    public List<FormAnswerData> getAnswers() {
        return answers;
    }

    public RealFarmer getFarmer() {
        return farmer;
    }

    public void setPlots(List<Plot> plots) {
        this.plots = plots;
    }

    public List<Plot> getPlots() {
        return plots;
    }
}
