package org.grameen.fdp.kasapin.data.db.entity;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Created by AangJnr on 20, September, 2018 @ 5:53 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class PlotAndAssessments {

    @Embedded
    public Plot plot;

    @Relation(parentColumn = "id", entityColumn = "plotId", entity = PlotAssessment.class)
    public List<PlotAssessment> assessmentList;


}
