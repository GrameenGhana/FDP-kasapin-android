package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.grameen.fdp.kasapin.data.db.entity.PlotAndAssessments;

/**
 * Created by AangJnr on 20, September, 2018 @ 5:57 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface PlotAndAssessmentsDao {

    @Transaction
    @Query("SELECT * FROM plots WHERE id = :id")
    PlotAndAssessments loadPlotAssessments(String id);

}
