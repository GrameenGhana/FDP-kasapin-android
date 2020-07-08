package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Farmer;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface RealFarmersDao extends BaseDao<Farmer> {
    @Query("SELECT * FROM farmers")
    Single<List<Farmer>> getAll();

    @Query("SELECT * FROM farmers WHERE syncStatus = '0'")
    Maybe<List<Farmer>> getAllNotSynced();

    @Query("SELECT * FROM farmers WHERE id = :id")
    Farmer get(int id);

    @Query("SELECT * FROM farmers WHERE code = :code")
    Maybe<Farmer> get(String code);

    @Update
    int updateFarmer(Farmer farmer);

    @Query("DELETE FROM farmers")
    void deleteAllFarmers();

    @Query("DELETE FROM farmers WHERE id = :id")
    int deleteFarmerById(int id);

    @Query("SELECT COUNT(syncStatus) FROM farmers")
    Maybe<Integer> checkIfUnsyncedFarmersAvailable();

    @Query("SELECT COUNT(id) FROM farmers where code =:code")
    int checkIfFarmerExists(String code);

}
