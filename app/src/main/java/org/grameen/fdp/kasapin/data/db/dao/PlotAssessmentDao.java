package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.PlotAssessment;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:32 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface PlotAssessmentDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlotAssessment> objects);

    @Query("SELECT * FROM plot_assessments")
    List<PlotAssessment> getAllPlotAssessments();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlotAssessment(PlotAssessment plotAssessment);


    @Query("SELECT * FROM plot_assessments WHERE id = :id")
    PlotAssessment getPlotAssessmentById(String id);


    @Update
    int updatePlotAssessment(PlotAssessment plotAssessment);



    @Query("DELETE FROM plot_assessments")
    void deleteAllPlotAssessments();


    @Query("DELETE FROM plot_assessments WHERE id = :id")
    int deletePlotAssessmentById(String id);




}
