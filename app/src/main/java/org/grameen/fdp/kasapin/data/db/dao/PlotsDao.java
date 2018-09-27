package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Plot;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:25 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface PlotsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Plot> objects);

    @Query("SELECT * FROM plots")
    List<Plot> getAllPlots();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlot(Plot plot);


    @Query("SELECT * FROM plots WHERE id = :id")
    Plot getPlotById(String id);


    @Update
    int updatePlot(Plot plot);



    @Query("DELETE FROM plots")
    void deleteAllPlots();


    @Query("DELETE FROM plots WHERE id = :id")
    int deletePlotById(String id);


    @Query("DELETE FROM plots WHERE farmerId = :farmerId")
    int deleteFarmersPlotsByFarmerCode(String farmerId);





}
