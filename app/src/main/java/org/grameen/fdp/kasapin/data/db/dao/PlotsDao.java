package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.grameen.fdp.kasapin.data.db.entity.Plot;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PlotsDao extends BaseDao<Plot> {
    @Transaction
    @Query("SELECT * FROM plots WHERE farmerCode = :farmerCode")
    Single<List<Plot>> getFarmersPlots(String farmerCode);

    @Query("SELECT * FROM plots WHERE id = :id")
    Plot getPlotById(String id);

    @Query("DELETE FROM plots WHERE externalId = :id")
    void deleteOne(String id);

    @Query("DELETE FROM plots")
    void deleteAllPlots();

    @Query("DELETE FROM plots WHERE farmerCode = :farmerCode")
    int deleteFarmersPlotsByFarmerCode(String farmerCode);
}
