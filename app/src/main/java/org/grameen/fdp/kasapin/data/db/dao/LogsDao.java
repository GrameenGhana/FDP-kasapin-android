package org.grameen.fdp.kasapin.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.Logs;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface LogsDao extends BaseDao<Logs> {
    @Query("SELECT * FROM logs WHERE farmerCode = :farmerCode")
    Maybe<Logs> getAllLogsForFarmer(String farmerCode);

    @Query("DELETE FROM logs WHERE  farmerCode IN (:farmerCodes)")
    void deleteFarmerLogs(List<String> farmerCodes);

}
