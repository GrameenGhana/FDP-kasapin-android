package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.grameen.fdp.kasapin.data.db.entity.Farmer;

import java.util.Date;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface FarmersDao extends BaseDao<Farmer> {
    @Query("SELECT * FROM farmers")
    Single<List<Farmer>> getAll();

    @Query("SELECT farmerName, code, syncStatus, lastModifiedDate, villageId, gender, imageLocalUrl, " +
            "lastVisitDate, educationLevel, hasSubmitted FROM farmers WHERE code IN (:codes)")
    Single<List<Farmer>> getAll(List<String> codes);

    @Query("SELECT farmerName, code, syncStatus, lastModifiedDate, villageId, gender, imageLocalUrl," +
            "lastVisitDate, educationLevel, hasSubmitted FROM farmers WHERE code = :farmerCode")
    Single<Farmer> getOne(String farmerCode);

    @Query("SELECT * FROM farmers WHERE syncStatus = '0'")
    Maybe<List<Farmer>> getAllNotSynced();

    @Query("SELECT * FROM farmers WHERE code = :code")
    Maybe<Farmer> get(String code);

    @Query("SELECT COUNT(syncStatus) FROM farmers")
    Maybe<Integer> checkIfUnSyncedFarmersAvailable();

    @Transaction
    @Query("UPDATE farmers SET syncStatus = 1 AND updatedAt = :updatedDate  WHERE code IN (:farmerCodes)")
    void setFarmersAsSynced(List<String> farmerCodes, Date updatedDate);

    @Query("SELECT COUNT(code) FROM farmers where code =:code")
    int checkIfFarmerExists(String code);
}
