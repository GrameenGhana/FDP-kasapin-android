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

    @Transaction
    @Query("SELECT * FROM plots WHERE farmerCode = :farmerCode AND externalId =:plotExternalId")
    Single<Plot> getPlot(String farmerCode, String plotExternalId);

    @Query("DELETE FROM plots WHERE externalId = :id")
    void deleteOne(String id);
}
