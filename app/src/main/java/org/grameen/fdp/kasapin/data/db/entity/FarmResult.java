package org.grameen.fdp.kasapin.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "farm_results")
public class FarmResult {
    @PrimaryKey
    @NonNull
    String id = "";
    String caption;
    String status;
    String plotAssessmentId;

    @Ignore
    List<PlotAssessment> plotAssessmentList;
    public FarmResult() {
    }
    public String getPlotAssessmentId() {
        return plotAssessmentId;
    }

    public void setPlotAssessmentId(String plotAssessmentId) {
        this.plotAssessmentId = plotAssessmentId;
    }

    public List<PlotAssessment> getPlotAssessmentList() {
        return plotAssessmentList;
    }

    public void setPlotAssessmentList(List<PlotAssessment> plotAssessmentList) {
        this.plotAssessmentList = plotAssessmentList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }
}
