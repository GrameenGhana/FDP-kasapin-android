package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by aangjnr on 15/02/2018.
 */

@Entity(tableName = "farm_results")
public class FarmResult {
    @PrimaryKey
    @NonNull
    String id;
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

    public void setPlotAssessmentList(List<PlotAssessment> plotAssessmentList) {
        this.plotAssessmentList = plotAssessmentList;
    }

    public List<PlotAssessment> getPlotAssessmentList() {
        return plotAssessmentList;
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
