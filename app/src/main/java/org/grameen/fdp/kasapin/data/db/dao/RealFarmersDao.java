package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:44 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface RealFarmersDao extends BaseDao<RealFarmer> {


    @Query("SELECT * FROM farmers")
    Single<List<RealFarmer>> getAll();

    @Query("SELECT * FROM farmers WHERE syncStatus = '0'")
    Maybe<List<RealFarmer>> getAllNotSynced();

    @Query("SELECT * FROM farmers WHERE id = :id")
    RealFarmer get(int id);

    @Query("SELECT * FROM farmers WHERE code = :code")
    Maybe<RealFarmer> get(String code);

    @Update
    int updateFarmer(RealFarmer farmer);

    @Query("DELETE FROM farmers")
    void deleteAllFarmers();

    @Query("DELETE FROM farmers WHERE id = :id")
    int deleteFarmerById(int id);


    @Query("SELECT COUNT(syncStatus) FROM farmers")
    Maybe<Integer> checkIfUnsyncedAvailable();

    @Query("SELECT COUNT(id) FROM farmers where code =:code")
    int checkIfFarmerExists(String code);

}
