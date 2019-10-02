package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.FarmResult;

import java.util.List;

/**
 * Created by AangJnr on 17, September, 2018 @ 8:40 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface FarmResultsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FarmResult> objects);

    @Query("SELECT * FROM farm_results")
    List<FarmResult> getAllFarmResults();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFarmResult(FarmResult farmResult);


    @Query("SELECT * FROM farm_results WHERE id = :id")
    FarmResult getFarmResultById(String id);


    @Update
    int updateFarmResult(FarmResult farmResult);


    @Query("DELETE FROM farm_results")
    void deleteAllFarmResult();


    @Query("DELETE FROM farm_results WHERE id = :id")
    int deleteFarmResultById(String id);


}
