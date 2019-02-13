package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Plot;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:25 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface PlotsDao extends BaseDao<Plot>{




    @Transaction
    @Query("SELECT * FROM plots WHERE farmerCode = :farmerCode")
    Single <List<Plot>> getFarmersPlots(String farmerCode);


    @Query("SELECT * FROM plots WHERE id = :id")
    Plot getPlotById(String id);


    @Query("DELETE FROM plots")
    void deleteAllPlots();


    @Query("DELETE FROM plots WHERE farmerCode = :farmerCode")
    int deleteFarmersPlotsByFarmerCode(String farmerCode);





}
