package org.grameen.fdp.kasapin.data.db.entity;


import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PlotAndAssessments {
    @Embedded
    public Plot plot;

    @Relation(parentColumn = "id", entityColumn = "plotExternalId", entity = PlotAssessment.class)
    public List<PlotAssessment> assessmentList;
}
