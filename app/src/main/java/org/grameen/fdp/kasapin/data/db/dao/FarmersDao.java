package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.Farmer;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface FarmersDao extends BaseDao<Farmer> {
    @Query("SELECT * FROM farmers")
    Single<List<Farmer>> getAll();

    @Query("SELECT farmerName, code, syncStatus, lastModifiedDate, id, villageId, gender, " +
            "lastVisitDate, educationLevel, hasSubmitted FROM farmers WHERE code IN (:codes)")
    Single<List<Farmer>> getAll(List<String> codes);

    @Query("SELECT farmerName, code, syncStatus, lastModifiedDate, id, villageId, gender, " +
            "lastVisitDate, educationLevel, hasSubmitted FROM farmers WHERE code = :farmerCode")
    Single<Farmer> getOne(String farmerCode);

    @Query("SELECT * FROM farmers WHERE syncStatus = '0'")
    Maybe<List<Farmer>> getAllNotSynced();

    @Query("SELECT * FROM farmers WHERE id = :id")
    Farmer get(int id);

    @Query("SELECT * FROM farmers WHERE code = :code")
    Maybe<Farmer> get(String code);

    @Query("SELECT COUNT(syncStatus) FROM farmers")
    Maybe<Integer> checkIfUnsyncedFarmersAvailable();

    @Query("UPDATE farmers SET syncStatus = 1 WHERE code IN (:farmerCodes)")
    void setFarmersAsSynced(List<String> farmerCodes);

    @Query("SELECT COUNT(id) FROM farmers where code =:code")
    int checkIfFarmerExists(String code);
}
